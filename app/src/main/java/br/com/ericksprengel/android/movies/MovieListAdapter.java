package br.com.ericksprengel.android.movies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.ericksprengel.android.movies.models.Movie;


public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {


    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    private List<Movie> mMovies;
    private OnMovieClickListener mOnClickMovieListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Movie mMovie;
        ImageView mPoster;

        ViewHolder(View v) {
            super(v);
            mPoster = v.findViewById(R.id.movie_list_ac_poster_imageview);
            v.setOnClickListener(this);
        }

        void updateData(Movie movie) {
            mMovie = movie;
            Picasso.with(mPoster.getContext())
                    .load(mMovie.getPosterThumbnailUrl(mPoster.getResources()))
                    .placeholder(R.drawable.movie_poster_thumbnail_placeholder)
                    .into(mPoster);
        }

        @Override
        public void onClick(View view) {
            MovieListAdapter.this.mOnClickMovieListener.onMovieClick(mMovie);
        }
    }

    MovieListAdapter(List<Movie> movies, OnMovieClickListener listener) {
        this.mMovies = movies;
        this.mOnClickMovieListener = listener;
    }

    void setMovies(List<Movie> movies) {
        this.mMovies = movies;
        notifyDataSetChanged();
    }

    @Override
    public MovieListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_movie_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Movie movie = mMovies.get(position);

        holder.updateData(movie);
    }

    @Override
    public int getItemCount() {
        return mMovies == null ? 0 : mMovies.size();
    }

}