package mario.android.tvseriesmvp.presenter;

import java.util.List;

import mario.android.tvseriesmvp.R;
import mario.android.tvseriesmvp.TVSeriesApplication;
import mario.android.tvseriesmvp.model.MazeApiService;
import mario.android.tvseriesmvp.model.episodes.Episodes;
import mario.android.tvseriesmvp.view.EpisodesMvpView;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Mario on 17.10.2016.
 */
public class EpisodesPresenter implements Presenter<EpisodesMvpView>  {

    private static final String TAG = "EpisodesPresenter";

    private EpisodesMvpView mEpisodesMvpView;
    private Subscription mSubscription;
    private List<Episodes> mEpisodesList;

    @Override
    public void attachView(EpisodesMvpView view) {
        this.mEpisodesMvpView = view;
    }

    @Override
    public void detachView() {
        this.mEpisodesMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }


    public void loadEpisodes(String showId) {
        if (showId.isEmpty()) return;

        mEpisodesMvpView.showProgressIndicator();
        if (mSubscription != null) mSubscription.unsubscribe();

        TVSeriesApplication application = TVSeriesApplication.get(mEpisodesMvpView.getContext());
        MazeApiService mazeApiService = application.getMazeApiService();
        mSubscription = mazeApiService.listOfEpisodes(showId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<List<Episodes>>() {
                    @Override
                    public void onCompleted() {
                        if (!mEpisodesList.isEmpty()){
                            mEpisodesMvpView.showEpisodes(mEpisodesList);
                        }else {
                            mEpisodesMvpView.showMessage(R.string.text_no_shows);
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        if (isHttp404(error)){
                            mEpisodesMvpView.showMessage(R.string.text_no_found);
                        }else {
                            mEpisodesMvpView.showMessage(R.string.error_loading_shows);
                        }
                    }

                    @Override
                    public void onNext(List<Episodes> episodesList) {
                        mEpisodesList = episodesList;
                    }
                });
    }

    private static boolean isHttp404(Throwable error) {
        return error instanceof HttpException && ((HttpException) error).code() == 404;
    }
}
