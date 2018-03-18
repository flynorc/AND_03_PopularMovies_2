package com.flynorc.popularmovies_stage2;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flynorc.popularmovies_stage2.database.DbUtils;
import com.flynorc.popularmovies_stage2.database.MoviesContract;
import com.flynorc.popularmovies_stage2.database.MoviesContract.MovieEntry;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {

    private static final int MOVIE_REVIEWS_LOADER_ID = 41; //all nice prime numbers
    private static final int MOVIE_VIDEOS_LOADER_ID = 43;
    private static final int MOVIE_DB_LOADER_ID = 97;

    private Movie movie;
    private LoaderManager loaderManager;

    @BindView(R.id.movie_title_tv)
    TextView titleTv;

    @BindView(R.id.release_date_tv)
    TextView releaseDateTv;

    @BindView(R.id.vote_average_tv)
    TextView voteAverageTv;

    @BindView(R.id.plot_tv)
    TextView plotTv;

    @BindView(R.id.movie_poster)
    ImageView posterIv;

    @BindView(R.id.reviews_ll)
    LinearLayout reviewsLl;

    @BindView(R.id.videos_ll)
    LinearLayout videosLl;

    @BindView(R.id.favorite_fab)
    FloatingActionButton favFab;

    View.OnClickListener playVideoClickListener;


    /*
     * LOADER FOR VIDEOS
     * implementation of loadercallbacks for videos
     */
    private LoaderManager.LoaderCallbacks<List<MovieVideo>> movieVideosLoaderListener
            = new LoaderManager.LoaderCallbacks<List<MovieVideo>>() {

        @Override
        public Loader<List<MovieVideo>> onCreateLoader(int i, Bundle bundle) {
            //movie object already has videos stored, no need to call loader again
            if(movie.getVideos() != null) {
                return null;
            }

            //create the loader
            return new MovieVideosLoader(DetailsActivity.this, movie.getId());
        }

        @Override
        public void onLoadFinished(Loader<List<MovieVideo>> loader, List<MovieVideo> videos) {
            //show the data in the correct view
            for (int i=0; i< videos.size(); i++) {
                Log.d("videos finished", videos.get(i).toString());
                addVideoToLayout(videos.get(i));
            }
        }

        private void addVideoToLayout(MovieVideo video) {
            LayoutInflater inflater = LayoutInflater.from(DetailsActivity.this);
            View movieVideo = inflater.inflate(R.layout.movie_video, null, false);

            TextView videoName = (TextView) movieVideo.findViewById(R.id.video_name_tv);
            TextView videoType = (TextView) movieVideo.findViewById(R.id.video_type_tv);
            TextView videoUrl = (TextView) movieVideo.findViewById(R.id.video_url);

            videoName.setText(video.getName());
            videoType.setText(video.getType());
            videoUrl.setText(video.getYoutubeKey());

            movieVideo.setOnClickListener(playVideoClickListener);
            videosLl.addView(movieVideo);
        }

        @Override
        public void onLoaderReset(Loader<List<MovieVideo>> loader) {
            //remove all child elements of the container view
            videosLl.removeAllViews();
        }
    };



    /*
     * LOADER FOR REVIEWS
     * implementation of loadercallbacks for reviews
     */
    private LoaderManager.LoaderCallbacks<List<MovieReview>> movieReviewsLoaderListener
            = new LoaderManager.LoaderCallbacks<List<MovieReview>>() {

        @Override
        public Loader<List<MovieReview>> onCreateLoader(int i, Bundle bundle) {
            //movie object already has reviews stored, no need to call loader again
            if(movie.getReviews() != null) {
                return null;
            }

            //create the loader
            return new MovieReviewsLoader(DetailsActivity.this, movie.getId());
        }

        @Override
        public void onLoadFinished(Loader<List<MovieReview>> loader, List<MovieReview> reviews) {
            //show the data in the correct view
            for (int i=0; i< reviews.size(); i++) {
                addReviewToLayout(reviews.get(i));
            }
        }

        private void addReviewToLayout(MovieReview review) {
            LayoutInflater inflater = LayoutInflater.from(DetailsActivity.this);
            View movieReview = inflater.inflate(R.layout.movie_review, null, false);

            TextView reviewContent = (TextView) movieReview.findViewById(R.id.review_content_tv);
            TextView reviewAuthor = (TextView) movieReview.findViewById(R.id.review_author_tv);

            reviewContent.setText(review.getReviewText());
            reviewAuthor.setText(review.getAuthor());

            reviewsLl.addView(movieReview);
        }

        @Override
        public void onLoaderReset(Loader<List<MovieReview>> loader) {
            //remove all child elements of the container view
            reviewsLl.removeAllViews();
        }
    };


    /*
     * LOADER FOR GETTING DATA FROM DB
     * implementation of loadercallbacks for fetching movie from db
     */
    private LoaderManager.LoaderCallbacks<Cursor> movieDbLoaderListener
            = new LoaderManager.LoaderCallbacks<Cursor>() {
        /*
         * loader callbacks
         */
        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            return DbUtils.getMovieCursorLoader(DetailsActivity.this, movie.getId());
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            if (data == null || data.getCount() < 1) {
                Log.d("movie done", "No movie found in DB, therefore movie is not rated");
                favFab.setImageResource(R.drawable.ic_star_border_black_18dp);
                favFab.setVisibility(View.VISIBLE);
                movie.setFavorite(false);
                return;
            }

            //check if movie is null
            favFab.setImageResource(R.drawable.ic_star_black_18dp);
            favFab.setVisibility(View.VISIBLE);
            movie.setFavorite(true);
        }


        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

            Log.d("movie reset", "TODO implement movie db reset");
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ButterKnife.bind(this);

        favFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(movie.isFavorite()) {
                Log.d("ON CLICK", "movie will be deleted from favorites");
                deleteFromFavorites(movie.getId());
            } else {
                insertToFavorites(movie.getContentValues());
            }
            }
        });

        playVideoClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView urlTv = v.findViewById(R.id.video_url);
                String videoUrl = urlTv.getText().toString();

                Log.d("VIDEO CLICK", "video url: " + videoUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + videoUrl));
                startActivity(intent);
            }
        };

        // Get a reference to the LoaderManager, in order to interact with loaders.
        loaderManager = getLoaderManager();

        /*
         * get the movie object from saved instance state if it exists
         * otherwise get it from the extra data passed along with the intent
         * if it can not be found, show a toast (error) and close the activity
         */
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.error_movie_details_not_found, Toast.LENGTH_LONG);
                toast.show();
                finish();
                movie = null;
            } else {
                movie = (Movie) extras.getSerializable("movie");
            }
        } else {
            movie = (Movie) savedInstanceState.getSerializable("movie");
        }

        //check if movie is in the DB
        checkFavorites();

        displayMovieInfo();
    }

    private void checkFavorites() {
        Log.d("FAV", "checking favorites");
        loaderManager.initLoader(MOVIE_DB_LOADER_ID, null, movieDbLoaderListener);

    }

    private void deleteFromFavorites(int id) {
        Uri currentMovieUri = ContentUris.withAppendedId(MoviesContract.MovieEntry.CONTENT_URI, id);

        int rowsDeleted = getContentResolver().delete(currentMovieUri, null, null);

        // Show a toast message depending on whether or not the delete was successful.
        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, "delete failed",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "delete worked", Toast.LENGTH_SHORT).show();
            //delete the poster file- todo move this to helper function
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("posters", Context.MODE_PRIVATE);
            File myImageFile = new File(directory, movie.getPosterFilename());
            if (myImageFile.delete()) {
                Toast.makeText(this, "file deleted from device",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void insertToFavorites(ContentValues values) {
        Uri newUri = getContentResolver().insert(MovieEntry.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful.
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, "insert failed", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, "insert worked", Toast.LENGTH_SHORT).show();
            //add the image
            Picasso.with(this).load(movie.getPosterPath()).into(QueryUtils.picassoImageTarget(getApplicationContext(), "posters", movie.getPosterFilename()));
        }
    }

    private void displayMovieInfo() {
        titleTv.setText(movie.getTitle());
        releaseDateTv.setText(movie.getReleaseDate());
        voteAverageTv.setText(NumberFormat.getNumberInstance(Locale.getDefault()).format(movie.getVoteAverage()));
        plotTv.setText(movie.getPlot());
        Picasso.with(this)
                .load(movie.getPosterPath())
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.no_image185)
                .into(posterIv);

        if(QueryUtils.checkConnectivity(this)) {
            loaderManager.initLoader(MOVIE_VIDEOS_LOADER_ID, null, movieVideosLoaderListener);
            loaderManager.initLoader(MOVIE_REVIEWS_LOADER_ID, null, movieReviewsLoaderListener);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.no_internet_toast, Toast.LENGTH_LONG);
            toast.show();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("movie", movie);
        super.onSaveInstanceState(outState);
    }

}
