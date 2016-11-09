package mario.android.tvseriesmvp.view;

import java.util.List;

import mario.android.tvseriesmvp.model.show.TVShow;

/**
 * Created by Mario on 12.10.2016.
 */
public interface ShowsMvpView extends MvpView {

    void showTVShows(List<TVShow> tvShowList);

    void showMessage(int stringId);

    void showProgressIndicator();
}
