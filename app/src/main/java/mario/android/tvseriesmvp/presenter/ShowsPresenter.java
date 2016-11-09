package mario.android.tvseriesmvp.presenter;

import java.util.List;

import mario.android.tvseriesmvp.R;
import mario.android.tvseriesmvp.TVSeriesApplication;
import mario.android.tvseriesmvp.model.MazeApiService;
import mario.android.tvseriesmvp.model.show.TVShow;
import mario.android.tvseriesmvp.view.ShowsMvpView;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Mario on 12.10.2016.
 */
public class ShowsPresenter implements Presenter<ShowsMvpView> {

    private static final String TAG = "ShowsPresenter";

    private ShowsMvpView mShowsMvpView;
    private Subscription mSubscription;
    private List<TVShow> mTVShowList;

    @Override
    public void attachView(ShowsMvpView view) {
        this.mShowsMvpView = view;
    }

    @Override
    public void detachView() {
        this.mShowsMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadShows(String showNameEntered) {
        String showName = showNameEntered.trim(); // Poglej kaj .trim naredi
        if (showName.isEmpty()) return;

        mShowsMvpView.showProgressIndicator();
        if (mSubscription != null) mSubscription.unsubscribe();

        TVSeriesApplication application = TVSeriesApplication.get(mShowsMvpView.getContext());
        MazeApiService mazeApiService = application.getMazeApiService();
        mSubscription = mazeApiService.listOfShows(showName)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<List<TVShow>>() {
                    @Override
                    public void onCompleted() {
                        if (!mTVShowList.isEmpty()){
                            mShowsMvpView.showTVShows(mTVShowList);
                        }else {
                            mShowsMvpView.showMessage(R.string.text_no_shows);
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        if (isHttp404(error)){
                            mShowsMvpView.showMessage(R.string.text_no_found);
                        }else {
                            mShowsMvpView.showMessage(R.string.error_loading_shows);
                        }
                    }

                    @Override
                    public void onNext(List<TVShow> tvShowList) {
                        mTVShowList = tvShowList;
                    }
                });


    }

    private static boolean isHttp404(Throwable error) {
        return error instanceof HttpException && ((HttpException) error).code() == 404;
    }

}
