package com.example.android.popularmovies.app;

import android.net.Uri;
import android.test.AndroidTestCase;

// NOTE:   These tests are NOT comprehensive.  They are only sanity checks on each of
//       the memeber functions to makse sure a basic query string can be parsed.
//      Notably missing:  Bad data inputs (Nulls, empty strings, numbers instead of strings, etc.)
//          All of this could work and the cursors could still fail

public class TestMovieCursorQueryParameters extends AndroidTestCase {

    public static final String[] TEST_KEYS = new String[]{
            "data_source", "vote_count", "time_period", "genre_ids", "sort_order"
    };

    public static final String[] TEST_VALUES = new String[]{
            "favorite", "10", "1980", "1,2,35793", "none"
    };

    private static final Uri TEST_ALL_QUERY_STRINGS =
            MoviesContract.MovieEntry.buildMoviesUriWithQueryParameters(
                    MoviesContract.MovieEntry.CONTENT_URI,
                    TEST_KEYS,
                    TEST_VALUES
            );


    public static final String sDataSourceFavoritesSelection =
            MoviesContract.MovieEntry.TABLE_NAME + "."
                    + MoviesContract.MovieEntry.COLUMN_FAVORITE + " = ? ";

    public static final String sDataSourceFavoritesSelectionArgs = "1";   // true


    public void testDataSourceFavoritesSelection() {

        MovieCursorQueryParameters mcqp = new MovieCursorQueryParameters(TEST_ALL_QUERY_STRINGS);

        assertEquals("Error: the favorite_movies selection string was formed improperly.",
                mcqp.getDataSourceSelectionString(), sDataSourceFavoritesSelection);

    }

    public void testDataSourceFavoritesSelectionArgs() {

        MovieCursorQueryParameters mcqp = new MovieCursorQueryParameters(TEST_ALL_QUERY_STRINGS);

        assertEquals("Error: the favorite_movies Selection Arg string was formed improperly.",
                mcqp.getDataSourceSelectionStringArgs(), sDataSourceFavoritesSelectionArgs);

    }

    public static final String sVoteCountSelection =
            MoviesContract.MovieEntry.TABLE_NAME + "."
                    + MoviesContract.MovieEntry.COLUMN_VOTE_COUNT + " = ? ";

    public static final String sVoteCountSelectionArgs = "10";   // true


    public void testVoteCountSelection() {

        MovieCursorQueryParameters mcqp = new MovieCursorQueryParameters(TEST_ALL_QUERY_STRINGS);

        assertEquals("Error: the vote_count selection string was formed improperly.",
                mcqp.getVoteCountSelectionString(), sVoteCountSelection);

    }

    public void testVoteCountSelectionArgs() {

        MovieCursorQueryParameters mcqp = new MovieCursorQueryParameters(TEST_ALL_QUERY_STRINGS);

        assertEquals("Error: the vote_count Selection Arg was formed improperly.",
                mcqp.getVoteCountSelectionArg(), sVoteCountSelectionArgs);

    }

    public static final String sTimePeriod1980sSelection =
            MoviesContract.MovieEntry.TABLE_NAME + "."
                    + MoviesContract.MovieEntry.COLUMN_RELEASE_DATE +
                    " BETWEEN ? AND ? ";

    public void testTimePeriod1980sSelection() {

        MovieCursorQueryParameters mcqp = new MovieCursorQueryParameters(TEST_ALL_QUERY_STRINGS);

        assertEquals("Error: the 1980s time_period selection string was formed improperly.",
                mcqp.getTimePeriodSelectionString(), sTimePeriod1980sSelection);

    }

    public void testTimePeriod1980sSelectionArgs() {

        MovieCursorQueryParameters mcqp = new MovieCursorQueryParameters(TEST_ALL_QUERY_STRINGS);
        String[] range = mcqp.getTimePeriodSelectionArgs();

        assertEquals("Error: the 1980s lower time_period SelectionArg was formed improperly.",
                range[0], "1980-1-1");

        assertEquals("Error: the 1980s upper time_period SelectionArg was formed improperly.",
                range[1], "1989-12-31");

    }

    private static final Uri TEST_UPPER_TIME_PERIOD_QUERY_STRINGS =
            MoviesContract.MovieEntry.buildMoviesUriWithQueryParameters(
                    MoviesContract.MovieEntry.CONTENT_URI,
                    new String[]{"time_period"},
                    new String[]{"before_1920"}
            );

