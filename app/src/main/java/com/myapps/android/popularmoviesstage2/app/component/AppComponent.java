package com.example.android.popularmovies.app.component;

import android.content.SharedPreferences;

import com.example.android.popularmovies.app.MoviesApplication;
import com.example.android.popularmovies.app.MoviesFragment;
import com.example.android.popularmovies.app.Reviews;
import com.example.android.popularmovies.app.TmdbApiParameters;
import com.example.android.popularmovies.app.Trailers;
import com.example.android.popularmovies.app.module.AppModule;
import com.example.android.popularmovies.app.module.NetModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component (modules = {AppModule.class, NetModule.class})
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
