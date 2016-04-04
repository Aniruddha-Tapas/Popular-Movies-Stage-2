
package com.example.android.popularmovies.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.app.MoviesContract.MovieEntry;
import com.example.android.popularmovies.app.MoviesContract.TrailerEntry;
import com.example.android.popularmovies.app.MoviesContract.ReviewEntry;

public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;

    static final String DATABASE_NAME = "movie.db";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +

                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                MovieEntry.COLUMN_POSTER_PATH + " TEXT, " +
                MovieEntry.COLUMN_ADULT + " INTEGER, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT, " +
                MovieEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT, " +
                MovieEntry.COLUMN_TITLE + " TEXT, " +
                MovieEntry.COLUMN_BACKDROP_PATH + " TEXT, " +
                MovieEntry.COLUMN_POPULARITY + " REAL, " +
                MovieEntry.COLUMN_VOTE_COUNT + " INTEGER, " +
                MovieEntry.COLUMN_VIDEO + " INTEGER, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL , " +
                MovieEntry.COLUMN_GENRE_IDS + " TEXT, " +
                MovieEntry.COLUMN_FAVORITE + " INTEGER, " +
                MovieEntry.COLUMN_WATCHED + " INTEGER, " +
                MovieEntry.COLUMN_WATCH_ME + " INTEGER, " +
                MovieEntry.COLUMN_POSTER_BITMAP + " BLOB, " +

                // Should only be one entry for this movie
                " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID  + ") ON CONFLICT REPLACE);";


        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + TrailerEntry.TABLE_NAME + " (" +


                TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL , " +
                TrailerEntry.COLUMN_NAME + " TEXT , " +
                TrailerEntry.COLUMN_SIZE + " TEXT , " +
                TrailerEntry.COLUMN_SOURCE + " TEXT NOT NULL , " +
                TrailerEntry.COLUMN_TYPE + " TEXT  , " +

                " FOREIGN KEY (" + TrailerEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_MOVIE_ID + "), " +

                // Should only be one entry for this movie with this trailer source
                " UNIQUE (" + TrailerEntry.COLUMN_MOVIE_ID + ", " +
                TrailerEntry.COLUMN_SOURCE + ") ON CONFLICT REPLACE);";


        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +

                ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL , " +
                ReviewEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL , " +
                ReviewEntry.COLUMN_AUTHOR + " TEXT , " +
                ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL , " +
                ReviewEntry.COLUMN_URL + " TEXT , " +

                " FOREIGN KEY (" + ReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_MOVIE_ID + "), " +

                // Should only be one entry for this movie with this review ID
                " UNIQUE (" + ReviewEntry.COLUMN_MOVIE_ID + ", " +
                ReviewEntry.COLUMN_REVIEW_ID + ") ON CONFLICT REPLACE);";


        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
