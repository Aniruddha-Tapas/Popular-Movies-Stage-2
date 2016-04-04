package com.example.android.popularmovies.app;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import static com.example.android.popularmovies.app.MoviesContract.*;


public class TestProvider  extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    private void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }


        //   Needs the query and delete functions to be completed

    public void deleteAllRecordsFromProvider() {

        mContext.getContentResolver().delete(
                ReviewEntry.CONTENT_URI,
                null,
                null
        );

        mContext.getContentResolver().delete( TrailerEntry.CONTENT_URI, null, null );

        mContext.getContentResolver().delete( MovieEntry.CONTENT_URI, null, null );

        Cursor cursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Review table during delete", 0,
                cursor.getCount());
        cursor.close();


        cursor = mContext.getContentResolver().query( TrailerEntry.CONTENT_URI,
                null, null, null, null );
        assertEquals("Error: Records not deleted from Trailer table during delete", 0,
                cursor.getCount());
        cursor.close();


        cursor = mContext.getContentResolver().query( MovieEntry.CONTENT_URI,
                null, null, null, null );
        if(cursor != null) {
            assertEquals("Error: Records not deleted from Movie table during delete", 0,
                    cursor.getCount());
            cursor.close();
        }

    }


    public void testDeleteRecords() {

        testInsertReadProvider();
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, tco);

        deleteAllRecordsFromProvider();
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);
    }

    public void testBasicMovieQuery() {
        // insert our test records into the database
        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues movieValues = TestUtilities.createMovieValues(mContext);

        long movieRowId = db.insert(MovieEntry.TABLE_NAME, null, movieValues);
        assertTrue("Unable to Insert MovieEntry into the Database", movieRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI, null, null, null, null );

        if(movieCursor != null)
            movieCursor.close();
    }


    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // MovieProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: MovieProvider registered with authority: " + providerInfo.authority +
                    " instead of authority: " + MoviesContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MoviesContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: MoviesProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

     public void testInsertReadProvider() {

         /** Insert a movie and wait to be notified of it  ***/
        ContentValues MovieValues = TestUtilities.createMovieValues(mContext);
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, tco);
        Uri MovieInsertUri = mContext.getContentResolver()
                .insert(MovieEntry.CONTENT_URI, MovieValues);
        assertTrue(MovieInsertUri != null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);
        // Insure it's there by querying for it
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI, null,  null,  null, null
        );
        // Validate it
        TestUtilities.validateCursor("testInsertReadProvider. Error validating MovieEntry insert.",
                movieCursor, MovieValues);
        if(movieCursor != null)
            movieCursor.close();

         /** Insert a trailer and wait to be notified of it  ***/
         ContentValues TrailerValues = TestUtilities.createTrailerValues();
         tco = TestUtilities.getTestContentObserver();
         mContext.getContentResolver().registerContentObserver(TrailerEntry.CONTENT_URI, true, tco);
         Uri TrailerInsertUri = mContext.getContentResolver()
                 .insert(TrailerEntry.CONTENT_URI, TrailerValues);
         assertTrue(TrailerInsertUri != null);
         tco.waitForNotificationOrFail();
         mContext.getContentResolver().unregisterContentObserver(tco);
         // Insure it's there by querying for it
         Cursor trailerCursor = mContext.getContentResolver().query(
                 TrailerEntry.CONTENT_URI, null,  null,  null, null
         );
         // Validate it
         TestUtilities.validateCursor("testInsertReadProvider. Error validating TrailerEntry insert.",
                 trailerCursor, TrailerValues);
         if(trailerCursor != null)
             trailerCursor.close();

         /** Insert a review and wait to be notified of it  ***/
         ContentValues ReviewValues = TestUtilities.createReviewValues();
         tco = TestUtilities.getTestContentObserver();
         mContext.getContentResolver().registerContentObserver(ReviewEntry.CONTENT_URI, true, tco);
         Uri ReviewInsertUri = mContext.getContentResolver()
                 .insert(ReviewEntry.CONTENT_URI, ReviewValues);
         assertTrue(ReviewInsertUri != null);
         tco.waitForNotificationOrFail();
         mContext.getContentResolver().unregisterContentObserver(tco);
         // Insure it's there by querying for it
         Cursor reviewCursor = mContext.getContentResolver().query(
                 ReviewEntry.CONTENT_URI, null,  null,  null, null
         );
         // Validate it
         TestUtilities.validateCursor("testInsertReadProvider. Error validating ReviewEntry insert.",
                 reviewCursor, ReviewValues);
         if(reviewCursor != null)
             reviewCursor.close();

     }


    public void testUpdateReadProvider() {

        /** Insert a movie and wait to be notified of it  ***/
        ContentValues MovieValues = TestUtilities.createMovieValues(mContext);
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, tco);
        Uri MovieInsertUri = mContext.getContentResolver()
                .insert(MovieEntry.CONTENT_URI, MovieValues);
        assertTrue(MovieInsertUri != null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);
        // Insure it's there by querying for it
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI, null,  null,  null, null
        );
        // Validate it
        TestUtilities.validateCursor("testInsertReadProvider. Error validating MovieEntry insert.",
                movieCursor, MovieValues);

        // Update favorites to "1"
        MovieValues.put(MovieEntry.COLUMN_FAVORITE, 1);
        int rowsUpdated = mContext.getContentResolver()
                .update(MovieEntry.CONTENT_URI,
                        MovieValues,
                        MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[] {"140607"}
                );
        assertTrue(rowsUpdated == 1);

        if(movieCursor != null)
            movieCursor.close();


    }


    static private final int BULK_INSERT_RECORDS_TO_INSERT = 20;

    public void testBulkInsert() {

        ContentValues[] bulkInsertContentValues = TestUtilities.createBulkInsertMovieContentValues(mContext);

        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, movieObserver);
        int insertCount = mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, bulkInsertContentValues);
        movieObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(movieObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating MovieEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }
}


