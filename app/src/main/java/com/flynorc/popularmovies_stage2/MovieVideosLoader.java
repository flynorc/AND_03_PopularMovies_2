package com.flynorc.popularmovies_stage2;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Flynorc on 18-Mar-18.
 */

public class MovieVideosLoader extends AsyncTaskLoader<List<MovieVideo>> {

    private int movieId;
    private WeakReference<Context> weakRefContext;

    public MovieVideosLoader(Context context, int id) {
        super(context);
        this.movieId = id;
        this.weakRefContext = new WeakReference<>(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<MovieVideo> loadInBackground() {
        return QueryUtils.getVideosForMovie(movieId, weakRefContext.get());
    }

}
