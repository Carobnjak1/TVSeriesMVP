package mario.android.tvseriesmvp.model;

import java.util.List;

import mario.android.tvseriesmvp.model.episodes.Episodes;
import mario.android.tvseriesmvp.model.show.TVShow;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Mario on 12.10.2016.
 */
public interface MazeApiService {
    @GET("search/shows")
    Observable<List<TVShow>> listOfShows(@Query("q") String name);

    @GET("shows/{id}/episodes")
    Observable<List<Episodes>> listOfEpisodes(@Path("id") String id);

    class Factory {
        public static MazeApiService create() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.tvmaze.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            return retrofit.create(MazeApiService.class);
        }
    }
}
