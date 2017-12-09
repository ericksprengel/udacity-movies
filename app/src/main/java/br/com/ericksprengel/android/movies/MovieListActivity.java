package br.com.ericksprengel.android.movies;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import org.parceler.Parcels;

import java.util.List;

import br.com.ericksprengel.android.movies.api.TheMovieDbApiError;
import br.com.ericksprengel.android.movies.api.TheMovieDbServicesBuilder;
import br.com.ericksprengel.android.movies.models.Movie;
import br.com.ericksprengel.android.movies.models.MovieListResponse;
import io.github.kobakei.materialfabspeeddial.FabSpeedDial;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.ericksprengel.android.movies.api.TheMovieDbServices.MOVIE_LIST_TYPE_POPULAR;
import static br.com.ericksprengel.android.movies.api.TheMovieDbServices.MOVIE_LIST_TYPE_TOP_RATED;

public class MovieListActivity extends BaseActivity implements View.OnClickListener, Callback<MovieListResponse>,MovieListAdapter.OnMovieClickListener {

    final private static String LOG_TAG = "MovieListActivity";

    final private static String PARAM_MOVIE_LIST_TYPE = "movie_list_type";
    final private static String PARAM_POPULAR_MOVIES = "popular_movies";
    final private static String PARAM_TOP_RATED_MOVIES = "top_rated_movies";

    private MovieListAdapter mAdapter;
    private Call<MovieListResponse> mMovieListCall;

    private String mMovieListType = MOVIE_LIST_TYPE_POPULAR;
    private List<Movie> mPopularMovies;
    private List<Movie> mTopRatedMovies;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        initBaseActivity();

        super.setOnErrorClickListener(this);

        RecyclerView mRecyclerView = findViewById(R.id.movie_list_ac_recycleview);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this,
                getResources().getInteger(R.integer.movie_list_ac_grid_spancount));
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MovieListAdapter(null, this);
        mRecyclerView.setAdapter(mAdapter);

        FabSpeedDial fab = findViewById(R.id.movie_list_ac_fab);
        fab.addOnMenuItemClickListener((miniFab, label, itemId) -> {
            switch (itemId) {
                case R.id.action_show_popular_movies:
                    loadMovies(MOVIE_LIST_TYPE_POPULAR);
                    return;
                case R.id.action_show_top_rated_movies:
                    loadMovies(MOVIE_LIST_TYPE_TOP_RATED);
                    return;
                default:
                    Log.wtf(LOG_TAG, "Click event without treatment. (item id: " + itemId + ")");
            }
        });

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(PARAM_MOVIE_LIST_TYPE)) {
                mMovieListType = savedInstanceState.getString(PARAM_MOVIE_LIST_TYPE);
            }
            if(savedInstanceState.containsKey(PARAM_POPULAR_MOVIES)) {
                mPopularMovies= Parcels.unwrap(savedInstanceState.getParcelable(PARAM_POPULAR_MOVIES));
            }
            if(savedInstanceState.containsKey(PARAM_TOP_RATED_MOVIES)) {
                mTopRatedMovies = Parcels.unwrap(savedInstanceState.getParcelable(PARAM_TOP_RATED_MOVIES));
            }
        }
        loadMovies(mMovieListType);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(PARAM_MOVIE_LIST_TYPE, mMovieListType);
        outState.putParcelable(PARAM_POPULAR_MOVIES, Parcels.wrap(mPopularMovies));
        outState.putParcelable(PARAM_TOP_RATED_MOVIES, Parcels.wrap(mTopRatedMovies));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mMovieListCall != null) {
            mMovieListCall.cancel();
        }
    }

    private void loadMovies(String listType) {
        mMovieListType = listType;

        // getting cached movie list
        if(mMovieListType.equals(MOVIE_LIST_TYPE_POPULAR) && mPopularMovies != null) {
            mAdapter.setMovies(mPopularMovies);
        } else if(mMovieListType.equals(MOVIE_LIST_TYPE_TOP_RATED) && mTopRatedMovies != null) {
            mAdapter.setMovies(mTopRatedMovies);
        } else {
            mMovieListCall = TheMovieDbServicesBuilder.build(this).getMovieList(listType);
            mMovieListCall.enqueue(this);
            showLoading(getString(R.string.movie_list_ac_loading_videos));
        }
    }

    @Override
    public void onResponse(Call<MovieListResponse> call, Response<MovieListResponse> response) {
        mMovieListCall = null;
        if(response.isSuccessful()) {
            MovieListResponse movieListResponse = response.body();
            if(movieListResponse == null) {
                showError(getString(R.string.movie_list_ac_api_request_error));
            }
            List<Movie> movies = movieListResponse.getResults();

            // caching the movie list
            if(mMovieListType.equals(MOVIE_LIST_TYPE_POPULAR)) {
                mPopularMovies = movies;
            } else if(mMovieListType.equals(MOVIE_LIST_TYPE_TOP_RATED)) {
                mTopRatedMovies = movies;
            }
            mAdapter.setMovies(movies);
            showContent();
        } else {
            TheMovieDbApiError error = TheMovieDbServicesBuilder.parseError(response, getApplicationContext());
            showError(error.getStatusMessage() != null ? error.getStatusMessage() : getString(R.string.movie_list_ac_api_request_error));
        }
    }

    @Override
    public void onFailure(Call<MovieListResponse> call, Throwable t) {
        mMovieListCall = null;
        Log.e(LOG_TAG, "Connection error.", t);
        showError(getString(R.string.connection_error));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_error_button:
                loadMovies(mMovieListType);
                break;
            default:
                Log.wtf(LOG_TAG, "Click event without treatment. (view id: " +view.getId()+ ")");
        }
    }

    @Override
    public void onMovieClick(Movie movie) {
        startActivity(MovieDetailsActivity.getStartIntent(this, movie));
    }
}
