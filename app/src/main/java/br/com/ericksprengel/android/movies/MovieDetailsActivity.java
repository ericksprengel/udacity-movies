package br.com.ericksprengel.android.movies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import br.com.ericksprengel.android.movies.models.Movie;

public class MovieDetailsActivity extends BaseActivity implements View.OnClickListener {

    final private static String LOG_TAG = "MovieListActivity";

    final private static String PARAM_MOVIE = "movie";


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

        ImageView poster = findViewById(R.id.movie_details_ac_poster_imageview);
        TextView title = findViewById(R.id.movie_details_ac_title_textview);

        Movie movie = Parcels.unwrap(getIntent().getParcelableExtra(PARAM_MOVIE));
        Picasso.with(this).load(movie.getPosterUrl(getResources()))
                .into(poster);
        title.setText(movie.getTitle());

        super.setOnErrorClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_error_button:
                Toast.makeText(this, "Error button.", Toast.LENGTH_LONG).show();
                showContent();
                break;
            default:
                Log.wtf(LOG_TAG, "Click event without treatment. (view id: " +view.getId()+ ")");
        }
    }
}
