package br.com.ericksprengel.android.movies;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.List;

import br.com.ericksprengel.android.movies.db.MoviesDataSource;
import br.com.ericksprengel.android.movies.db.MoviesRepository;
import br.com.ericksprengel.android.movies.models.Movie;
import io.github.kobakei.materialfabspeeddial.FabSpeedDial;

import static br.com.ericksprengel.android.movies.api.TheMovieDbServices.MOVIE_LIST_TYPE_FAVORITE;
import static br.com.ericksprengel.android.movies.api.TheMovieDbServices.MOVIE_LIST_TYPE_POPULAR;
import static br.com.ericksprengel.android.movies.api.TheMovieDbServices.MOVIE_LIST_TYPE_TOP_RATED;

public class MovieListActivity extends BaseActivity implements View.OnClickListener,MovieListAdapter.OnMovieClickListener, MoviesDataSource.LoadMoviesCallback {

    final private static String LOG_TAG = "MovieListActivity";

    final private static String PARAM_MOVIE_LIST_TYPE = "movie_list_type";

    private MovieListAdapter mAdapter;

    private String mMovieListType = MOVIE_LIST_TYPE_POPULAR;

    private MoviesRepository mMoviesRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        initBaseActivity();

        mMoviesRepository = MoviesRepository.getInstance(this.getApplicationContext());

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
                case R.id.action_show_favorite_movies:
                    loadMovies(MOVIE_LIST_TYPE_FAVORITE);
                    return;
                default:
                    Log.wtf(LOG_TAG, "Click event without treatment. (item id: " + itemId + ")");
            }
        });

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(PARAM_MOVIE_LIST_TYPE)) {
                mMovieListType = savedInstanceState.getString(PARAM_MOVIE_LIST_TYPE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMovies(mMovieListType);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(PARAM_MOVIE_LIST_TYPE, mMovieListType);
        super.onSaveInstanceState(outState);
    }

    private void loadMovies(String listType) {
        mMovieListType = listType;
        boolean isCached = mMoviesRepository.getMovies(this, listType);
        if(!isCached) {
            showLoading(getResources().getString(R.string.movie_list_ac_loading_videos));
        }
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

    @Override
    public void onMoviesLoaded(List<Movie> movies, String listType) {
        mAdapter.setMovies(movies);
        showContent();
    }

    @Override
    public void onDataNotAvailable(int errorCode, String errorMessage) {
        Log.w(LOG_TAG, errorMessage + " (" + errorCode + ")");
        showError(errorMessage);
    }
}
