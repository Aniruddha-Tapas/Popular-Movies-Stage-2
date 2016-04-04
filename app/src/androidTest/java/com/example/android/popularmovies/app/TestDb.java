/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

import com.example.android.popularmovies.app.MoviesContract.MovieEntry;
import com.example.android.popularmovies.app.MoviesContract.TrailerEntry;


public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();


    void deleteTheDatabase() {
        mContext.deleteDatabase(MoviesDbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {

        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieEntry.TABLE_NAME);
        tableNameHashSet.add(TrailerEntry.TABLE_NAME);

        mContext.deleteDatabase(MoviesDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MoviesDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: The movie database has not been created correctly",
                cursor.moveToFirst());

        do {
            tableNameHashSet.remove(cursor.getString(0));
        } while (cursor.moveToNext());

        assertTrue("Error: Your database was created but is missing the movie and trailer tables",
                tableNameHashSet.isEmpty());

        cursor = db.rawQuery("PRAGMA table_info(" + MovieEntry.TABLE_NAME + ")", null);

        assertTrue("Error: Unable to query the database for table information.",
                cursor.moveToFirst());

        final HashSet<String> movieColumnHashSet = new HashSet<String>();
        movieColumnHashSet.add(MovieEntry._ID);
        movieColumnHashSet.add(MovieEntry.COLUMN_POSTER_PATH);
        movieColumnHashSet.add(MovieEntry.COLUMN_ADULT);
        movieColumnHashSet.add(MovieEntry.COLUMN_OVERVIEW);
        movieColumnHashSet.add(MovieEntry.COLUMN_RELEASE_DATE);
        movieColumnHashSet.add(MovieEntry.COLUMN_MOVIE_ID);
        movieColumnHashSet.add(MovieEntry.COLUMN_ORIGINAL_TITLE);
        movieColumnHashSet.add(MovieEntry.COLUMN_ORIGINAL_LANGUAGE);
        movieColumnHashSet.add(MovieEntry.COLUMN_TITLE);
        movieColumnHashSet.add(MovieEntry.COLUMN_BACKDROP_PATH);
        movieColumnHashSet.add(MovieEntry.COLUMN_POPULARITY);
        movieColumnHashSet.add(MovieEntry.COLUMN_VOTE_COUNT);
        movieColumnHashSet.add(MovieEntry.COLUMN_VIDEO);
        movieColumnHashSet.add(MovieEntry.COLUMN_VOTE_AVERAGE);
        movieColumnHashSet.add(MovieEntry.COLUMN_GENRE_IDS);
        movieColumnHashSet.add(MovieEntry.COLUMN_FAVORITE);
        movieColumnHashSet.add(MovieEntry.COLUMN_WATCHED);
        movieColumnHashSet.add(MovieEntry.COLUMN_WATCH_ME);
        movieColumnHashSet.add(MovieEntry.COLUMN_POSTER_BITMAP);

        int columnNameIndex = cursor.getColumnIndex("name");
        do {
            String columnName = cursor.getString(columnNameIndex);
            movieColumnHashSet.remove(columnName);
        } while (cursor.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required movie entry columns",
                movieColumnHashSet.isEmpty());
        db.close();
    }

    public void testMovieTable() {
        insertMovie();
    }

    public void testTrailerTable() {

        long movieRowId = insertMovie();

        assertFalse("Error: Movie Not Inserted Correctly", movieRowId == -1L);

        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues trailerValues = TestUtilities.createTrailerValues();

        long trailerRowId = db.insert(MoviesContract.TrailerEntry.TABLE_NAME, null, trailerValues);
        assertTrue(trailerRowId != -1);

        Cursor trailerCursor = db.query(
                MoviesContract.TrailerEntry.TABLE_NAME, null, null, null, null, null, null);

        assertTrue("Error: No Records returned from trailer query", trailerCursor.moveToFirst());

        TestUtilities.validateCurrentRecord("testInsertReadDb trailerrEntry failed to validate",
                trailerCursor, trailerValues);

        assertFalse("Error: More than one record returned from trailer query",
                trailerCursor.moveToNext());

        trailerCursor.close();
        dbHelper.close();
    }

    public void testReviewTable() {

        long movieRowId = insertMovie();

        assertFalse("Error: Movie Not Inserted Correctly", movieRowId == -1L);

        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues reviewValues = TestUtilities.createReviewValues();

        long reviewRowId = db.insert(MoviesContract.ReviewEntry.TABLE_NAME, null, reviewValues);
        assertTrue(reviewRowId != -1);

        Cursor reviewCursor = db.query(
                MoviesContract.ReviewEntry.TABLE_NAME, null, null, null, null, null, null);

        assertTrue("Error: No Records returned from review query", reviewCursor.moveToFirst());

        TestUtilities.validateCurrentRecord("testInsertReadDb trailerrEntry failed to validate",
                reviewCursor, reviewValues);

        assertFalse("Error: More than one record returned from trailer query",
                reviewCursor.moveToNext());

        reviewCursor.close();
        dbHelper.close();
    }


    public long insertMovie() {

        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createMovieValues(mContext);

        long movieRowId = db.insert(MovieEntry.TABLE_NAME, null, testValues);

        assertTrue(movieRowId != -1);

        Cursor cursor = db.query(
                MoviesContract.MovieEntry.TABLE_NAME, null, null, null, null, null, null);

        assertTrue("Error: No Records returned from movie query", cursor.moveToFirst());

        TestUtilities.validateCurrentRecord("Error: Movie Query Validation Failed",
                cursor, testValues);

        assertFalse("Error: More than one record returned from movie query",
                cursor.moveToNext());

        cursor.close();
        db.close();
        return movieRowId;
    }


}