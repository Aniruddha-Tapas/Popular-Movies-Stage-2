package com.example.android.popularmovies.app;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.widget.ListView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import javax.inject.Inject;

public class Reviews {


    private Activity mActivity;
    ListView mReviewsListView;
    String mMovieId;
    private MovieReviewsResponse reviewsResponse;

    @Inject MoviePreferences mMoviePreferences;
    @Inject Gson gson;

    public Reviews(Activity activity, ListView reviewsListView, String movieId) {
        this.mActivity = activity;
        this.mReviewsListView = reviewsListView;
        this.mMovieId = movieId;

        ((MoviesApplication) activity.getApplication()).getAppComponent().inject(this);

    }

    public void setMovieReviews() {

        if (mMoviePreferences.getDataSource().equals("network"))
            getReviewsFromWebAndInsertThemInDb();
        else
            setReviewListAdapter();
    }


    private void setReviewListAdapter() {

        Cursor cursor = null;
        try {
            cursor = mActivity.getContentResolver().query(
                    MoviesContract.ReviewEntry.buildReviewWithMovieId(String.valueOf(mMovieId)),
                    null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
        } catch (Exception e) {
        }

        ReviewsCursorAdapter rca = new ReviewsCursorAdapter(mActivity, cursor, 0);
        mReviewsListView.setAdapter(rca);

    }

    public void getReviewsFromWebAndInsertThemInDb() {

        String url = getMovieReviewsUrl(mMovieId);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(mActivity, url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responsestr = new String(responseBody);
                reviewsResponse = gson.fromJson(responsestr, MovieReviewsResponse.class);
                insertReviews(reviewsResponse);
                setReviewListAdapter();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
            }


        });

    }

    private String getMovieReviewsUrl(String movieId) {

        Uri uri = Uri.parse(getMovieReviewsBaseURL(movieId));
        Uri.Builder builder = uri.buildUpon();
        builder.appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_KEY);
        uri = builder.build();
        return uri.toString();
    }


    private String getMovieReviewsBaseURL(String movieID) {
        return "http://api.themoviedb.org/3/movie/" + movieID + "/reviews?";
    }


    private void insertReviews(MovieReviewsResponse mrr) {

        if (!mrr.getResults().isEmpty()) {
            for (MovieReviewsResponse.ResultsEntity mr : mrr.getResults()) {
                addReviewToDb(mrr, mr);
            }
        }
    }


    private void addReviewToDb(MovieReviewsResponse mrr,
                               MovieReviewsResponse.ResultsEntity mr) {

        ContentValues reviewValues = new ContentValues();

        reviewValues.put(MoviesContract.ReviewEntry.COLUMN_MOVIE_ID, mrr.getId());

        if (mr.getId() == null)
            return;  // If there's no review ID, don't try to insert it
        reviewValues.put(MoviesContract.ReviewEntry.COLUMN_REVIEW_ID, mr.getId());

        if (mr.getAuthor() == null) // Don't keep null values
            reviewValues.put(MoviesContract.ReviewEntry.COLUMN_AUTHOR, "");
        else
            reviewValues.put(MoviesContract.ReviewEntry.COLUMN_AUTHOR, mr.getAuthor());

        if (mr.getContent() == null)
            return;  // If there's no content, don't try to insert it
        reviewValues.put(MoviesContract.ReviewEntry.COLUMN_CONTENT, mr.getContent());

        if (mr.getAuthor() == null) // Don't keep null values
            reviewValues.put(MoviesContract.ReviewEntry.COLUMN_URL, "");
        else
            reviewValues.put(MoviesContract.ReviewEntry.COLUMN_URL, mr.getUrl());

        mActivity.getContentResolver().insert(MoviesContract.ReviewEntry.CONTENT_URI,
                reviewValues);

    }


}
