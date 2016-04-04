package com.example.android.popularmovies.app;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Arrays;

import static com.example.android.popularmovies.app.MoviesContract.CONTENT_AUTHORITY;


public class MovieProvider extends ContentProvider {

    public static final String LOG_TAG = MovieProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mOpenHelper;

    static final int MOVIES = 100;
    static final int MOVIES_WITH_QUERY_STRING = 101;
    static final int MOVIES_BY_MOVIE_ID = 102;
    static final int TRAILERS = 200;
    static final int REVIEWS = 300;
    static final int TRAILERS_BY_MOVIE_ID = 201;
    static final int REVIEWS_BY_MOVIE_ID = 301;

    private static final SQLiteQueryBuilder sMoviesQueryBuilder;

    static {

        sMoviesQueryBuilder = new SQLiteQueryBuilder();
    }

    @Override
    public boolean onCreate() {

        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)){

            case MOVIES: {
                retCursor = getMovies(uri, projection, selection, selectionArgs, sortOrder);
                break;
            }
            case TRAILERS: {
                retCursor = getTrailers(uri, projection, selection, selectionArgs, sortOrder);
                break;
            }
            case REVIEWS: {
                retCursor = getReviews(uri, projection, selection, selectionArgs, sortOrder);
                break;
            }
            case MOVIES_WITH_QUERY_STRING: {
                retCursor = getMoviesWithQueryString(uri, projection, sortOrder);
                break;
            }
            case MOVIES_BY_MOVIE_ID: {
                retCursor = getMovieByMovieId(uri, projection, sortOrder);
                break;
            }
            case TRAILERS_BY_MOVIE_ID: {
                retCursor = getTrailersByMovieId(uri, projection, sortOrder);
                break;
            }
            case REVIEWS_BY_MOVIE_ID: {
                retCursor = getReviewsByMovieId(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getMovieByMovieId(Uri uri, String[] projection, String sortOrder) {

        String movieId = MoviesContract.MovieEntry.getMovieIdFromUri(uri);
        String selection = MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";
        String[] selectionArgs = new String[]{movieId};

        return  mOpenHelper.getReadableDatabase().query(
                MoviesContract.MovieEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

    }

    private Cursor getTrailersByMovieId(Uri uri, String[] projection, String sortOrder) {

        String movieId = MoviesContract.TrailerEntry.getMovieIdFromUri(uri);
        String selection = MoviesContract.TrailerEntry.COLUMN_MOVIE_ID + " = ? ";
        String[] selectionArgs = new String[]{movieId};

        return  mOpenHelper.getReadableDatabase().query(
                MoviesContract.TrailerEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

    }
    private Cursor getReviewsByMovieId(Uri uri, String[] projection, String sortOrder) {

        String movieId = MoviesContract.ReviewEntry.getMovieIdFromUri(uri);
        String selection = MoviesContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? ";
        String[] selectionArgs = new String[]{movieId};

        return  mOpenHelper.getReadableDatabase().query(
                MoviesContract.ReviewEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

    }

    private Cursor getMoviesWithQueryString(Uri uri, String[] projection, String sortOrder) {

        MovieCursorQueryParameters mcqp = new MovieCursorQueryParameters(uri);

        return  mOpenHelper.getReadableDatabase().query(
                MoviesContract.MovieEntry.TABLE_NAME,
                projection,
                mcqp.getSelection(),
                mcqp.getSelectionArgs(),
                null,
                null,
                mcqp.getSortOrder()
        );

    }


    private Cursor getMovies(Uri uri, String[] projection, String selection,
                             String[] selectionArgs, String sortOrder) {


        return mOpenHelper.getReadableDatabase().query(
                MoviesContract.MovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

    }

    private Cursor getTrailers(Uri uri, String[] projection, String selection,
                             String[] selectionArgs, String sortOrder) {

        return mOpenHelper.getReadableDatabase().query(
                MoviesContract.TrailerEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

    }
    private Cursor getReviews(Uri uri, String[] projection, String selection,
                             String[] selectionArgs, String sortOrder) {

        return mOpenHelper.getReadableDatabase().query(
                MoviesContract.ReviewEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

    }


    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match){

            case MOVIES:
                return MoviesContract.MovieEntry.CONTENT_TYPE;

            case TRAILERS:
                return MoviesContract.TrailerEntry.CONTENT_TYPE;

            case REVIEWS:
                return MoviesContract.ReviewEntry.CONTENT_TYPE;

            case MOVIES_WITH_QUERY_STRING:
                return MoviesContract.MovieEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case MOVIES: {
                long _id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MoviesContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TRAILERS: {
                long _id = db.insert(MoviesContract.TrailerEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MoviesContract.TrailerEntry.buildTrailerUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEWS: {
                long _id = db.insert(MoviesContract.ReviewEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MoviesContract.ReviewEntry.buildReviewUri (_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if(null==selection) selection = "1";
        switch ((match)){
            case MOVIES:
                rowsDeleted = db.delete(MoviesContract.MovieEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;

            case TRAILERS:
                rowsDeleted = db.delete(MoviesContract.TrailerEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;

            case REVIEWS:
                rowsDeleted = db.delete(MoviesContract.ReviewEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case MOVIES:
                rowsUpdated = db.update(MoviesContract.MovieEntry.TABLE_NAME, values, selection,
                                    selectionArgs);
                   break;
            case TRAILERS:
                rowsUpdated = db.update(MoviesContract.TrailerEntry.TABLE_NAME, values, selection,
                                    selectionArgs);
                   break;
            case REVIEWS:
                rowsUpdated = db.update(MoviesContract.ReviewEntry.TABLE_NAME, values, selection,
                                    selectionArgs);
                   break;
            default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        if (rowsUpdated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        return rowsUpdated;
    }


    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;

        matcher.addURI(authority, MoviesContract.PATH_MOVIE, MOVIES);
        matcher.addURI(authority, MoviesContract.PATH_TRAILER, TRAILERS);
        matcher.addURI(authority, MoviesContract.PATH_REVIEW, REVIEWS);

        matcher.addURI(authority, MoviesContract.PATH_MOVIE + "/*", MOVIES_WITH_QUERY_STRING);
        matcher.addURI(authority, MoviesContract.PATH_MOVIE_ID + "/*", MOVIES_BY_MOVIE_ID);
        matcher.addURI(authority, MoviesContract.PATH_TRAILER + "/*", TRAILERS_BY_MOVIE_ID);
        matcher.addURI(authority, MoviesContract.PATH_REVIEW + "/*", REVIEWS_BY_MOVIE_ID);

        return matcher;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case TRAILERS: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoviesContract.TrailerEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case REVIEWS: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoviesContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }

}
