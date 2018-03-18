package com.flynorc.popularmovies_stage2;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Flynorc on 05-Mar-18.
 */

class MovieReview implements Parcelable{

    private String author;
    private String reviewText;

    public MovieReview(String author, String reviewText) {
        this.author = author;
        this.reviewText = reviewText;
    }

    protected MovieReview(Parcel in) {
        author = in.readString();
        reviewText = in.readString();
    }

    public static final Creator<MovieReview> CREATOR = new Creator<MovieReview>() {
        @Override
        public MovieReview createFromParcel(Parcel in) {
            return new MovieReview(in);
        }

        @Override
        public MovieReview[] newArray(int size) {
            return new MovieReview[size];
        }
    };

    public String getAuthor() {
        return author;
    }

    public String getReviewText() {
        return reviewText;
    }

    @Override
    public String toString() {
        return "MovieReview{" +
                "author='" + author + '\'' +
                ", reviewText='" + reviewText + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(author);
        parcel.writeString(reviewText);
    }
}
