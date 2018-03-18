package com.flynorc.popularmovies_stage2;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Flynorc on 25-Feb-18.
 */

public class QueryUtils {

    public static final String MOVIE_API_BASE_URL = "https://api.themoviedb.org/3/movie";

    private static final String KEY_RESULTS = "results";

    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_RELEASE_DATE = "release_date";
    private static final String KEY_POSTER = "poster_path";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String KEY_PLOT = "overview";

    private static final String KEY_AUTHOR = "author";
    private static final String KEY_REVIEW_TEXT = "content";

    private static final String KEY_VIDEO_NAME = "name";
    private static final String KEY_VIDEO_TYPE = "type";
    private static final String KEY_VIDEO_SITE = "site";
    private static final String KEY_VIDEO_KEY = "key";

    public static ArrayList<Movie> getMoviesFromApi(String urlString, final Context context) {
        URL url = createUrl(urlString, context);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url, context);
        } catch (IOException e) {
            Log.e("GET MOVIES", "Error closing input stream", e);

            //use a handler to create a toast from the background thread
            Handler handler =  new Handler(context.getMainLooper());
            handler.post( new Runnable(){
                public void run(){
                    Toast toast = Toast.makeText(context, R.string.error_networking_operation,Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }

        //extract the movies from the string that was returned from the API
        return extractMovies(jsonResponse, context);
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl, Context context) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e("create url", "Error with creating URL ", e);
            Toast toast = Toast.makeText(context, R.string.error_networking_operation,Toast.LENGTH_LONG);
            toast.show();
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     * as learned in the course
     */
    private static String makeHttpRequest(URL url, final Context context) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("make request", "Error response code: " + urlConnection.getResponseCode());

                //use a handler to create a toast from the background thread
                Handler handler =  new Handler(context.getMainLooper());
                handler.post( new Runnable(){
                    public void run(){
                        Toast toast = Toast.makeText(context, R.string.error_networking_operation,Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
            }
        } catch (IOException e) {
            Log.e("make request", "Problem retrieving the movies JSON results.", e);

            //use a handler to create a toast from the background thread
            Handler handler =  new Handler(context.getMainLooper());
            handler.post( new Runnable(){
                public void run(){
                    Toast toast = Toast.makeText(context, R.string.error_networking_operation,Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the InputStream into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static ArrayList<Movie> extractMovies(String response, final Context context) {
        // Create an empty ArrayList that we can start adding movies to
        ArrayList<Movie> movies = new ArrayList<>();

        // Try to parse the JSON response. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            JSONObject data =  new JSONObject(response);
            JSONArray results = data.getJSONArray(KEY_RESULTS);

            //parse every item and if parsing is successful it is added to the list
            for(int i=0; i < results.length(); i++) {
                JSONObject movieJson = results.getJSONObject(i);
                Movie movie = parseMovieJson(movieJson);
                if(movie != null ) {
                    movies.add(movie);
                }
            }
        } catch (JSONException e) {
            showErrorToast(e, context);
        }

        // Return the list of movies (that were successfully parsed)
        return movies;
    }

    private static void showErrorToast(JSONException e, final Context context) {
        // If an error is thrown when executing any of the above statements in the "try" block,
        // catch the exception here, so the app doesn't crash. Print a log message
        // with the message from the exception.
        Log.e("QueryUtils", "Problem parsing the JSON results", e);

        //use a handler to create a toast from the background thread
        Handler handler =  new Handler(context.getMainLooper());
        handler.post( new Runnable(){
            public void run(){
                Toast toast = Toast.makeText(context, R.string.error_networking_operation,Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    /*
     * helper function to parse the attributes of the movie from the JSON object
     * and return the Movie object
     */
    private static Movie parseMovieJson(JSONObject movieJson) {
        int id;
        //id is a required parameter, if no ID is present, we can't use this movie
        //since we need the ID if we want to "favorite it"
        try {
            id = movieJson.getInt(KEY_ID);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        String title = movieJson.optString(KEY_TITLE, "");
        String releaseDate = movieJson.optString(KEY_RELEASE_DATE, "");
        String posterPath = movieJson.optString(KEY_POSTER, "");
        Float voteAverage = Float.parseFloat(movieJson.optString(KEY_VOTE_AVERAGE, "0"));
        String plot = movieJson.optString(KEY_PLOT, "");

        return new Movie(id, title, releaseDate, posterPath, voteAverage, plot);
    }

    /*
     * helper method to check if user has internet access
     */
    public static boolean checkConnectivity(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    public static List<MovieReview> getReviewsForMovie(int movieId, final Context context) {
        String urlString = MOVIE_API_BASE_URL + "/" + movieId + "/reviews?api_key=" + BuildConfig.THE_MOVIE_DB_API_KEY;
        URL url = createUrl(urlString, context);

        Log.d("GET REVIEWS", "url: " + url.toString());

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url, context);
        } catch (IOException e) {
            Log.e("GET REVIEWS", "Error closing input stream", e);

            //use a handler to create a toast from the background thread
            Handler handler =  new Handler(context.getMainLooper());
            handler.post( new Runnable(){
                public void run(){
                    Toast toast = Toast.makeText(context, R.string.error_networking_operation,Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }

        //extract the review from the string that was returned from the API
        Log.d("GET REVIEWS", "response: " + jsonResponse);
        return extractReviews(jsonResponse, context);
    }

    private static List<MovieReview> extractReviews(String jsonResponse, Context context) {
        // Create an empty ArrayList that we can start adding reviews to
        ArrayList<MovieReview> reviews = new ArrayList<>();

        // Try to parse the JSON response. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            JSONObject data =  new JSONObject(jsonResponse);
            JSONArray results = data.getJSONArray(KEY_RESULTS);

            //parse every item and if parsing is successful it is added to the list
            for(int i=0; i < results.length(); i++) {
                JSONObject reviewJson = results.getJSONObject(i);
                MovieReview review = parseMovieReviewJson(reviewJson);
                if(review != null ) {
                    reviews.add(review);
                }
            }
        } catch (JSONException e) {
            showErrorToast(e, context);
        }

        // Return the list of reviews (that were successfully parsed)
        return reviews;
    }

    private static MovieReview parseMovieReviewJson(JSONObject reviewJson) {


        String author = reviewJson.optString(KEY_AUTHOR, "");
        String reviewText = reviewJson.optString(KEY_REVIEW_TEXT, "");

        return new MovieReview(author, reviewText);

    }

    //http://www.codexpedia.com/android/android-download-and-save-image-through-picasso/
    public static Target picassoImageTarget(Context context, final String imageDir, final String imageName) {
        Log.d("picassoImageTarget", " picassoImageTarget");
        ContextWrapper cw = new ContextWrapper(context);
        final File directory = cw.getDir(imageDir, Context.MODE_PRIVATE); // path to /data/data/yourapp/app_imageDir
        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final File myImageFile = new File(directory, imageName); // Create image file
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(myImageFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.i("image", "image saved to >>>" + myImageFile.getAbsolutePath());

                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                if (placeHolderDrawable != null) {}
            }
        };
    }


    public static List<MovieVideo> getVideosForMovie(int movieId, final Context context) {
        String urlString = MOVIE_API_BASE_URL + "/" + movieId + "/videos?api_key=" + BuildConfig.THE_MOVIE_DB_API_KEY;
        URL url = createUrl(urlString, context);

        Log.d("GET VIDEOS", "url: " + url.toString());

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url, context);
        } catch (IOException e) {
            Log.e("GET VIDEOS", "Error closing input stream", e);

            //use a handler to create a toast from the background thread
            Handler handler =  new Handler(context.getMainLooper());
            handler.post( new Runnable(){
                public void run(){
                    Toast toast = Toast.makeText(context, R.string.error_networking_operation,Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }

        //extract the review from the string that was returned from the API
        Log.d("GET VIDEOS", "response: " + jsonResponse);
        return extractVideos(jsonResponse, context);
    }

    private static List<MovieVideo> extractVideos(String jsonResponse, Context context) {
        // Create an empty ArrayList that we can start adding videos to
        ArrayList<MovieVideo> videos = new ArrayList<>();

        // Try to parse the JSON response. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            JSONObject data =  new JSONObject(jsonResponse);
            JSONArray results = data.getJSONArray(KEY_RESULTS);

            //parse every item and if parsing is successful it is added to the list
            for(int i=0; i < results.length(); i++) {
                JSONObject videoJson = results.getJSONObject(i);
                MovieVideo video = parseMovieVideoJson(videoJson);
                if(video != null ) {
                    videos.add(video);
                }
            }
        } catch (JSONException e) {
            showErrorToast(e, context);
        }

        // Return the list of videos (that were successfully parsed)
        return videos;
    }

    private static MovieVideo parseMovieVideoJson(JSONObject videoJson) {

        String site = videoJson.optString(KEY_VIDEO_SITE, "");

        //we are supporting youtube links at the moment
        if(!site.equals("YouTube")) {
            return null;
        }

        String name = videoJson.optString(KEY_VIDEO_NAME, "");
        String type = videoJson.optString(KEY_VIDEO_TYPE, "");
        String value = videoJson.optString(KEY_VIDEO_KEY, "");

        return new MovieVideo(value, type, name);

    }

}