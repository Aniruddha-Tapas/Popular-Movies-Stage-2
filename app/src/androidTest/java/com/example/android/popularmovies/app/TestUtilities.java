package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;


import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Set;

public class TestUtilities extends AndroidTestCase {

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();

            if(!entry.getKey().equals("poster_bitmap"))
                assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createMovieValues(Context context) {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, "/fYzpM9GmpBlIC893fNjoWCwE24H.jpg");
        movieValues.put(MoviesContract.MovieEntry.COLUMN_ADULT, 0);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, "Thirty years after defeating the Galactic Empire, Han Solo and his allies face a new threat from the evil Kylo Ren and his army of Stormtroopers.");
        movieValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, "2015-12-18");
        movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, 140607);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "Star Wars: The Force Awakens");
        movieValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, "en");
        movieValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, "Star Wars: The Force Awakens");
        movieValues.put(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH, "/njv65RTipNSTozFLuF85jL0bcQe.jpg");
        movieValues.put(MoviesContract.MovieEntry.COLUMN_POPULARITY, 79.08);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_COUNT, 1426);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_VIDEO, 0);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, 8.05);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_GENRE_IDS, "28,12,878,14");
        movieValues.put(MoviesContract.MovieEntry.COLUMN_FAVORITE, 0);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_WATCHED, 0);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_WATCH_ME, 0);

        byte[] img=getBytes(BitmapFactory.decodeResource(context.getResources(), R.drawable.force_awakens));
        movieValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_BITMAP, img);

        return movieValues;
    }


        public static byte[] getBytes(Bitmap bitmap)
        {
            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,0, stream);
            return stream.toByteArray();
        }

        public static Bitmap getImage(byte[] image)
        {
            return BitmapFactory.decodeByteArray(image, 0, image.length);
        }



     static ContentValues[] createBulkInsertMovieContentValues(Context context){

        ContentValues[] cv = new ContentValues[20];

        for (int i = 0; i < 20; i++) {
            ContentValues movieValues = new ContentValues();
            movieValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, "/fYzpM9GmpBlIC893fNjoWCwE24H.jpg");
            movieValues.put(MoviesContract.MovieEntry.COLUMN_ADULT, 0);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, "Thirty years after defeating the Galactic Empire, Han Solo and his allies face a new threat from the evil Kylo Ren and his army of Stormtroopers.");
            movieValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, "2015-12-18");
            movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, 140607 + i);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "Star Wars: The Force Awakens");
            movieValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, "en");
            movieValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, i + "Star Wars: The Force Awakens");
            movieValues.put(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH, "/njv65RTipNSTozFLuF85jL0bcQe.jpg");
            movieValues.put(MoviesContract.MovieEntry.COLUMN_POPULARITY, i+79.08);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_COUNT, 1426-i);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_VIDEO, 0);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, (i/10) + 8.05);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_GENRE_IDS, "28,12,878,14");
            movieValues.put(MoviesContract.MovieEntry.COLUMN_FAVORITE, Math.round(Math.random()));
            movieValues.put(MoviesContract.MovieEntry.COLUMN_WATCHED, 0);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_WATCH_ME, 0);

            byte[] img=getBytes(BitmapFactory.decodeResource(context.getResources(), R.drawable.force_awakens));
            movieValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_BITMAP, img);

            cv[i] = movieValues;
        }
        return cv;

    }

    static ContentValues createTrailerValues() {

        ContentValues trailerValues = new ContentValues();
        trailerValues.put(MoviesContract.TrailerEntry.COLUMN_MOVIE_ID, 140607);
        trailerValues.put(MoviesContract.TrailerEntry.COLUMN_NAME, "Teaser");
        trailerValues.put(MoviesContract.TrailerEntry.COLUMN_SIZE, "HD");
        trailerValues.put(MoviesContract.TrailerEntry.COLUMN_SOURCE, "OMOVFvcNfvE");
        trailerValues.put(MoviesContract.TrailerEntry.COLUMN_TYPE, "Trailer");

        return trailerValues;
    }

    static ContentValues createReviewValues() {

        ContentValues testValues = new ContentValues();
        testValues.put(MoviesContract.ReviewEntry.COLUMN_MOVIE_ID, 140607);
        testValues.put(MoviesContract.ReviewEntry.COLUMN_REVIEW_ID, "5675fd7792514179e7003dc5");
        testValues.put(MoviesContract.ReviewEntry.COLUMN_AUTHOR, "Frank Ochieng");
        testValues.put(MoviesContract.ReviewEntry.COLUMN_CONTENT, "So where were you when the Science Fiction cinema sensation ‘Star Wars’ took shape and captured the imagination of the massive global moviegoers’ escapist expectations back in 1977? Regardless of whether you existed thirty-eight years ago or not, the legend of George Lucas’ highly");
        testValues.put(MoviesContract.ReviewEntry.COLUMN_URL, "http://j.mp/1ODjyR4");

        return testValues;
    }

     long insertMovieValues(Context context) {
        MoviesDbHelper dbHelper = new MoviesDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = createMovieValues(context);

        long movieRowId = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, testValues);

        assertTrue("Error: Failure to insert Movie Values", movieRowId != -1);

        return movieRowId;
    }


    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
