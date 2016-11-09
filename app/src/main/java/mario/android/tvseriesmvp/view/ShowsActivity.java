package mario.android.tvseriesmvp.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import mario.android.tvseriesmvp.R;
import mario.android.tvseriesmvp.ShowsAdapter;
import mario.android.tvseriesmvp.model.show.Show;
import mario.android.tvseriesmvp.model.show.TVShow;
import mario.android.tvseriesmvp.presenter.ShowsPresenter;

public class ShowsActivity extends AppCompatActivity implements ShowsMvpView {

    private ShowsPresenter mShowsPresenter;

    private RecyclerView mShowsRecyclerView;
    private Toolbar mToolbar;
    private EditText mSearchEditText;
    private ProgressBar mProgressBar;
    private TextView mInfoTextView;
    private ImageButton mSearchButton;

    private Realm realm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shows);

        realm = Realm.getDefaultInstance();

        //Set up presenter
        mShowsPresenter = new ShowsPresenter();
        mShowsPresenter.attachView(this);

        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mInfoTextView = (TextView) findViewById(R.id.text_info);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mShowsRecyclerView = (RecyclerView) findViewById(R.id.shows_recycler_view);
        setupRecyclerView(mShowsRecyclerView);

        mSearchEditText = (EditText) findViewById(R.id.edit_text_show_name);

        mSearchButton = (ImageButton) findViewById(R.id.button_search);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mShowsPresenter.loadShows(mSearchEditText.getText().toString());
            }
        });

        mSearchEditText.addTextChangedListener(mHideShowButtonTextWatcher);
        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    mShowsPresenter.loadShows(mSearchEditText.getText().toString());
                    return true;
                }else {
                    return false;
                }
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mShowsPresenter.detachView();
    }

    private void setupRecyclerView(RecyclerView showsRecyclerView) {
        final ShowsAdapter adapter = new ShowsAdapter();
        adapter.setCallback(new ShowsAdapter.Callback() {
            @Override
            public void onItemClick(TVShow tvShow) {
                startActivity(EpisodesActivity.newIntent(ShowsActivity.this, tvShow.getShow().getId(),
                        tvShow.getShow().getName(), tvShow.getShow().getUrl()));
            }
        });

        adapter.setLikeCallback(new ShowsAdapter.LikeCallback() {
            @Override
            public void onLikeClick(final TVShow tvShow, int position) {

                // TODO This code work but I do not think this is a best way to do it

                if (tvShow.getShow().isLiked()) {
                    // we set like to false and delete Show from DB
                    adapter.setShowLike(position, false);

                    final Show resultUnlike = realm.where(Show.class)
                            .equalTo("id", tvShow.getShow().getId())
                            .findFirst();

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            resultUnlike.deleteFromRealm();
                        }
                    });
                } else {

                    // we set like to true and add Show to DB
                    adapter.setShowLike(position, true);

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Show showDb = realm.createObject(Show.class); // Create a new object
                            showDb.setName(tvShow.getShow().getName());
                            showDb.setId(tvShow.getShow().getId());
                        }
                    });
                }
                adapter.notifyDataSetChanged();
            }
        });

        showsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        showsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void showTVShows(List<TVShow> tvShowList) {

        // TODO This code work but I do not think this is a best way to do it

        // find all shows with "like" in DB
        RealmQuery<Show> queryLikes = realm.where(Show.class);
        RealmResults<Show> result = queryLikes.findAll();

        // compare list from API with list from DB
        for (Show showInDb : result) {
            for (int i = 0; i < tvShowList.size(); i++) {
                if (showInDb.getId().equals(tvShowList.get(i).getShow().getId())) {
                    // set liked to true so we can use this data in adapter
                    tvShowList.get(i).getShow().setLiked(true);
                }
            }
        }

        ShowsAdapter adapter = (ShowsAdapter) mShowsRecyclerView.getAdapter();
        adapter.setShowsList(tvShowList);
        adapter.notifyDataSetChanged();
        mShowsRecyclerView.requestFocus();
        hideSoftKeyboard();
        mProgressBar.setVisibility(View.INVISIBLE);
        mInfoTextView.setVisibility(View.INVISIBLE);
        mShowsRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showMessage(int stringId) {
        mProgressBar.setVisibility(View.INVISIBLE);
        mInfoTextView.setVisibility(View.VISIBLE);
        mShowsRecyclerView.setVisibility(View.INVISIBLE);
        mInfoTextView.setText(getString(stringId));
    }

    @Override
    public void showProgressIndicator() {
        mProgressBar.setVisibility(View.VISIBLE);
        mInfoTextView.setVisibility(View.INVISIBLE);
        mShowsRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public Context getContext() {
        return this;
    }

    private TextWatcher mHideShowButtonTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            mSearchButton.setVisibility(charSequence.length() > 0 ? View.VISIBLE : View.GONE);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);
    }
}
