package com.example.android.popularmovies.app;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import javax.inject.Inject;

public class TmdbApiParameters {

    private final String LOG_TAG = TmdbApiParameters.class.getSimpleName();

    Activity mActivity;
    int mcurrentPage = 1;
    private Uri.Builder builder;
    private Uri uri;

    @Inject
    MoviePreferences mMoviePreferences;

    TmdbApiParameters(Activity activity, int currentPage ){
        this.mActivity = activity;
        this.mcurrentPage = currentPage;
        uri = Uri.parse(getBaseURL());
        builder = uri.buildUpon();

        // Inject mMoviePreferences
        ((MoviesApplication) activity.getApplication()).getAppComponent().inject(this);

    }


    public Uri buildMoviesUri() {

        appendPage();
        appendSortOrder();
        appendVoteCount();
        appendGenres();
        appendTimePeriods();
        appendApiKey();

        uri = builder.build();

        Log.v(LOG_TAG, "Movie URL " + uri.toString());
        return uri;
    }

    private void appendApiKey() {
        builder.appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_KEY);
    }

    private void appendTimePeriods() {
        // Use primariy_release_date or you get back multile results
        // because sometimes there are much later re-releases
        // This can pollute results upon sorting by gross, popularity, etc.
        TimePeriod timePeriod = new TimePeriod(mMoviePreferences.getTimePeriods());
        if(timePeriod.periodHasLowerDate()) {
            builder.appendQueryParameter("primary_release_date.gte", timePeriod.getLowerDate());
        }
        if(timePeriod.periodHasUpperDate()) {
            builder.appendQueryParameter("primary_release_date.lte", timePeriod.getUpperDate());
        }
    }

    private void appendGenres() {
        String genres = mMoviePreferences.getGenresAsCommaSeparatedNumbers();
        if(!genres.equals(""))
           builder.appendQueryParameter("with_genres", genres);
    }

    private void appendVoteCount() {
        builder.appendQueryParameter("vote_count.gte", mMoviePreferences.getVoteCount());
    }

    private void appendSortOrder() {
        String sortOrder = mMoviePreferences.getSortOrder();
        if(sortOrder != null && !sortOrder.equals("none") )
            builder.appendQueryParameter("sort_by", sortOrder);
    }

    private void appendPage() {
         builder.appendQueryParameter("page", String.valueOf(mcurrentPage));
    }

    private String getBaseURL(){
        return mActivity.getString(R.string.tmdb_base_url);
    }



}
