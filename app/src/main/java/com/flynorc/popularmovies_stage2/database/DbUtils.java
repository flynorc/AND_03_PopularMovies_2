package com.flynorc.popularmovies_stage2.database;

import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;

import com.flynorc.popularmovies_stage2.Movie;
import com.flynorc.popularmovies_stage2.database.MoviesContract.MovieEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Flynorc on 17-Mar-18.
 */

public class DbUtils {
    public static CursorLoader getMovieCursorLoader(Context context, int id) {

        Uri currentMovieUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, id);
        //create the loader
        return new CursorLoader(context,   // Parent activity context
                currentMovieUri,         // Query the content URI for the current movie
                getMovieProjection(),             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    public static List<Movie> getMoviesFromDb(Context context) {
        Cursor cursor = context.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                getMovieProjection(),
                null,
                null,
                null
        );

        List<Movie> movies = parseMoviesFromCursorToList(cursor);

        cursor.close();

        return  movies;
    }

    private static String[] getMovieProjection() {
        String[] projection = {
                MovieEntry.COLUMN_API_ID,
                MovieEntry.COLUMN_TITLE,
                MovieEntry.COLUMN_RELEASE_DATE,
                MovieEntry.COLUMN_POSTER_PATH,
                MovieEntry.COLUMN_VOTE_AVERAGE,
                MovieEntry.COLUMN_PLOT
        };

        return projection;
    }

    private static List<Movie> parseMoviesFromCursorToList(Cursor cursor) {
        ArrayList<Movie> movies = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            //get columns
            int idColumn = cursor.getColumnIndex(MovieEntry.COLUMN_API_ID);
            int titleColumn = cursor.getColumnIndex(MovieEntry.COLUMN_TITLE);
            int releaseDateColumn = cursor.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE);
            int posterPathColumn = cursor.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH);
            int voteAverageColumn = cursor.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE);
            int plotColumn = cursor.getColumnIndex(MovieEntry.COLUMN_PLOT);

            //add row to list
            do {
                int id = cursor.getInt(idColumn);
                String title = cursor.getString(titleColumn);
                String releaseDate = cursor.getString(releaseDateColumn);
                String posterPath = cursor.getString(posterPathColumn);
                Float voteAverage = cursor.getFloat(voteAverageColumn);
                String plot = cursor.getString(plotColumn);

                movies.add(new Movie(id, title, releaseDate, posterPath, voteAverage, plot, true));
            }
            while (cursor.moveToNext());
            cursor.close();
        }

        return movies;
    }
}
