package mario.android.tvseriesmvp.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import mario.android.tvseriesmvp.EpisodesAdapter;
import mario.android.tvseriesmvp.R;
import mario.android.tvseriesmvp.model.episodes.Episodes;
import mario.android.tvseriesmvp.presenter.EpisodesPresenter;

/**
 * Created by Mario on 17.10.2016.
 */
public class EpisodesActivity extends AppCompatActivity implements EpisodesMvpView {

    private static final String EXTRA_SHOW_ID = "EXTRA_SHOW_ID";
    private static final String EXTRA_SHOW_NAME = "EXTRA_SHOW_NAME";
    private static final String EXTRA_SHOW_URL = "EXTRA_SHOW_URL";

    private EpisodesPresenter mEpisodesPresenter;

    private RecyclerView mEpisodesRecyclerView;
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private TextView mInfoTextView;

    private String tvShowId;
    private String tvShowName;
    private String tvShowUrl;


    private Realm realm;

    public static Intent newIntent(Context context, String showId, String showName, String showUrl){
        Intent intent = new Intent(context, EpisodesActivity.class);
        intent.putExtra(EXTRA_SHOW_ID, showId);
        intent.putExtra(EXTRA_SHOW_NAME, showName);
        intent.putExtra(EXTRA_SHOW_URL, showUrl);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episodes);

        realm = Realm.getDefaultInstance();

        mEpisodesPresenter = new EpisodesPresenter();
        mEpisodesPresenter.attachView(this);

        mToolbar = (Toolbar) findViewById(R.id.episodes_toolbar);
        mProgressBar = (ProgressBar) findViewById(R.id.episodes_loading_progress);
        mEpisodesRecyclerView = (RecyclerView) findViewById(R.id.episodes_recycler_view);
        mInfoTextView = (TextView) findViewById(R.id.episodes_text_info);

        setupRecyclerView(mEpisodesRecyclerView);

        tvShowId = getIntent().getStringExtra(EXTRA_SHOW_ID);
        tvShowName = getIntent().getStringExtra(EXTRA_SHOW_NAME);
        tvShowUrl = getIntent().getStringExtra(EXTRA_SHOW_URL);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(tvShowName);
        }

        mEpisodesPresenter.loadEpisodes(tvShowId);

    }

    @Override
    public Context getContext() {
        return this;
    }

    private void setupRecyclerView(RecyclerView episodesRecyclerView) {
        final EpisodesAdapter adapter = new EpisodesAdapter();

        // when user click on checkbox we add or delete data in DB
        adapter.setCheckedCallback(new EpisodesAdapter.CheckedCallback() {
            @Override
            public void onCheckBoxClick(final Episodes episode, int position) {

                // TODO This code work but I do not think this is a best way to do it

                if (episode.isWatched()) {
                    adapter.setEpisodeWatched(position, false);

                    final Episodes resultUnwatched = realm.where(Episodes.class)
                            .equalTo("id", episode.getId())
                            .findFirst();

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            resultUnwatched.deleteFromRealm();
                        }
                    });
                } else {

                    adapter.setEpisodeWatched(position, true);

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Episodes episodeDB = realm.createObject(Episodes.class);
                            episodeDB.setTvShowId(tvShowId);
                            episodeDB.setWatched(true);
                            episodeDB.setId(episode.getId());
                        }
                    });
                }
                adapter.notifyDataSetChanged();
            }
        });

        mEpisodesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mEpisodesRecyclerView.setAdapter(adapter);
    }

    @Override
    public void showEpisodes(List<Episodes> episodesList) {

        // TODO This code work but I do not think this is a best way to do it

        RealmResults<Episodes> episodesRealmResults = realm.where(Episodes.class)
                .equalTo("tvShowId", tvShowId)
                .findAll();

            for (Episodes episodeInDb : episodesRealmResults) {
                for (int i = 0; i < episodesList.size(); i++) {
                    if (episodeInDb.getId().equals(episodesList.get(i).getId())) {
                        episodesList.get(i).setWatched(true);
                    }
                }
            }


        EpisodesAdapter adapter = (EpisodesAdapter) mEpisodesRecyclerView.getAdapter();
        adapter.setEpisodesList(episodesList);
        adapter.notifyDataSetChanged();

        mEpisodesRecyclerView.requestFocus();
        mProgressBar.setVisibility(View.INVISIBLE);
        mInfoTextView.setVisibility(View.INVISIBLE);
        mEpisodesRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showMessage(int stringId) {
        mProgressBar.setVisibility(View.INVISIBLE);
        mInfoTextView.setVisibility(View.VISIBLE);
        mEpisodesRecyclerView.setVisibility(View.INVISIBLE);
        mInfoTextView.setText(getString(stringId));
    }

    @Override
    public void showProgressIndicator() {
        mProgressBar.setVisibility(View.VISIBLE);
        mInfoTextView.setVisibility(View.INVISIBLE);
        mEpisodesRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEpisodesPresenter.detachView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.episode_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, ShowsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;

            case R.id.more_info:
                Intent i = new Intent(EpisodesActivity.this, ShowInfoActivity.class);
                i.putExtra("tvShowUrl", tvShowUrl);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
