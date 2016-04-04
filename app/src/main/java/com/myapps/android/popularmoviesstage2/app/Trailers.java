package com.example.android.popularmovies.app;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import javax.inject.Inject;

public class Trailers {

    private MovieDetailsFragment movieDetailsFragment;
    Activity mActivity;
    ListView mTrailersListView;
    String mMovieId;
    private MovieTrailersResponse trailersResponse;

    @Inject MoviePreferences mMoviePreferences;
    @Inject Gson gson;


    public Trailers(MovieDetailsFragment movieDetailsFragment, Activity activity, ListView trailersListView, String movieId) {
        this.movieDetailsFragment = movieDetailsFragment;
        this.mActivity = activity;
        this.mTrailersListView = trailersListView;
        this.mMovieId = movieId;

        // Inject mMoviePreferences
        ((MoviesApplication) activity.getApplication()).getAppComponent().inject(this);

    }


    public void setMovieTrailers() {

        if (mMoviePreferences.getDataSource().equals("network"))
            getTrailersFromWebAndInsertThemInDb();
        else
            setTrailersListAdapter();

    }


    private void setTrailersListAdapter() {

        Cursor cursor = null;
        try {
            cursor = mActivity.getContentResolver().query(
                    MoviesContract.TrailerEntry.buildTrailerWithMovieId(String.valueOf(mMovieId)),
                    null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
        } catch (Exception e) {
        }

        TrailersCursorAdapter tca = new TrailersCursorAdapter(mActivity, cursor, 0);
        mTrailersListView.setAdapter(tca);

    }


    public void getTrailersFromWebAndInsertThemInDb() {

        String url = getMovieTrailersUrl(mMovieId);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(mActivity, url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responsestr = new String(responseBody);
                trailersResponse = gson.fromJson(responsestr, MovieTrailersResponse.class);
                insertTrailers(trailersResponse);
                setTrailersListAdapter();
                setTrailerClickListener();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
            }
        });

    }

    private void setTrailerClickListener() {
        mTrailersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = mActivity
                        .getPackageManager()
                        .getLaunchIntentForPackage("com.google.android.youtube");

                TextView sourceView = (TextView) view.findViewById(
                        R.id.list_item_movie_trailer_url_textview);

                String source = sourceView.getText().toString();
                watchYoutubeVideo(source);

            }
        });
    }

    private void watchYoutubeVideo(String source) {

        try {

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + source));
            mActivity.startActivity(intent);

        } catch (ActivityNotFoundException e) {

            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(mActivity.getString(R.string.you_tube_url) + source));

            mActivity.startActivity(intent);
        }
    }

    private String getMovieTrailersUrl(String movieId) {

        Uri uri = Uri.parse(getMovieTrailersBaseURL(movieId));
        Uri.Builder builder = uri.buildUpon();
        builder.appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_KEY);
        uri = builder.build();
        return uri.toString();
    }


    private String getMovieTrailersBaseURL(String movieID) {
        return "http://api.themoviedb.org/3/movie/" + movieID + "/trailers?";
    }


    private void insertTrailers(MovieTrailersResponse mtr) {

        StringBuilder trailers = new StringBuilder();
        if (!mtr.getYoutube().isEmpty()) {
            for (MovieTrailersResponse.YoutubeEntity yte : mtr.getYoutube()) {
                addTrailerToDb(mtr, yte);
                trailers.append(
                        mActivity.getString(R.string.you_tube_url) +
                                yte.getSource() + "  ");
            }
        }

        movieDetailsFragment
                .mShareActionProvider
                .setShareIntent(movieDetailsFragment.createShareTrailerIntent(trailers.toString()));
    }


    private void addTrailerToDb(MovieTrailersResponse mtr,
                                MovieTrailersResponse.YoutubeEntity yte) {

        ContentValues trailerValues = new ContentValues();

        trailerValues.put(MoviesContract.TrailerEntry.COLUMN_MOVIE_ID, mtr.getId());

        if (yte.getName() == null)
            trailerValues.put(MoviesContract.TrailerEntry.COLUMN_NAME, "");
        else
            trailerValues.put(MoviesContract.TrailerEntry.COLUMN_NAME, yte.getName());

        if (yte.getName() == null)
            trailerValues.put(MoviesContract.TrailerEntry.COLUMN_SIZE, "");
        else
            trailerValues.put(MoviesContract.TrailerEntry.COLUMN_SIZE, yte.getSize());

        if (yte.getSource() == null)
            return;  // There's no point in adding this record if there's no trailer to play
        trailerValues.put(MoviesContract.TrailerEntry.COLUMN_SOURCE, yte.getSource());

        if (yte.getType() == null)
            trailerValues.put(MoviesContract.TrailerEntry.COLUMN_TYPE, "");
        else
            trailerValues.put(MoviesContract.TrailerEntry.COLUMN_TYPE, yte.getType());

        mActivity.getContentResolver().insert(MoviesContract.TrailerEntry.CONTENT_URI,
                trailerValues);

    }

}
