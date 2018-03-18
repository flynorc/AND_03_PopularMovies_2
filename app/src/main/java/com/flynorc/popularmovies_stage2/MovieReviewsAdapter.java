package com.flynorc.popularmovies_stage2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Flynorc on 05-Mar-18.
 */

public class MovieReviewsAdapter extends ArrayAdapter<MovieReview> {

    private Context context;
    private List<MovieReview> reviews = new ArrayList<>();

    public MovieReviewsAdapter(Context context, List<MovieReview> reviews) {
        super(context, -1, reviews);

        this.context = context;
        this.reviews = reviews;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.movie_review, parent, false);

        TextView reviewContent = (TextView) rowView.findViewById(R.id.review_content_tv);
        TextView reviewAuthor = (TextView) rowView.findViewById(R.id.review_author_tv);

        MovieReview review = reviews.get(position);

        reviewContent.setText(review.getReviewText());
        reviewAuthor.setText(review.getAuthor());

        return rowView;
    }
}
