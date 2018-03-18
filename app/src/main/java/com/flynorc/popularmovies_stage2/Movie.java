package com.flynorc.popularmovies_stage2;

import android.content.ContentValues;

import com.flynorc.popularmovies_stage2.database.MoviesContract.MovieEntry;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Flynorc on 24-Feb-18.
 */

public class Movie implements Serializable {

    private static final String BASE_POSTER_THUMB_PATH = "https://image.tmdb.org/t/p/w185";
    private static final String BASE_POSTER_PATH = "https://image.tmdb.org/t/p/w500";

    private int id;
    private String title;
    private String releaseDate;
    private String posterPath;
    private Float voteAverage;
    private String plot;
    private boolean favorite;

    private List<MovieVideo> videos;
    private List<MovieReview>  reviews;

    public List<MovieVideo> getVideos() {
        return videos;
    }

    public List<MovieReview> getReviews() {
        return reviews;
    }

    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public Float getVoteAverage() {
        return voteAverage;
    }

    public String getPlot() {
        return plot;
    }

    public Movie(int id, String title, String releaseDate, String posterPath, Float voteAverage, String plot) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
        this.plot = plot;
    }

    public Movie(int id, String title, String releaseDate, String posterPath, Float voteAverage, String plot, boolean favorite) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
        this.plot = plot;
        this.favorite = favorite;
    }



    /*
     * helper for debugging
     */
    @Override
    public String toString() {
        return "id: " + id
            + "title: " + title
            + ",\nrelease date: " + releaseDate
            + ",\nposterPath: " + posterPath
            + ",\nvote average: " + voteAverage.toString()
            + ",\nplot: " + plot;
    }

    public String getPosterThumbPath() {
        return BASE_POSTER_THUMB_PATH + posterPath;
    }

    public String getPosterPath() {
        return BASE_POSTER_PATH + posterPath;
    }

    public String getPosterFilename() {
        return posterPath;
    }

    public ContentValues getContentValues() {

        ContentValues values = new ContentValues();
        values.put(MovieEntry.COLUMN_API_ID, id);
        values.put(MovieEntry.COLUMN_TITLE, title);
        values.put(MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
        values.put(MovieEntry.COLUMN_POSTER_PATH, posterPath);
        values.put(MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);
        values.put(MovieEntry.COLUMN_PLOT, plot);

        return values;
    }

    public boolean isFavorite() {
        return this.favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
