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

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.net.URI;


public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_REVIEW = "review";
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_MOVIE_ID = "movie_id";



    public static final class TrailerEntry implements BaseColumns {


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        public static final String TABLE_NAME = "trailer";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_SOURCE = "source";
        public static final String COLUMN_TYPE = "trailer";


        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTrailerWithMovieId(String movieID) {
            return CONTENT_URI.buildUpon().appendPath(movieID).build();
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }


    }

    public static final class ReviewEntry implements BaseColumns {


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;


        public static final String TABLE_NAME = "review";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_REVIEW_ID = "review_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "review_content";
        public static final String COLUMN_URL = "url";


        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildReviewWithMovieId(String movieID) {
            return CONTENT_URI.buildUpon().appendPath(movieID).build();
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_ADULT = "adult";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_VIDEO = "video";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_GENRE_IDS = "genre_ids";
        public static final String COLUMN_FAVORITE = "favorite";
        public static final String COLUMN_WATCHED = "watched";
        public static final String COLUMN_WATCH_ME = "watch_me";
        public static final String COLUMN_POSTER_BITMAP = "poster_bitmap";


    // Build a URI for getting details for a single movie from its movie_id
    public static Uri buildMovieUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    public static Uri buildMovieWithMovieId(String movieID) {
        return BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE_ID)
                .appendPath(movieID).build();
    }

    public static String getMovieIdFromUri(Uri uri) {
        return uri.getPathSegments().get(1);
    }



    public static Uri buildMoviesUri(){
            return CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();
        }

        public static Uri buildMoviesUriWithQueryParameters(Uri uri, String[] keys, String[] values){
            Uri newUri = uri.buildUpon().appendPath("/").build();

            for (int i=0;i<keys.length;i++) {

                switch (keys[i]){
                    case "data_source":
                        newUri = appendUriWithDataSource(newUri, values[i]);
                        break;
                    case "vote_count":
                        newUri = appendUriWithVoteCount(newUri, values[i]);
                        break;
                    case "time_period":
                        newUri = appendUriWithTimePeriod(newUri, values[i]);
                        break;
                    case "genre_ids":
                        if(values[i] != null &&  !values[i].isEmpty())
                            newUri = appendUriWithGenres(newUri, values[i]);
                        break;
                    case "sort_order":
                        newUri = appendUriWithSortOrder(newUri, values[i]);
                        break;
                    default:
                        break;
                }
            }
            return newUri;
        }
        
        public static Uri appendUriWithDataSource(Uri oldUri, String dataSource){
            return oldUri.buildUpon().appendQueryParameter("data_source", dataSource).build();
        }

        public static Uri appendUriWithVoteCount(Uri oldUri, String voteCount){
            return oldUri.buildUpon().appendQueryParameter("vote_count", voteCount).build();
        }

        public static Uri appendUriWithTimePeriod(Uri oldUri, String timePeriod){
            return oldUri.buildUpon().appendQueryParameter("time_period", timePeriod).build();
        }

        public static Uri appendUriWithGenres(Uri oldUri, String genres){
            return oldUri.buildUpon().appendQueryParameter("genre_ids", genres).build();
        }

        public static Uri appendUriWithSortOrder(Uri oldUri, String sortOrder){
            return oldUri.buildUpon().appendQueryParameter("sort_order", sortOrder).build();
        }


        public static String getDataSourceFromUri(Uri uri){
            return uri.getQueryParameter("data_source");
        }

        public static String getVoteCountFromUri(Uri uri){
            return uri.getQueryParameter("vote_count");
        }

        public static String getTimePeriodFromUri(Uri uri){
            return uri.getQueryParameter("time_period");
        }

        public static String getGenreIdsFromUri(Uri uri){
            return uri.getQueryParameter("genre_ids");
        }

        public static String getSortOrderFromUri(Uri uri){
            return uri.getQueryParameter("sort_order");
        }

    }


}
