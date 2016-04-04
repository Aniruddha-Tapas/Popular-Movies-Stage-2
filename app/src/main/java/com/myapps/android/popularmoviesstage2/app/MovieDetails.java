package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.android.popularmovies.app.MoviesContract.MovieEntry;
import com.squareup.picasso.Picasso;


public class MovieDetails {

    private final Context mContext;
    private final String mMovieId;
    private boolean mFavoriteState;
    private final View mRootView;
    private Cursor mCursor;

    public MovieDetails(Context context, String movieId,  View rootView) {
        this.mContext = context;
        this.mMovieId = movieId;
        this.mRootView = rootView;

    }

    public void showMovieDetails() {

        mCursor = getMovieDetails();

        showMoviePoster();
        showMovieTitle();
        showScrollingMovieOverview();
        showMovieReleaseDate();
        showMovieVoteAverage();
        setFavoritesToggleButtonInitialState();

        setToggleButtonHandler();

    }

    private Cursor getMovieDetails(){

        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(
                    MovieEntry.buildMovieWithMovieId(mMovieId),
                    null, null, null, null);
            if (cursor != null)
                cursor.moveToFirst();

        } catch (Exception e) {

        }

        return cursor;
    }

    private void showMoviePoster() {

        ImageView imageView = (ImageView) mRootView.findViewById(R.id.details_movie_poster);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setPadding(0,0,0,0);

        final byte[] imageBytes = mCursor.getBlob(
                mCursor.getColumnIndex(MovieEntry.COLUMN_POSTER_BITMAP) );

        if(imageBytes != null )
            imageView.setImageBitmap(
                    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length) );


    }

    private void showMovieTitle() {
        ((TextView) mRootView.findViewById(R.id.details_title))
                .setText(mCursor.getString(
                        mCursor.getColumnIndex(MovieEntry.COLUMN_TITLE)));
    }

    private void showScrollingMovieOverview() {
        TextView tv = ((TextView) mRootView.findViewById(R.id.details_overview));
        tv.setMovementMethod(new ScrollingMovementMethod());
        tv.setText(mCursor.getString(
                mCursor.getColumnIndex(MovieEntry.COLUMN_OVERVIEW)));
    }

    private void showMovieReleaseDate() {

        if(!mCursor.getString(mCursor.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE)).isEmpty()) {
            ((TextView) mRootView.findViewById(R.id.details_release_date))
                    .setText(mCursor.getString(mCursor.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE)).substring(0, 4));
        }else{
            ((TextView) mRootView.findViewById(R.id.details_release_date)).setText(R.string.unavailabledata);
        }
    }

    private void showMovieVoteAverage() {
        ((TextView) mRootView.findViewById(R.id.details_rating))
                .setText(mCursor.getDouble(
                        mCursor.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE))
                        + mContext.getString(R.string.out_of_ten));
    }

    private void setFavoritesToggleButtonInitialState() {
        ToggleButton toggle = (ToggleButton) mRootView.findViewById(R.id.toggle_button);
        toggle.setChecked(getFavoriteState());
    }

    private void setToggleButtonHandler() {
        ToggleButton toggle = (ToggleButton) mRootView.findViewById(R.id.toggle_button);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setFavoriteState(!getFavoriteState());
                if (isChecked) {
                    updateFavoriteInDb(1);
                } else {
                    updateFavoriteInDb(0);
                }
            }
        });
    }

    private void updateFavoriteInDb(int favoriteState) {

        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieEntry.COLUMN_FAVORITE, favoriteState);

        int rowsUpdated = mContext.getContentResolver()
                .update(MovieEntry.CONTENT_URI,
                        movieValues,
                        MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{String.valueOf(mMovieId)}
                );

    }

    public void setFavoriteState(boolean favoriteState) {
        this.mFavoriteState = favoriteState;
    }

    public boolean getFavoriteState() {

        int i =  mCursor.getInt( mCursor.getColumnIndex(
                             MovieEntry.COLUMN_FAVORITE) ) ;
        mFavoriteState = (boolean) (i != 0);
        return mFavoriteState;
    }

}
