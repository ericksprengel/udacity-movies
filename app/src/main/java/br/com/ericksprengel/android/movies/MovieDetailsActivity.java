package br.com.ericksprengel.android.movies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.parceler.Parcels;

import java.util.List;

import br.com.ericksprengel.android.movies.api.TheMovieDbApiError;
import br.com.ericksprengel.android.movies.api.TheMovieDbServicesBuilder;
import br.com.ericksprengel.android.movies.db.MovieContract;
import br.com.ericksprengel.android.movies.models.Movie;
import br.com.ericksprengel.android.movies.models.MovieListResponse;
import br.com.ericksprengel.android.movies.models.MovieReview;
import br.com.ericksprengel.android.movies.models.MovieReviewListResponse;
import br.com.ericksprengel.android.movies.models.MovieVideo;
import br.com.ericksprengel.android.movies.models.MovieVideoListResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.ericksprengel.android.movies.api.TheMovieDbServices.MOVIE_LIST_TYPE_POPULAR;
import static br.com.ericksprengel.android.movies.api.TheMovieDbServices.MOVIE_LIST_TYPE_TOP_RATED;

public class MovieDetailsActivity extends BaseActivity implements View.OnClickListener,
        MovieDetailsAdapter.OnMovieVideoClickListener, MovieDetailsAdapter.OnMovieFavoriteClickListener {

    final private static String LOG_TAG = "MovieListActivity";

    final private static String PARAM_MOVIE = "movie";

    final private static String PARAM_MOVIE_VIDEOS = "movie_videos";
    final private static String PARAM_MOVIE_REVIEWS = "movie_reviews";

    private MovieDetailsAdapter mAdapter;
    private Call<MovieVideoListResponse> mMovieVideoListCall;
    private Call<MovieReviewListResponse> mMovieReviewListCall;

    private Movie mMovie;
    private List<MovieVideo> mMovieVideos;
    private List<MovieReview> mMovieReviews;

    Callback<MovieVideoListResponse> mMovieVideoListCallback = new Callback<MovieVideoListResponse>() {
        @Override
        public void onResponse(Call<MovieVideoListResponse> call, Response<MovieVideoListResponse> response) {
            mMovieVideoListCall = null;
            if(response.isSuccessful()) {
                MovieVideoListResponse movieVideoListResponse = response.body();
                if(movieVideoListResponse == null) {
                    showError(getString(R.string.movie_details_ac_api_request_error));
                }
                List<MovieVideo> videos = movieVideoListResponse.getResults();

                // caching the movie video list
                mMovieVideos = videos;
                mAdapter.setMovieVideos(videos);

                loadMovieReviews();
            } else {
                TheMovieDbApiError error = TheMovieDbServicesBuilder.parseError(response, getApplicationContext());
                showError(error.getStatusMessage() != null ? error.getStatusMessage() : getString(R.string.movie_details_ac_api_request_error));
            }
        }

        @Override
        public void onFailure(Call<MovieVideoListResponse> call, Throwable t) {
            mMovieVideoListCall = null;
            Log.e(LOG_TAG, "Connection error.", t);
            showError(getString(R.string.connection_error));
        }
    };

    Callback<MovieReviewListResponse> mMovieReviewListCallback = new Callback<MovieReviewListResponse>() {
        @Override
        public void onResponse(Call<MovieReviewListResponse> call, Response<MovieReviewListResponse> response) {
            mMovieReviewListCall = null;
            if(response.isSuccessful()) {
                MovieReviewListResponse movieReviewListResponse = response.body();
                if(movieReviewListResponse == null) {
                    showError(getString(R.string.movie_details_ac_api_request_error));
                }
                List<MovieReview> reviews = movieReviewListResponse.getResults();

                // caching the movie review list
                mMovieReviews = reviews;
                mAdapter.setMovieReviews(reviews);
                showContent();
            } else {
                TheMovieDbApiError error = TheMovieDbServicesBuilder.parseError(response, getApplicationContext());
                showError(error.getStatusMessage() != null ? error.getStatusMessage() : getString(R.string.movie_details_ac_api_request_error));
            }
        }

        @Override
        public void onFailure(Call<MovieReviewListResponse> call, Throwable t) {
            mMovieReviewListCall = null;
            Log.e(LOG_TAG, "Connection error.", t);
            showError(getString(R.string.connection_error));
        }
    };


    public static Intent getStartIntent(Context context, Movie movie) {
        Intent intent = new Intent(context, MovieDetailsActivity.class);
        intent.putExtra(PARAM_MOVIE, Parcels.wrap(movie));
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        initBaseActivity();

        super.setOnErrorClickListener(this);

        mMovie = Parcels.unwrap(getIntent().getParcelableExtra(PARAM_MOVIE));

        // Recycle view initialization
        RecyclerView mRecyclerView = findViewById(R.id.content_view);
        mRecyclerView.setHasFixedSize(false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MovieDetailsAdapter(mMovie, null,null, this, this);
        mRecyclerView.setAdapter(mAdapter);

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(PARAM_MOVIE_VIDEOS)) {
                mMovieVideos = Parcels.unwrap(savedInstanceState.getParcelable(PARAM_MOVIE_VIDEOS));
            }
        }

        loadMovieVideos();
    }

    private void loadMovieVideos() {
        // getting cached movie video list
        if(mMovieVideos != null) {
            mAdapter.setMovieVideos(mMovieVideos);
            loadMovieReviews();
        } else {
            mMovieVideoListCall = TheMovieDbServicesBuilder.build(this).getMovieVideoList(mMovie.getId());
            mMovieVideoListCall.enqueue(mMovieVideoListCallback);
            showLoading(getString(R.string.movie_details_ac_loading_movie_content));
        }
    }

    private void loadMovieReviews() {
        // getting cached movie review list
        if(mMovieReviews != null) {
            mAdapter.setMovieReviews(mMovieReviews);
            showContent();
        } else {
            mMovieReviewListCall = TheMovieDbServicesBuilder.build(this).getMovieReviewList(mMovie.getId());
            mMovieReviewListCall.enqueue(mMovieReviewListCallback);
            showLoading(getString(R.string.movie_details_ac_loading_movie_content));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_error_button:
                Toast.makeText(this, "Error button.\nNot implemented.", Toast.LENGTH_LONG).show();
                break;
            default:
                Log.wtf(LOG_TAG, "Click event without treatment. (view id: " +view.getId()+ ")");
        }
    }

    @Override
    public void onMovieVideoClick(MovieVideo video) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(video.getVideoURL()));
        startActivity(intent);
    }

    @Override
    public void onMovieFavoriteClick(Movie movie, boolean favorite) {
        movie.setFavorite(favorite);
        if(favorite) {
            getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,
                    MovieContract.MovieEntry.getContentValues(movie));
        } else {
            Uri uri = MovieContract.MovieEntry.CONTENT_URI.buildUpon()
                    .appendPath(String.valueOf(movie.getId()))
                    .build();
            getContentResolver().delete(uri,null, null);
            //TODO getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, MainActivity.this);
        }
    }
}
