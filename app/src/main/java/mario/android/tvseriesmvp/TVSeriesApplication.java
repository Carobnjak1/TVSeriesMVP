package mario.android.tvseriesmvp;

import android.app.Application;
import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import mario.android.tvseriesmvp.model.MazeApiService;
import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * Created by Mario on 12.10.2016.
 */
public class TVSeriesApplication extends Application {

    private MazeApiService mMazeApiService;
    private Scheduler mDefaultSubscribeScheduler;

    @Override
    public void onCreate() {
        super.onCreate();

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name("TVShowsMVP.realm")
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(realmConfiguration);
    }

    public static TVSeriesApplication get(Context context) {
        return (TVSeriesApplication) context.getApplicationContext();
    }

    public MazeApiService getMazeApiService() {
        if (mMazeApiService == null) {
            mMazeApiService = MazeApiService.Factory.create();
        }
        return mMazeApiService;
    }

    public Scheduler defaultSubscribeScheduler() {
        if (mDefaultSubscribeScheduler == null) {
            mDefaultSubscribeScheduler = Schedulers.io();
        }
        return mDefaultSubscribeScheduler;
    }
}
