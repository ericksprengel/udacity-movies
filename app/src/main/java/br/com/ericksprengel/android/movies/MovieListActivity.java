package br.com.ericksprengel.android.movies;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import br.com.ericksprengel.android.movies.api.TheMovieDbApiError;
import br.com.ericksprengel.android.movies.api.TheMovieDbServicesBuilder;
import br.com.ericksprengel.android.movies.models.Movie;
import br.com.ericksprengel.android.movies.models.MovieListResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieListActivity extends BaseActivity implements View.OnClickListener, Callback<MovieListResponse>,MovieListAdapter.OnMovieClickListener {

    final private static String LOG_TAG = "MovieListActivity";

    private MovieListAdapter mAdapter;
    private Call<MovieListResponse> mMoviewListCall;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        initBaseActivity();

        RecyclerView mRecyclerView = findViewById(R.id.movie_details_ac_recycleview);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this,
                getResources().getInteger(R.integer.movie_list_ac_grid_spancount));
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MovieListAdapter(null, this);
        mRecyclerView.setAdapter(mAdapter);

        super.setOnErrorClickListener(this);

        loadMovies();
    }

    private void loadMovies() {
        mMoviewListCall = TheMovieDbServicesBuilder.build(this).getMovieList("popular");
        mMoviewListCall.enqueue(this);
        //TODO stringfy
        showLoading("Carregando vídeos...");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mMoviewListCall != null) {
            mMoviewListCall.cancel();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_error_button:
                //TODO stringfy
                loadMovies();
                showContent();
                break;
            default:
                Log.wtf(LOG_TAG, "Click event without treatment. (view id: " +view.getId()+ ")");
        }
    }



    @Override
    public void onResponse(Call<MovieListResponse> call, Response<MovieListResponse> response) {
        mMoviewListCall = null;
        if(response.isSuccessful()) {
            mAdapter.setmMovies(response.body().getResults());
            showContent();
        } else {
            TheMovieDbApiError error = TheMovieDbServicesBuilder.parseError(response, getApplicationContext());
            //TODO stringfy
            showError(error.getStatusMessage() != null ? error.getStatusMessage() : "Movies couldn't be loaded.");
        }
    }

    @Override
    public void onFailure(Call<MovieListResponse> call, Throwable t) {
        mMoviewListCall = null;
        Log.e(LOG_TAG, "Connection error.", t);
        //TODO stringfy
        showError("Seu celular está sem conexão de internet.\nTente novamente.");
    }

    @Override
    public void onMovieClick(Movie movie) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("movie", movie.getId());
        startActivity(intent);
    }
}
