package com.example.android.popularmovies.app;


import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

public class TestUriMatcher extends AndroidTestCase {


    private static final Uri TEST_MOVIES_DIR = MoviesContract.MovieEntry.CONTENT_URI;

    private static final Uri TEST_FAVORITE_MOVIES_QUERY_STRING =
            MoviesContract.MovieEntry.buildMoviesUriWithQueryParameters(
                    MoviesContract.MovieEntry.CONTENT_URI,
                    new String[]{"which_movies"},
                    new String[]{"favorite_movies"}
            );

    private static final Uri TEST_SORT_ORDER_QUERY_STRING =
            MoviesContract.MovieEntry.buildMoviesUriWithQueryParameters(
                    MoviesContract.MovieEntry.CONTENT_URI,
                    new String[]{"sort_order"},
                    new String[]{"none"}
            );
    private static final Uri TEST_VOTE_COUNT_QUERY_STRING =
            MoviesContract.MovieEntry.buildMoviesUriWithQueryParameters(
                    MoviesContract.MovieEntry.CONTENT_URI,
                    new String[]{"vote_count"},
                    new String[]{"10"}
            );
    private static final Uri TEST_TIME_PERIOD_QUERY_STRING =
            MoviesContract.MovieEntry.buildMoviesUriWithQueryParameters(
                    MoviesContract.MovieEntry.CONTENT_URI,
                    new String[]{"time_period"},
                    new String[]{"This Entire Week"}
            );
    private static final Uri TEST_GENRES_QUERY_STRING =
            MoviesContract.MovieEntry.buildMoviesUriWithQueryParameters(
                    MoviesContract.MovieEntry.CONTENT_URI,
                    new String[]{"genre_ids"},
                    new String[]{"1,2,3"}
            );

    private static final Uri TEST_ALL_QUERY_STRINGS =
            MoviesContract.MovieEntry.buildMoviesUriWithQueryParameters(
                    MoviesContract.MovieEntry.CONTENT_URI,
                    new String[]{"which_movies", "sort_order", "vote_count", "time_period", "genre_ids"},
                    new String[]{"favorite_movies", "none", "10", "This Entire Week", "1,2,3"}
            );

    /*
        Students: This function tests that your UriMatcher returns the correct integer value
        for each of the Uri types that our ContentProvider can handle.  Uncomment this when you are
        ready to test your UriMatcher.
     */
    public void testUriMatcher() {
        UriMatcher testMatcher = MovieProvider.buildUriMatcher();
//
        assertEquals("Error: The MOVIES URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIES_DIR), MovieProvider.MOVIES);
        assertEquals("Error: The MOVIES WITH QUERY STRING URI was matched incorrectly.",
                testMatcher.match(TEST_FAVORITE_MOVIES_QUERY_STRING), MovieProvider.MOVIES_WITH_QUERY_STRING);
        assertEquals("Error: The MOVIES WITH QUERY STRING URI was matched incorrectly.",
                testMatcher.match(TEST_ALL_QUERY_STRINGS), MovieProvider.MOVIES_WITH_QUERY_STRING);

    }
}
