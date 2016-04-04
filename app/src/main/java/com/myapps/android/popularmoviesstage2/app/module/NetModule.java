package com.example.android.popularmovies.app.module;


import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class NetModule {

//    String baseUrl;

    public NetModule( /* String baseUrl  */  ) {
      //   this.baseUrl = baseUrl;
    }

    @Provides
    @Singleton
    public Gson gson(){
        return new Gson();
    }


}
