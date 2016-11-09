package mario.android.tvseriesmvp.presenter;

/**
 * Created by Mario on 12.10.2016.
 */
public interface Presenter<V> {

    void attachView(V view);

    void detachView();
}
