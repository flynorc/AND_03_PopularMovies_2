package com.flynorc.popularmovies_stage2;

/**
 * Created by Flynorc on 05-Mar-18.
 */

class MovieReview {

    //Todo implement parcelable

    private String author;
    private String reviewText;

    public MovieReview(String author, String reviewText) {
        this.author = author;
        this.reviewText = reviewText;
    }

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
}
