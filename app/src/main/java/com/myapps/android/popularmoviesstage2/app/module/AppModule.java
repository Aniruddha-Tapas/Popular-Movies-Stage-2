package com.example.android.popularmovies.app.module;


import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.android.popularmovies.app.MoviePreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private Application mApplication;

    public AppModule(Application application) {
        this.mApplication = application;
    }

    @Provides
    @Singleton
    Application providesApplication(){
        return mApplication;
    }

    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences(Application application){
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    MoviePreferences providesMoviePreferences(SharedPreferences sharedPreferences,
                                              Application application){
        MoviePreferences moviePreferences =
                new MoviePreferences(application, sharedPreferences);
        return moviePreferences;
    }
}
