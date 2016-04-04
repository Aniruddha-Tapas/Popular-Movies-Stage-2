package com.example.android.popularmovies.app;


import android.app.Application;
import android.content.SharedPreferences;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Set;


public class MoviePreferences {

    SharedPreferences mSharedPreferences;
    Application mApplication;

    public MoviePreferences(Application mApplication, SharedPreferences mSharedPreferences) {
        this.mApplication = mApplication;
        this.mSharedPreferences = mSharedPreferences;
    }

    public String getTimePeriods() {
        return   mSharedPreferences.getString(mApplication
                    .getString(R.string.pref_period_key), "all");
    }

    public String getGenresAsCommaSeparatedNumbers() {

        String genres;
        Set<String> genresSet = mSharedPreferences.getStringSet("genre_ids", null);
        if ( genresSet != null && !genresSet.isEmpty()  )
            genres = genresSet.toString().replaceAll("\\s+", "").replace("[", "").replace("]", "");
        else
            genres = "";

        return genres;
    }

    public String getSortOrder(){ return  mSharedPreferences.getString(
            mApplication.getString(R.string.pref_sort_order_key),
            mApplication.getString(R.string.pref_sort_order_most_popular));
    }

    public String getVoteCount(){  return  mSharedPreferences.getString(
            mApplication.getString(R.string.pref_vote_count_key),
            mApplication.getString(R.string.pref_vote_count_value_0)); }

    public String getDataSource(){ return mSharedPreferences.getString(
            mApplication.getString(R.string.pref_data_source_key), "network");
    }

    public Uri getUriFromMoviePreferences() {

        String[] keys = {mApplication.getString(R.string.pref_data_source_key),
                mApplication.getString(R.string.pref_vote_count_key),
                mApplication.getString(R.string.pref_period_key),
                "genre_ids", mApplication.getString(R.string.pref_sort_order_key)};

        String[] values = {getDataSource(),
                getVoteCount(),
                getTimePeriods(),
                getGenresAsCommaSeparatedNumbers(),
                getSortOrder() };

        return MoviesContract.MovieEntry.buildMoviesUriWithQueryParameters(
                MoviesContract.MovieEntry.CONTENT_URI, keys, values
        );
    }

    public ArrayList<Integer> getGenreIdsAsInegerArrayList(String genreIds) {

        if (genreIds != null) {
            String[] strArray = genreIds
                    .replace("[", "").replace("]", "").replaceAll("\\s+", "")
                    .split(",");
            ArrayList<Integer> intArrayList = new ArrayList<>(strArray.length);
            for (String i : strArray) {
                if (i != null && !i.isEmpty())
                    intArrayList.add(Integer.parseInt(i));
            }

            return intArrayList;
        } else
            return null;
    }

    public String toString(){
        return mSharedPreferences.getAll().toString();
    }

    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }


}
