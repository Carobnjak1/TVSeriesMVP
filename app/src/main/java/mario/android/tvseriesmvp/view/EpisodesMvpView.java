package mario.android.tvseriesmvp.view;

import java.util.List;

import mario.android.tvseriesmvp.model.episodes.Episodes;

/**
 * Created by Mario on 17.10.2016.
 */
public interface EpisodesMvpView extends MvpView {

    void showEpisodes(List<Episodes> episodesList);

    void showMessage(int stringId);

    void showProgressIndicator();
}