    public static final String sTimePeriodBefore1920sSelection =
            MoviesContract.MovieEntry.TABLE_NAME + "."
                    + MoviesContract.MovieEntry.COLUMN_RELEASE_DATE +
                    " <= ? ";


    public void testTimePeriodBefore1920sSelection() {

        MovieCursorQueryParameters mcqp =
                new MovieCursorQueryParameters(TEST_UPPER_TIME_PERIOD_QUERY_STRINGS);

        assertEquals("Error: the before 1920s  time_period selection string was formed improperly.",
                mcqp.getTimePeriodSelectionString(), sTimePeriodBefore1920sSelection);

    }

    public void testTimePeriodBefore1920sSelectionArgs() {

        MovieCursorQueryParameters mcqp = new
                MovieCursorQueryParameters(TEST_UPPER_TIME_PERIOD_QUERY_STRINGS);
        String[] range = mcqp.getTimePeriodSelectionArgs();

        assertEquals("Error: the before 1920s time_period SelectionArg was formed improperly.",
                range[0], "1919-12-31");

    }


    //  This tests that 3 ids are in the string
    public static final String sGenreIdsSelection =
            " instr(" +
                MoviesContract.MovieEntry.TABLE_NAME + "."
                    + MoviesContract.MovieEntry.COLUMN_GENRE_IDS +
                   ", ? ) > 0 " + " AND " +
            " instr(" +
                MoviesContract.MovieEntry.TABLE_NAME + "."
                    + MoviesContract.MovieEntry.COLUMN_GENRE_IDS +
                    ", ? ) > 0 " + " AND " +
            " instr(" +
                MoviesContract.MovieEntry.TABLE_NAME + "."
                    + MoviesContract.MovieEntry.COLUMN_GENRE_IDS +
                    ", ? ) > 0 " ;

    public static final String[] sGenreIdsSelectionArgs = {"1", "2", "35793"};   // true


    public void testGenreIdSelection() {

        MovieCursorQueryParameters mcqp = new MovieCursorQueryParameters(TEST_ALL_QUERY_STRINGS);

        assertEquals("Error: the genre_ids selection string was formed improperly.",
                mcqp.getGenreIdsSelectionString(), sGenreIdsSelection);

    }

    public void testGenreIdSelectionArgs() {

        MovieCursorQueryParameters mcqp = new MovieCursorQueryParameters(TEST_ALL_QUERY_STRINGS);

        String[] genreIds = mcqp.getGenreIdsSelectionArgs();

        assertEquals("Error: the first genreId Selection Arg was formed improperly.",
                genreIds[0], sGenreIdsSelectionArgs[0]);
        assertEquals("Error: the first genreId Selection Arg was formed improperly.",
                genreIds[1], sGenreIdsSelectionArgs[1]);
        assertEquals("Error: the first genreId Selection Arg was formed improperly.",
                genreIds[2], sGenreIdsSelectionArgs[2]);

    }


    public static final String sAllSelectionStrings =
            sDataSourceFavoritesSelection + " AND " +
            sVoteCountSelection + " AND " +
            sTimePeriod1980sSelection  + " AND " +
                    sGenreIdsSelection;

    public void testAllSelectionStrings() {

        MovieCursorQueryParameters mcqp = new MovieCursorQueryParameters(TEST_ALL_QUERY_STRINGS);

        assertEquals("Error testing All selections: the selection string was formed improperly.",
                mcqp.getSelection(), sAllSelectionStrings);

    }

    public static final String[] sAllSelectionArgs = new String[]{
            sDataSourceFavoritesSelectionArgs,
            sVoteCountSelectionArgs,
            "1980-1-1", "1989-12-31",
            "1", "2", "35793"};  // sGenreIdsSelectionArgs }


    public void testAllSelectionArgs() {

        MovieCursorQueryParameters mcqp = new MovieCursorQueryParameters(TEST_ALL_QUERY_STRINGS);

        String[] selectionArgs = mcqp.getSelectionArgs();

        for (int i = 0; i < selectionArgs.length; i++) {
            assertEquals("Error testing all Selection Args. Arg "+ (i+1) + " was formed improperly.",
                    selectionArgs[i], sAllSelectionArgs[i]);
        }
    }

    public static final String sSortOrderNone = "";   // true

    public void testSortOrder(){

        MovieCursorQueryParameters mcqp = new MovieCursorQueryParameters(TEST_ALL_QUERY_STRINGS);

        assertEquals("Error:  Sort Order was formed improperly.",
                mcqp.getSortOrder(), sSortOrderNone);

    }
}