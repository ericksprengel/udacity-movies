package br.com.ericksprengel.android.movies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.parceler.Parcels;

import br.com.ericksprengel.android.movies.models.Movie;
import br.com.ericksprengel.android.movies.models.MovieVideo;

public class MovieDetailsActivity extends BaseActivity implements View.OnClickListener, MovieDetailsAdapter.OnMovieVideoClickListener {

    final private static String LOG_TAG = "MovieListActivity";

    final private static String PARAM_MOVIE = "movie";
    private MovieDetailsAdapter mAdapter;


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

        Movie movie = Parcels.unwrap(getIntent().getParcelableExtra(PARAM_MOVIE));

        // Recycle view initialization
        RecyclerView mRecyclerView = findViewById(R.id.content_view);
        mRecyclerView.setHasFixedSize(false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MovieDetailsAdapter(movie, null,null, this);
        mRecyclerView.setAdapter(mAdapter);

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

    }
}
