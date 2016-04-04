package com.example.android.popularmovies.app;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class MovieCursorQueryParameters {

   //  static
    Uri mUri;


    public MovieCursorQueryParameters(Uri mUri) {
        this.mUri = mUri;
    }

    public static final String LOG_TAG = MovieCursorQueryParameters.class.getSimpleName();

    public String getSelection(){

        // Log.v(LOG_TAG, "*** getSelection URI: "  + mUri.toString());


        StringBuilder selection = new StringBuilder();

        /* Work Backwards so all the ANDs are in the proper places */

        String genreIds = getGenreIdsSelectionString();
        if ( genreIds != null)
            selection.insert(0, genreIds );  // No AND here;  Make sure it's always first!

        String timePeriods = getTimePeriodSelectionString();
        if ( timePeriods != null)
            selection.insert(0, timePeriods + appendAndIfNeeded(selection) );

        String voteCount = getVoteCountSelectionString();
        if ( voteCount != null)
            selection.insert(0, voteCount + appendAndIfNeeded(selection));

        String dataSource = getDataSourceSelectionString();
        if ( dataSource != null)
            selection.insert(0, dataSource + appendAndIfNeeded(selection));

        // Log.v(LOG_TAG, "*** getSelection string: " + selection.toString());

        if(selection.toString().equals(""))
            return null;  // Makes sure the cursor has a "no args" case

        return selection.toString();
    }

    private String appendAndIfNeeded(StringBuilder selection) {
        if(selection != null && selection.length() > 0)
            return " AND ";
        else
            return "";

    }


    public String[] getSelectionArgs (){

        List<String> selectionArgsList = new ArrayList<>();

        String dataSource = getDataSourceSelectionStringArgs();
        if(dataSource != null)
            selectionArgsList.add(dataSource);

        String voteCount = getVoteCountSelectionArg();
        if(voteCount != null)
            selectionArgsList.add(voteCount);

        String[] timePeriods = getTimePeriodSelectionArgs();
        if(timePeriods != null) {
            List<String> periods = Arrays.asList(timePeriods);
            selectionArgsList.addAll(periods);
        }

        String[] genreIds = getGenreIdsSelectionArgs();
        if(genreIds != null) {
            List<String> genres = Arrays.asList(genreIds);
            selectionArgsList.addAll(genres);
        }

        String[] selectionArgs;
        if(selectionArgsList.size() > 0) {
            selectionArgs = new String[selectionArgsList.size()];
            selectionArgs = selectionArgsList.toArray(selectionArgs);
        }
        else
            selectionArgs = null;  // Makes sure the cursor has a "no args" case


       //  Log.v(LOG_TAG, "*** getSelectionArgs : "  + selectionArgsList.toString());

        return selectionArgs;
    }


    @Nullable
   public String getDataSourceSelectionString() {
        String dataSource = MoviesContract.MovieEntry.getDataSourceFromUri(mUri);

        if(dataSource != null) {
            switch (dataSource) {

                // return nothing;  parameter doesn't get added
                case "all":
                    return null;
                // return
                case "favorite":
                    return
                            MoviesContract.MovieEntry.TABLE_NAME + "." +
                                    MoviesContract.MovieEntry.COLUMN_FAVORITE + " = ? ";
                case "watched":
                    return
                            MoviesContract.MovieEntry.TABLE_NAME + "." +
                                    MoviesContract.MovieEntry.COLUMN_WATCHED + " = ? ";
                case "watch_me":
                    return
                            MoviesContract.MovieEntry.TABLE_NAME + "." +
                                    MoviesContract.MovieEntry.COLUMN_WATCH_ME + " = ? ";
                default:
                    return null;
            }
        }
        else
            return null;
    }


    @Nullable
    public String getDataSourceSelectionStringArgs() {
        String dataSource = MoviesContract.MovieEntry.getDataSourceFromUri(mUri);

        if(dataSource != null) {

            switch (dataSource) {

                // return nothing;  parameter doesn't get added
                case "all":
                    return null;
                // return
                case "favorite":
                    return "1";
                case "watched":
                    return "1";
                case "watch_me":
                    return "1";
                default:
                    return null;
            }
        }
        else
            return null;
    }

    @Nullable
    public String getVoteCountSelectionString() {
        String voteCount =   MoviesContract.MovieEntry.getVoteCountFromUri(mUri);
        if(voteCount != null && !voteCount.equals("0"))
            return MoviesContract.MovieEntry.TABLE_NAME + "." +
                    MoviesContract.MovieEntry.COLUMN_VOTE_COUNT + " = ? ";
        else
            return null;

    }

    public String getVoteCountSelectionArg() {
        String voteCount =  MoviesContract.MovieEntry.getVoteCountFromUri(mUri);
        if(voteCount != null && !voteCount.equals("0"))
            return voteCount;
        else
            return null;

    }

    @Nullable
    public String getTimePeriodSelectionString() {
        String timePeriod =   MoviesContract.MovieEntry.getTimePeriodFromUri(mUri);
        if(timePeriod != null && !timePeriod.equals("all")) {
            TimePeriod tp = new TimePeriod(timePeriod);

            if(tp.isDateRange()){
                return  MoviesContract.MovieEntry.TABLE_NAME + "." +
                        MoviesContract.MovieEntry.COLUMN_RELEASE_DATE + " BETWEEN ? AND ? ";
            } else if(tp.isOnlyAfter()) {
                return MoviesContract.MovieEntry.TABLE_NAME + "." +
                        MoviesContract.MovieEntry.COLUMN_RELEASE_DATE + " >= ? ";
            } else {
                return MoviesContract.MovieEntry.TABLE_NAME + "." +
                        MoviesContract.MovieEntry.COLUMN_RELEASE_DATE + " <= ? ";
            }

        }
        else
            return null;

    }

    public String[] getTimePeriodSelectionArgs() {
        String timePeriod =   MoviesContract.MovieEntry.getTimePeriodFromUri(mUri);
        if(timePeriod != null  && !timePeriod.equals("all")) {
            TimePeriod tp = new TimePeriod(timePeriod);

            if(tp.isDateRange()){
                return  new String[] {tp.getLowerDate(), tp.getUpperDate()};
            } else if(tp.isOnlyAfter()) {
                return new String[] {tp.getLowerDate()};
            } else {
                return new String[] {tp.getUpperDate()};
            }

        }
        else
            return null;

    }


    public String getGenreIdsSelectionString() {
        String genreIds =   MoviesContract.MovieEntry.getGenreIdsFromUri(mUri);
        if(genreIds != null) {
            String[] ids = genreIds.split(Pattern.quote(","));

            StringBuilder whereClause = new StringBuilder();
            whereClause.append(
                    " instr(" +   MoviesContract.MovieEntry.TABLE_NAME + "."
                    + MoviesContract.MovieEntry.COLUMN_GENRE_IDS +  ", ? ) > 0 "
            );

            for (int i = 0; i < ids.length -1 ; i++) {
                whereClause.insert(0,
                        " instr(" + MoviesContract.MovieEntry.TABLE_NAME + "."
                        + MoviesContract.MovieEntry.COLUMN_GENRE_IDS +  ", ? ) > 0 " + " AND "
                );
            }
            return whereClause.toString();
        }
        else
            return null;

    }

    public String[] getGenreIdsSelectionArgs() {
        String genreIds =   MoviesContract.MovieEntry.getGenreIdsFromUri(mUri);
        if(genreIds != null)
            return genreIds.split(Pattern.quote(","));
        else
            return null;
    }

    public String getSortOrder(){

        String sortOrder = MoviesContract.MovieEntry.getSortOrderFromUri(mUri);

        switch (sortOrder){
            case "none":
                return "";
            case "popularity.desc":
                return MoviesContract.MovieEntry.TABLE_NAME + "."
                        + MoviesContract.MovieEntry.COLUMN_POPULARITY +
                        " DESC ";
            case "vote_average.desc":
                return MoviesContract.MovieEntry.TABLE_NAME + "."
                        + MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE +
                        " DESC ";
            case "release_date.desc":
                return MoviesContract.MovieEntry.TABLE_NAME + "."
                        + MoviesContract.MovieEntry.COLUMN_RELEASE_DATE +
                        " DESC ";
            case "primary_release_date.desc":
                return MoviesContract.MovieEntry.TABLE_NAME + "."
                        + MoviesContract.MovieEntry.COLUMN_RELEASE_DATE +
                        " DESC ";
            case "original_title.asc":
                return MoviesContract.MovieEntry.TABLE_NAME + "."
                        + MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE;
            default:
                return "";   // No sorting
        }

    }
}
