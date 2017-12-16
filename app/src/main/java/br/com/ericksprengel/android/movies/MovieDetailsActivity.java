package br.com.ericksprengel.android.movies;

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
import br.com.ericksprengel.android.movies.db.MoviesDataSource;
import br.com.ericksprengel.android.movies.db.MoviesRepository;
import br.com.ericksprengel.android.movies.db.local.MovieDbUtils;
import br.com.ericksprengel.android.movies.models.Movie;
import br.com.ericksprengel.android.movies.models.MovieReview;
import br.com.ericksprengel.android.movies.models.MovieReviewListResponse;
import br.com.ericksprengel.android.movies.models.MovieVideo;
import br.com.ericksprengel.android.movies.models.MovieVideoListResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends BaseActivity implements View.OnClickListener,
        MovieDetailsAdapter.OnMovieVideoClickListener, MovieDetailsAdapter.OnMovieFavoriteClickListener, MoviesDataSource.LoadVideosCallback, MoviesDataSource.LoadReviewsCallback {

    final private static String LOG_TAG = "MovieListActivity";

    final private static String PARAM_MOVIE = "movie";

    private MoviesRepository mMoviesRepository;

    private MovieDetailsAdapter mAdapter;

    private Movie mMovie;


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

        mMoviesRepository = MoviesRepository.getInstance(this.getApplicationContext());

        super.setOnErrorClickListener(this);

        mMovie = Parcels.unwrap(getIntent().getParcelableExtra(PARAM_MOVIE));

        mMovie.setFavorite(MovieDbUtils.isFavoriteMovie(this, mMovie));

        // Recycle view initialization
        RecyclerView mRecyclerView = findViewById(R.id.content_view);
        mRecyclerView.setHasFixedSize(false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MovieDetailsAdapter(mMovie, null,null, this, this);
        mRecyclerView.setAdapter(mAdapter);

        loadVideos();
    }


    private void loadVideos() {
        boolean isCached = mMoviesRepository.getVideos(this, mMovie);
        if(!isCached) {
            showLoading(getResources().getString(R.string.movie_details_ac_loading_movie_content));
        }
        mMoviesRepository.getVideos(this, mMovie);
    }

    private void loadReviews() {
        boolean isCached = mMoviesRepository.getReviews(this, mMovie);
        if(!isCached) {
            showLoading(getResources().getString(R.string.movie_details_ac_loading_movie_content));
        }
        mMoviesRepository.getReviews(this, mMovie);
    }

    @Override
    public void onVideosLoaded(List<MovieVideo> videos) {
        mAdapter.setMovieVideos(videos);
        loadReviews();
    }

    @Override
    public void onReviewsLoaded(List<MovieReview> reviews) {
        mAdapter.setMovieReviews(reviews);
        showContent();
    }

    @Override
    public void onDataNotAvailable(int errorCode, String errorMessage) {
        Log.w(LOG_TAG, errorMessage + " (" + errorCode + ")");
        showError(errorMessage);
    }



    // click events

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
            mMoviesRepository.favoriteMovie(movie);
        } else {
            mMoviesRepository.unfavoriteMovie(movie);
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
}
