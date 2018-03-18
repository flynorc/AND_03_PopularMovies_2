package com.flynorc.popularmovies_stage2;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.flynorc.popularmovies_stage2.database.MoviesContract.MovieEntry;

import java.util.List;

/**
 * Created by Flynorc on 24-Feb-18.
 */

public class Movie implements Parcelable {

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



    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public List<MovieVideo> getVideos() {
        return videos;
    }

    public void setVideos(List<MovieVideo> videos) {
        this.videos = videos;
    }

    public List<MovieReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<MovieReview> reviews) {
        this.reviews = reviews;
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
        String debugText = "id: " + id
            + "title: " + title
            + ",\nrelease date: " + releaseDate
            + ",\nposterPath: " + posterPath
            + ",\nvote average: " + voteAverage.toString()
            + ",\nplot: " + plot;

        if(reviews != null) {
            debugText += "\n\nREVIEWS: ";
            for(int i=0; i<reviews.size(); i++) {
                debugText += "\n" + reviews.get(i).getAuthor() + " - " + reviews.get(i).getReviewText().substring(0,30);
            }
        }

        if(videos != null) {
            debugText += "\n\nVIDEOS: ";
            for(int i=0; i<videos.size(); i++) {
                debugText += "\n" + videos.get(i).getName() + " - " + videos.get(i).getType();
            }
        }

        return debugText;
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

    /*
     * parcelable implementation
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(releaseDate);
        parcel.writeString(posterPath);
        if (voteAverage == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeFloat(voteAverage);
        }
        parcel.writeString(plot);
        parcel.writeByte((byte) (favorite ? 1 : 0));
        parcel.writeList(videos);
        parcel.writeList(reviews);
    }

    protected Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        releaseDate = in.readString();
        posterPath = in.readString();
        if (in.readByte() == 0) {
            voteAverage = null;
        } else {
            voteAverage = in.readFloat();
        }
        plot = in.readString();
        favorite = in.readByte() != 0;
        videos = in.readArrayList(MovieVideo.class.getClassLoader());
        reviews = in.readArrayList(MovieReview.class.getClassLoader());

    }
}
