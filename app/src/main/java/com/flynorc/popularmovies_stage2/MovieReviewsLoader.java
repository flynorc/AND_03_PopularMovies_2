package com.flynorc.popularmovies_stage2;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Flynorc on 05-Mar-18.
 */

public class MovieReviewsLoader extends AsyncTaskLoader<List<MovieReview>> {

    private int movieId;
    private WeakReference<Context> weakRefContext;

    public MovieReviewsLoader(Context context, int id) {
        super(context);
        this.movieId = id;
        this.weakRefContext = new WeakReference<>(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<MovieReview> loadInBackground() {
        return QueryUtils.getReviewsForMovie(movieId, weakRefContext.get());
    }
}
