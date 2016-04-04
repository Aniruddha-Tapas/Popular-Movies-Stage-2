
package com.example.android.popularmovies.app;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.popularmovies.app.MoviesContract.MovieEntry;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.http.Header;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

public class MoviesFragment extends Fragment {

    private final String LOG_TAG = MoviesFragment.class.getSimpleName();
    private int currentPage = 1;
    private Boolean currentlyFetchingMovies = true;
    private Boolean morePagesOfMoviesLeftToGet = true;
    private MoviesCursorAdapter moviesCursorAdapter;
    int savedPosition = 0;
    String savedMovieId;
    private View rootView;
    private GridView mGridView;
    final Set<Target> targetHashSet = new HashSet<>();
    private String mState = "";
    private boolean weJustScrolledToTheEnd = false;


    @Inject MoviePreferences mMoviePreferences;
    @Inject Gson gson;


    interface Callback {
        void onItemSelected(String movieId);
    }

    public MoviesFragment() { }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Inject mMoviePreferences
        ((MoviesApplication) activity.getApplication()).getAppComponent().inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "***  Entering onCreate()");
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        Log.v(LOG_TAG, "***  Entering onSaveInstanceState()");
        super.onSaveInstanceState(outstate);

        int pos = mGridView.getFirstVisiblePosition();
        outstate.putInt("selected_item_position", pos);
        Log.v(LOG_TAG, "***  onSaveInstanceState() first_visible_position = "+ pos);
        outstate.putInt("saved_position", savedPosition);
        Log.v(LOG_TAG, "***  onSaveInstanceState() savedPosition = "+ savedPosition);
        outstate.putString("saved_movie_id", savedMovieId);
        Log.v(LOG_TAG, "***  onSaveInstanceState() savedMovieId = "+ savedMovieId);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.v(LOG_TAG, "***  Entering onCreateView()");

        rootView = inflater.inflate(R.layout.movie_fragment, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.gridview_movie);

        Cursor cursor = getCursorWithCurrentPreferences();
        moviesCursorAdapter = new MoviesCursorAdapter(getActivity(), cursor, 0);
        mGridView.setAdapter(moviesCursorAdapter);
        setMovieItemClickListener(mGridView);
        setUpMovieGridviewEndlessScrolling(mGridView);
        return rootView;
    }


    @Override
    public void onResume() {
            super.onResume();
        Log.v(LOG_TAG, "***  Entering onResume()");

        if (mMoviePreferences.getDataSource().equals("network")) {
            initializeMoviesGridFromNetwork();
        } else {  // Cache  or Favorites
            initializeMoviesGridFromCache();
        }

    }

    private void initializeMoviesGridFromCache() {

        switch (mState){

            case "app_just_started":
                reattachGridViewCursorAdapter();
                selectFirstMovieToFillOutDetail();
                Log.v(LOG_TAG, "***  Cache, App Just Started, reatached, set first position");
                break;

            case "rotated":  // Do nothing; no new selection
                reattachGridViewCursorAdapter();
                selectFirstMovieToFillOutDetail();
                Log.v(LOG_TAG, "***  Cache, rotated, did nothing");
                break;

            case "prefs_changed":
                reattachGridViewCursorAdapter();
                selectFirstMovieToFillOutDetail();
                Log.v(LOG_TAG, "***  Cache, prefs changed, reatached, set first position");
                break;

            case "prefs_didnt_change":  // Do nothing; no new selection
                reattachGridViewCursorAdapter();
                selectFirstMovieToFillOutDetail();
                Log.v(LOG_TAG, "***  Cache, prefs didn't change, did nothing.");
                break;

        }

    }

    private void initializeMoviesGridFromNetwork() {

        switch (mState){

            case "app_just_started":
                getFirstPageOfMovies();  // This will select the first movie
                Log.v(LOG_TAG, "***  Network, App Just Started, Got First Page of Movies");
                break;

            case "rotated":  // Do nothing; no new selection
                getFirstPageOfMovies(); // This will select the first movie
                Log.v(LOG_TAG, "***  Network, rotated, did nothing");
                break;

            case "prefs_changed":
                getFirstPageOfMovies(); // This will select the first movie
                Log.v(LOG_TAG, "***  Network, prefs changed, Got First Page of Movies.");
                break;

            case "prefs_didnt_change":  // Do nothing; no new selection
                getFirstPageOfMovies(); // This will select the first movie
                Log.v(LOG_TAG, "***  Network, prefs didn't change, did nothing.");
                break;

        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.v(LOG_TAG, "***  Entering onActivityCreated() ");

        if(savedInstanceState != null ) {
            savedPosition = savedInstanceState.getInt("saved_position");
            if (savedPosition == -1)
                savedPosition = 0;

            savedMovieId = savedInstanceState.getString("saved_movie_id");
            if (savedMovieId == null)
                savedMovieId = "";
        }

        // Did the App just start?
        if(didAppJustStart(savedInstanceState))
            mState = "app_just_started";
         else  // Ok, we just rotated
            mState = "rotated";

    }

    private boolean didAppJustStart(Bundle savedInstanceState) {
        return savedInstanceState == null;
    }

    public void getFirstPageOfMovies() {
        morePagesOfMoviesLeftToGet = true;  // New data set on startup and on prefs changing
        currentPage = 1;        // This is only called on startup, or when prefs change
        fetchMoviesFromWeb(currentPage);
        currentPage += 1;
    }

    // This should only be called if prefs have changed or when the program opens
    private void reattachGridViewCursorAdapter() {
        Cursor cursor = getCursorWithCurrentPreferences();
        if (cursor != null) {
            cursor.moveToLast();
            cursor.moveToFirst();
        }
        moviesCursorAdapter.changeCursor(cursor);
        setMovieItemClickListener(mGridView);
        setUpMovieGridviewEndlessScrolling(mGridView);
        currentlyFetchingMovies = false;
    }

    private void selectFirstMovieToFillOutDetail() {

        if (mGridView.getNumColumns() == -1)
            return;

        if(mGridView.getNumColumns() % 2 == 0)  // Tablets use odd #s
            return;  // Don't select unless we're using master/detail

        if(savedMovieId != null && savedMovieId.equals("")) {
            Log.v(LOG_TAG, "***  FillOutDetail savedPosition = "+ savedPosition);
            mGridView.smoothScrollToPosition(savedPosition);
            Log.v(LOG_TAG, "***  FillOutDetail savedMovieId = " + savedMovieId);
            ((Callback) getActivity()).onItemSelected(savedMovieId);
        }
        else {
            Cursor cursor = moviesCursorAdapter.getCursor();
            cursor.moveToFirst();
            String movieId = getMovieIdFromCursor();
            ((Callback) getActivity()).onItemSelected(movieId);
        }
    }

    private Cursor getCursorWithCurrentPreferences() {

        Uri uri = mMoviePreferences.getUriFromMoviePreferences();
        Cursor cursor;
        try {
            cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToLast();
//                String cursorContents = DatabaseUtils.dumpCurrentRowToString(cursor);
//                Log.v(LOG_TAG, cursorContents);
                cursor.moveToFirst();
            }
        } catch (Exception e) {
            return null;
        }

        return cursor;
    }



    private void setMovieItemClickListener(GridView gridView) {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(currentlyFetchingMovies)
                    return;

                savedPosition = position;
                ((Callback) getActivity())
                        .onItemSelected(getMovieIdFromCursor());
            }
        });
    }

    private void setUpMovieGridviewEndlessScrolling(GridView gridView) {
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mMoviePreferences.getDataSource().equals("network"))
                    getAnotherPageOfMoviesIfNeeded(firstVisibleItem, visibleItemCount, totalItemCount);
            }
        });
    }

    private void getAnotherPageOfMoviesIfNeeded(int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if (shouldWeFetchMoreMovies(firstVisibleItem, visibleItemCount, totalItemCount)) {
            weJustScrolledToTheEnd = true;
            fetchMoviesFromWeb(currentPage);
            currentPage += 1;
        }
    }

    private boolean shouldWeFetchMoreMovies(int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        int last = firstVisibleItem + visibleItemCount;

        if (currentPage > 1 &&
                visibleItemCount != 0
                && ((visibleItemCount + firstVisibleItem) == totalItemCount
                && totalItemCount == last
                && last == visibleItemCount))
            return false;

        return (last == totalItemCount) &&   // We've scrolled past the end of the grid view
                !currentlyFetchingMovies &&        // We're not trying to grab movies already with
                // another HTTPAsync client
                morePagesOfMoviesLeftToGet;  // We're not out of pages of movies yet

    }


    private String getMovieIdFromCursor() {

        Cursor cursor = moviesCursorAdapter.getCursor();
        savedMovieId = String.valueOf(cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID)));
        return savedMovieId;
    }




    private void updateMovie(MoviesResponse.ResultsEntity mr, boolean isLastResult) {

        // These values change at least daily, especially for popular movies
        final ContentValues movieValues = new ContentValues();
        movieValues.put(MovieEntry.COLUMN_POPULARITY, mr.getPopularity());
        movieValues.put(MovieEntry.COLUMN_VOTE_COUNT, mr.getVote_count());
        movieValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, mr.getVote_average());

        String selection = MovieEntry.COLUMN_MOVIE_ID + " = ? ";
        String[] selectionArgs = new String[]{String.valueOf(mr.getId())};

        getActivity().getContentResolver().update(
                MovieEntry.CONTENT_URI,
                movieValues,
                selection, selectionArgs
        );

        ifFullPageOfMoviesHasBeenInsertedOrUpdatedChangeCursor(isLastResult);

    }

    private boolean isMovieAlreadyInDb(MoviesResponse.ResultsEntity mr) {

        Cursor cursor = null;

        try {

            String selection = MovieEntry.COLUMN_MOVIE_ID + " = ? ";
            String[] selectionArgs = new String[]{String.valueOf(mr.getId())};

            cursor = getActivity().getContentResolver().query( MovieEntry.CONTENT_URI,
                    null, selection, selectionArgs, null );

            if (cursor != null && cursor.getCount() == 1)
                return true;

        } catch (Exception e) {
            return false;

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return false;
    }

    private void fetchMoviesFromWeb(final int page) {

        TmdbApiParameters apiParams = new TmdbApiParameters(getActivity(), page);
        String url = apiParams.buildMoviesUri().toString();

        currentlyFetchingMovies = true;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getActivity(), url, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                int numberOfMovies = insertOrUpdateMovies(getMoviesResponse(responseBody));
                // Don't allow more grid view scrolling when we're out of results
                morePagesOfMoviesLeftToGet = numberOfMovies >= 20;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });

    }

    private int insertOrUpdateMovies(MoviesResponse moviesResponse) {
        int totalResults = 0;  // Assume no results left to get
        if (!moviesResponse.getResults().isEmpty()) {
            totalResults = moviesResponse.getResults().size();
            int resultsProcessed = 0;
            for (MoviesResponse.ResultsEntity mr : moviesResponse.getResults()) {
                resultsProcessed++;
                if (isMovieAlreadyInDb(mr)) {
                    updateMovie(mr, resultsProcessed == totalResults);
                } else {
                    addMovieToDb(mr, resultsProcessed == totalResults);
                }
            }
        }

        return totalResults;
    }

    private MoviesResponse getMoviesResponse(byte[] responseBody) {
        String responsestr = new String(responseBody);
        return gson.fromJson(responsestr, MoviesResponse.class);
    }

    private void addMovieToDb(final MoviesResponse.ResultsEntity mr, boolean isLastResult) {

        String fullPosterPathUrl =
                getActivity().getString(R.string.tmdb_base_image_url) +
                        getActivity().getString(R.string.tmdb_image_size_185) +
                        mr.getPoster_path();

        Target bitmapTarget = new BitmapTarget(mr, isLastResult);
        targetHashSet.add(bitmapTarget);
        Picasso.with(getActivity()).load(fullPosterPathUrl).into(bitmapTarget);

    }

    class BitmapTarget implements Target {

        MoviesResponse.ResultsEntity mMr;
        boolean mIsLastResult;

        public BitmapTarget(MoviesResponse.ResultsEntity mMr, boolean isLastResult) {
            this.mMr = mMr;
            this.mIsLastResult = isLastResult;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {

            byte[] imageBytes = getBitmapAsByteArray(bitmap);
            insertMovieIntoDB(imageBytes, mMr);
            ifFullPageOfMoviesHasBeenInsertedOrUpdatedChangeCursor(mIsLastResult);
            targetHashSet.remove(this);
        }

        @Override
        public void onBitmapFailed(Drawable drawable) {
            targetHashSet.remove(this);
        }

        @Override
        public void onPrepareLoad(Drawable drawable) {
        }
    }

    private void ifFullPageOfMoviesHasBeenInsertedOrUpdatedChangeCursor(boolean isLastResult) {

        if (isLastResult) { // Change cursor after last result from a page has been downloaded
            reattachGridViewCursorAdapter();
            // if we get here because we scrolled to the end, don't scroll back
            if (didWeJustScrollToTheEnd()) {
                weJustScrolledToTheEnd = false;
            } else {
                selectFirstMovieToFillOutDetail();
            }

        }

    }

    private boolean didWeJustScrollToTheEnd() {

        return weJustScrolledToTheEnd;
    }

    private void insertMovieIntoDB(byte[] imageBytes, MoviesResponse.ResultsEntity mr) {

        final ContentValues movieValues = new ContentValues();

        movieValues.put(MovieEntry.COLUMN_POSTER_PATH, mr.getPoster_path());
        movieValues.put(MovieEntry.COLUMN_ADULT, mr.isAdult());
        movieValues.put(MovieEntry.COLUMN_OVERVIEW, mr.getOverview());
        movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, mr.getRelease_date());
        movieValues.put(MovieEntry.COLUMN_MOVIE_ID, mr.getId());
        movieValues.put(MovieEntry.COLUMN_ORIGINAL_TITLE, mr.getOriginal_title());
        movieValues.put(MovieEntry.COLUMN_ORIGINAL_LANGUAGE, mr.getOriginal_language());
        movieValues.put(MovieEntry.COLUMN_TITLE, mr.getTitle());
        movieValues.put(MovieEntry.COLUMN_BACKDROP_PATH, mr.getBackdrop_path());
        movieValues.put(MovieEntry.COLUMN_POPULARITY, mr.getPopularity());
        movieValues.put(MovieEntry.COLUMN_VOTE_COUNT, mr.getVote_count());
        movieValues.put(MovieEntry.COLUMN_VIDEO, mr.isVideo());
        movieValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, mr.getVote_average());
        movieValues.put(MovieEntry.COLUMN_GENRE_IDS, mr.getGenre_ids().toString());
        /*  These are initial values, all set to zero  */
        movieValues.put(MovieEntry.COLUMN_FAVORITE, 0);
        movieValues.put(MovieEntry.COLUMN_WATCHED, 0);
        movieValues.put(MovieEntry.COLUMN_WATCH_ME, 0);
        movieValues.put(MovieEntry.COLUMN_POSTER_BITMAP, imageBytes);

        getActivity().getContentResolver().insert(MovieEntry.CONTENT_URI, movieValues);

    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        return outputStream.toByteArray();
    }


}



