package com.flynorc.popularmovies_stage2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.flynorc.popularmovies_stage2.database.MoviesContract.MovieEntry;

/**
 * Created by Flynorc on 17-Mar-18.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createMoviesTableSql = "CREATE TABLE " + MovieEntry.TABLE_NAME + " ("
                + MovieEntry.COLUMN_API_ID + " INTEGER NOT NULL, "
                + MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + MovieEntry.COLUMN_RELEASE_DATE + " TEXT, "
                + MovieEntry.COLUMN_POSTER_PATH + " TEXT, "
                + MovieEntry.COLUMN_VOTE_AVERAGE + " REAL, "
                + MovieEntry.COLUMN_PLOT + " TEXT);";

        db.execSQL(createMoviesTableSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //we are still at version 1 so nothing but spider webs here now...
    }
}
