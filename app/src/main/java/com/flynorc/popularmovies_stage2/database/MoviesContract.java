package com.flynorc.popularmovies_stage2.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Flynorc on 17-Mar-18.
 */

public final class MoviesContract {
    // private constructor to prevent instantiation of the contract class
    private MoviesContract() {}

    /*
     * constants to provide consistent use of paths and content authority throughout the app
     */
    public static final String CONTENT_AUTHORITY = "com.flynorc.popularmovies_stage2";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";



    /**
     * Inner class that defines constant values for the movies database table
     * Each entry in the table represents a single movie
     */
    public static final class MovieEntry implements BaseColumns {
        //content://com.flynorc.popularmovies_stage2/movies
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MOVIES);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        //database table name
        public final static String TABLE_NAME = "movies";

        /*
         * columns in the table
         */

        /**
         * Movies API id
         *
         * Type: INTEGER
         */
        public final static String COLUMN_API_ID ="api_id";

        /**
         * Title of the movie.
         *
         * Type: TEXT
         */
        public final static String COLUMN_TITLE = "title";

        /**
         * Release date stored as string
         *
         * Type: TEXT
         */
        public final static String COLUMN_RELEASE_DATE = "release_date";

        /**
         * Poster path
         *
         * Type: TEXT
         */
        public final static String COLUMN_POSTER_PATH = "poster_path";

        /**
         * Vote average
         *
         * Type: REAL
         */
        public final static String COLUMN_VOTE_AVERAGE = "vote_average";

        /**
         * Plot
         *
         * Type: TEXT
         */
        public final static String COLUMN_PLOT = "plot";

    }
}
