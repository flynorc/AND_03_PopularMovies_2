package com.flynorc.popularmovies_stage2;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.flynorc.popularmovies_stage2.database.DbUtils;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Flynorc on 24-Feb-18.
 */

public class MoviesLoader extends AsyncTaskLoader<List<Movie>> {

    private String url;
    private boolean fetchFromDb;
    private WeakReference<Context> weakRefContext;
    private String sortUrlParam;

    public MoviesLoader(Context context) {
        super(context);

        this.weakRefContext = new WeakReference<>(context);

        //get the url part saved in shared preferences (sorted by popular vs top rated)
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sortUrlParam = sharedPreferences.getString(context.getString(R.string.pref_sort_key), context.getString(R.string.pref_sort_popular_value));

        //if we want favorite movies OR there is no internet connection
        if(sortUrlParam.equals(context.getString(R.string.pref_sort_favorite_value))
                || !QueryUtils.checkConnectivity(context)) {
            fetchFromDb = true;
        } else {
            fetchFromDb = false;
        }
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        if(fetchFromDb) {
            return DbUtils.getMoviesFromDb(weakRefContext.get());
        } else {
            //build up the URL from MOVIE_API_BASE_URL, the parameters from the shared preferences and api key
            Uri baseUri = Uri.parse(QueryUtils.MOVIE_API_BASE_URL);
            Uri.Builder uriBuilder = baseUri.buildUpon();
            uriBuilder.appendPath(sortUrlParam);
            uriBuilder.appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_KEY);

            url = uriBuilder.toString();

            if(url.isEmpty()) {
                return null;
            }

            return QueryUtils.getMoviesFromApi(url, weakRefContext.get());
        }
    }
}
