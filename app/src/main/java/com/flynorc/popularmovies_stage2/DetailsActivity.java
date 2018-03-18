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
                displayVideos(movie.getVideos());
                return null;
            }

            //create the loader
            return new MovieVideosLoader(DetailsActivity.this, movie.getId());
        }

        @Override
        public void onLoadFinished(Loader<List<MovieVideo>> loader, List<MovieVideo> videos) {
            // add the reviews to the movie object
            movie.setVideos(videos);

            displayVideos(videos);
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
                displayReviews(movie.getReviews());
                return null;
            }

            //create the loader
            return new MovieReviewsLoader(DetailsActivity.this, movie.getId());
        }

        @Override
        public void onLoadFinished(Loader<List<MovieReview>> loader, List<MovieReview> reviews) {
            // add the reviews to the movie object
            movie.setReviews(reviews);

            displayReviews(reviews);
        }


        @Override
        public void onLoaderReset(Loader<List<MovieReview>> loader) {
            //remove all child elements of the container view
            reviewsLl.removeAllViews();
        }
    };


    /*
     * LOADER FOR GETTING DATA FROM DB
     * only used to check if current movie is present in favorites, to correctly display the star (on fab)
     * implementation of loadercallbacks for fetching movie from db
     */
    private LoaderManager.LoaderCallbacks<Cursor> movieDbLoaderListener
            = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            return DbUtils.getMovieCursorLoader(DetailsActivity.this, movie.getId());
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data == null || data.getCount() < 1) {
                //no movie with current id present, meaning it is not favorited
                favFab.setImageResource(R.drawable.ic_star_border_black_18dp);
                favFab.setVisibility(View.VISIBLE);
                movie.setFavorite(false);
                return;
            }

            //movie is present in database - it is a favorite movie
            favFab.setImageResource(R.drawable.ic_star_black_18dp);
            favFab.setVisibility(View.VISIBLE);
            movie.setFavorite(true);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            // Note for the reviewer:
            // is there some cleanup that i'm forgetting about here?
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        attachFabOnClick();
        defineVideoPlayOnclick();

        loaderManager = getLoaderManager();

        initializeMovieObject(savedInstanceState);

        checkFavorites();
        displayMovieInfo();
    }

    private void attachFabOnClick() {
        favFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(movie.isFavorite()) {
                deleteFromFavorites(movie.getId());
            } else {
                insertToFavorites(movie.getContentValues());
            }
            }
        });
    }

    private void defineVideoPlayOnclick() {
        playVideoClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView urlTv = v.findViewById(R.id.video_url);
                String videoKey = urlTv.getText().toString();

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + videoKey));
                startActivity(intent);
            }
        };
    }

    /*
     * get the movie object from saved instance state if it exists
     * otherwise get it from the extra data passed along with the intent
     * if it can not be found, show a toast (error) and close the activity
     */
    private void initializeMovieObject(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            //try to get movie information from intent
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.error_movie_details_not_found, Toast.LENGTH_LONG);
                toast.show();
                finish();
            } else {
                movie = extras.getParcelable("movie");
            }
        } else {
            movie = savedInstanceState.getParcelable("movie");
        }
    }

    private void checkFavorites() {
        loaderManager.initLoader(MOVIE_DB_LOADER_ID, null, movieDbLoaderListener);

    }

    private void deleteFromFavorites(int id) {
        Uri currentMovieUri = ContentUris.withAppendedId(MoviesContract.MovieEntry.CONTENT_URI, id);

        int rowsDeleted = getContentResolver().delete(currentMovieUri, null, null);

        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, R.string.delete_error,
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.remove_favorite_success_msg, Toast.LENGTH_SHORT).show();

            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir(getString(R.string.posters_folder_path), Context.MODE_PRIVATE);
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
            Toast.makeText(this, R.string.insert_failed_msg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.movie_added_msg, Toast.LENGTH_SHORT).show();
            //store the poster image to the device storage
            Picasso.with(this).load(movie.getPosterPath()).into(QueryUtils.picassoImageTarget(getApplicationContext(), getString(R.string.posters_folder_path), movie.getPosterFilename()));
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
            // if we have already loaded the videos or reviews, they will be persisted on configuration change
            // and they can be displayed even without internet connection
            if(movie.getReviews() != null) {
                displayReviews(movie.getReviews());
            }

            if(movie.getVideos() != null) {
                displayVideos(movie.getVideos());
            }

             Toast toast = Toast.makeText(getApplicationContext(), R.string.no_internet_toast, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void displayReviews(List<MovieReview> reviews) {

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

    private void displayVideos(List <MovieVideo> videos ) {
        //show the data in the correct view
        for (int i=0; i< videos.size(); i++) {
            addVideoToLayout(videos.get(i));
        }
    }

    private void addVideoToLayout(MovieVideo video) {
        LayoutInflater inflater = LayoutInflater.from(DetailsActivity.this);
        View movieVideo = inflater.inflate(R.layout.movie_video, null, false);

        // Note for reviewer:
        // how can I use butterknife library in this case?
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("movie", movie);
        super.onSaveInstanceState(outState);
    }

}
