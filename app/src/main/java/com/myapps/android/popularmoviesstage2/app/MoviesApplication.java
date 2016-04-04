package com.example.android.popularmovies.app;


import android.app.Application;
import android.content.SharedPreferences;

import com.example.android.popularmovies.app.module.AppModule;
import com.example.android.popularmovies.app.module.NetModule;

import javax.inject.Singleton;

import dagger.Component;

public class MoviesApplication extends Application {


    @Singleton
    @Component(modules = {AppModule.class, NetModule.class})
    public interface AppComponent {

        /*
        *   Dagger2 will inject dependencies provided my the modules
        *   listed above into the classes listed below
         */
        void inject(MoviesApplication moviesApplication);
        void inject(SharedPreferences sharedPreferences);
        void inject(TmdbApiParameters tmdbApiParameters);
        void inject(MoviesFragment fragment);
        void inject(Reviews reviews);
        void inject(Trailers trailers);

    }

    private AppComponent mAppComponent;


    @Override public void onCreate() {
        super.onCreate();

        // AppModule holds application-wide singletons
        mAppComponent = DaggerMoviesApplication_AppComponent.builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule())
                .build();
    }


    public AppComponent getAppComponent(){
        return mAppComponent;
    }

}


