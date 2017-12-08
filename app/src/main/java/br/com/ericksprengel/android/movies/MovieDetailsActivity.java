package br.com.ericksprengel.android.movies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
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
        TextView releaseDate = findViewById(R.id.movie_details_ac_release_date_textview);
        TextView overview = findViewById(R.id.movie_details_ac_overview_textview);
        RatingBar voteAverage = findViewById(R.id.movie_details_ac_vote_average_ratingbar);

        Movie movie = Parcels.unwrap(getIntent().getParcelableExtra(PARAM_MOVIE));
        Picasso.with(this)
                .load(movie.getPosterThumbnailUrl(getResources()))
                .placeholder(R.drawable.movie_poster_thumbnail_placeholder)
                .into(poster);
        title.setText(movie.getTitle());
        releaseDate.setText(movie.getReleaseYear());
        overview.setText(movie.getOverview());
        voteAverage.setRating((float) movie.getVoteAverage()/2);

        super.setOnErrorClickListener(this);
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
