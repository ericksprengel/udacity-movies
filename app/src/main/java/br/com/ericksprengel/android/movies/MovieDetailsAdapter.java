package br.com.ericksprengel.android.movies;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.ericksprengel.android.movies.models.Movie;
import br.com.ericksprengel.android.movies.models.MovieReview;
import br.com.ericksprengel.android.movies.models.MovieVideo;


public class MovieDetailsAdapter extends RecyclerView.Adapter<MovieDetailsAdapter.ViewHolder> {

    final private static String LOG_TAG = "MovieDetailsAdapter";

    private static final int VIEW_TYPE_MOVIE_INFO = 0x01;
    private static final int VIEW_TYPE_SECTION = 0x02;
    private static final int VIEW_TYPE_MOVIE_VIDEO = 0x03;
    private static final int VIEW_TYPE_MOVIE_REVIEW = 0x04;

    public interface OnMovieVideoClickListener {
        void onMovieVideoClick(MovieVideo video);
    }

    public interface OnMovieReviewClickListener {
        void onMovieReviewClick(MovieReview review);
    }

    private Movie mMovie;
    private List<MovieVideo> mMovieVideos;
    private List<MovieReview> mMovieReviews;
    private OnMovieVideoClickListener mOnClickMovieVideoListener;

    // VIEW HOLDERS - START

    abstract class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class MovieInfoViewHolder extends MovieDetailsAdapter.ViewHolder implements View.OnClickListener {
        Movie mMovie;

        private final ImageView mPoster;
        private final TextView mTitle;
        private final TextView mReleaseDate;
        private final TextView mOverview;
        private final RatingBar mVoteAverage;

        MovieInfoViewHolder(View v) {
            super(v);
            mPoster = v.findViewById(R.id.movie_details_ac_poster_imageview);
            mTitle = v.findViewById(R.id.movie_details_ac_title_textview);
            mReleaseDate = v.findViewById(R.id.movie_details_ac_release_date_textview);
            mOverview = v.findViewById(R.id.movie_details_ac_overview_textview);
            mVoteAverage = v.findViewById(R.id.movie_details_ac_vote_average_ratingbar);;

            v.setOnClickListener(this);
        }

        void updateData(Movie movie) {
            mMovie = movie;
            Picasso.with(mPoster.getContext())
                    .load(mMovie.getPosterThumbnailUrl(mPoster.getResources()))
                    .placeholder(R.drawable.movie_poster_thumbnail_placeholder)
                    .into(mPoster);

            mTitle.setText(mMovie.getTitle());
            mReleaseDate.setText(mMovie.getReleaseYear());
            mOverview.setText(mMovie.getOverview());
            mVoteAverage.setRating((float) mMovie.getVoteAverage()/2);
        }

        @Override
        public void onClick(View view) {
            MovieDetailsAdapter.this.mOnClickMovieVideoListener.onMovieVideoClick(null);
        }
    }

    public class SectionViewHolder extends MovieDetailsAdapter.ViewHolder {
        TextView mSectionTitle;

        SectionViewHolder(View v) {
            super(v);
            mSectionTitle = v.findViewById(R.id.movie_details_ac_section_item_title_textview);
        }

        void updateData(int resId) {
            mSectionTitle.setText(resId);
        }
    }

    //TODO implement
    public class MovieVideoViewHolder extends SectionViewHolder {

        MovieVideoViewHolder(View v) {
            super(v);
        }
    }

    //TODO implement
    public class MovieReviewViewHolder extends SectionViewHolder {

        MovieReviewViewHolder(View v) {
            super(v);
        }
    }


    // VIEW HOLDERS - END


    @Override
    public int getItemViewType(int position) {
        int offset = 0;

        // movie info
        if(position == offset) { return VIEW_TYPE_MOVIE_INFO; }
        offset++;

        // section trailers
        if(position == offset) { return VIEW_TYPE_SECTION; }
        offset++;

        // trailers
        if(mMovieVideos != null && position - offset < mMovieVideos.size()) {
            return VIEW_TYPE_MOVIE_VIDEO;
        }
        offset += mMovieVideos == null ? 0 : mMovieVideos.size();

        // section reviews
        if(position == offset) { return VIEW_TYPE_SECTION; }
        offset++;

        // reviews
        if(mMovieReviews != null && position - offset < mMovieReviews.size()) {
            return VIEW_TYPE_MOVIE_REVIEW;
        }
        // offset += mMovieReviews == null ? 0 : mMovieReviews.size();

        return -1;
    }



    MovieDetailsAdapter(Movie movie, List<MovieVideo> videos, List<MovieReview> reviews, OnMovieVideoClickListener listener) {
        this.mMovie = movie;
        this.mMovieVideos = videos;
        this.mMovieReviews = reviews;
        this.mOnClickMovieVideoListener = listener;
    }

    void setMovieVideos(List<MovieVideo> videos) {
        this.mMovieVideos = videos;
        notifyDataSetChanged();
    }

    void setMovieReviews(List<MovieReview> reviews) {
        this.mMovieReviews = reviews;
        notifyDataSetChanged();
    }

    @Override
    public MovieDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        View v;
        switch (viewType) {
            case VIEW_TYPE_MOVIE_INFO:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.activity_movie_details_movie_info_item, parent, false);
                return new MovieInfoViewHolder(v);
            case VIEW_TYPE_SECTION:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.activity_movie_details_section_item, parent, false);
                return new SectionViewHolder(v);
            case VIEW_TYPE_MOVIE_VIDEO:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.activity_movie_details_movie_video_item, parent, false);
                return new SectionViewHolder(v);
                //TODO return new MovieVideoViewHolder(v);
            case VIEW_TYPE_MOVIE_REVIEW:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.activity_movie_details_movie_review_item, parent, false);
                return new SectionViewHolder(v);
                //TODO return new MovieReviewViewHolder(v);

            default:
                Log.wtf(LOG_TAG, "Invalid view type (type: " + viewType + ")");
                return null;

        }
    }

    @Override
    public void onBindViewHolder(MovieDetailsAdapter.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MOVIE_INFO:
                ((MovieInfoViewHolder) holder).updateData(mMovie);
                break;
            case VIEW_TYPE_SECTION:
                ((SectionViewHolder) holder).updateData(getSectionTitle(position));
                break;
            case VIEW_TYPE_MOVIE_VIDEO:
                ((SectionViewHolder) holder).updateData(R.string.movie_details_ac_videos_section);
                //TODO ((MovieVideoViewHolder) holder).updateData(getRelativePosition(position));
                break;
            case VIEW_TYPE_MOVIE_REVIEW:
                ((SectionViewHolder) holder).updateData(R.string.movie_details_ac_videos_section);
                //TODO ((MovieReviewViewHolder) holder).updateData(getRelativePosition(position));
                break;

            default:
                Log.wtf(LOG_TAG, "Invalid view type (type: " + holder.getItemViewType() + ")");

        }
    }

    private int getRelativePosition(int position) {
        int offset = 0;

        // movie info
        if(position == offset) { return -1; }
        offset++;

        // section trailers
        if(position == offset) { return -1; }
        offset++;

        // trailers
        if(position - offset < mMovieVideos.size()) {
            return position - offset;
        }
        offset += mMovieVideos == null ? 0 : mMovieVideos.size();

        // section reviews
        if(position == offset) { return -1; }
        offset++;

        // reviews
        if(position - offset < mMovieReviews.size()) {
            return position - offset;
        }
        // offset += mMovieReviews == null ? 0 : mMovieReviews.size();

        return -1;
    }

    private int getSectionTitle(int position) {
        if(position == 1) {
            return R.string.movie_list_ac_label;
        } else {
            return R.string.movie_details_ac_reviews_section;
        }
    }

    @Override
    public int getItemCount() {
        return 1    // movie info
                + 1 // section trailers
                + (mMovieVideos == null ? 0 : mMovieVideos.size())
                + 1 // section trailers
                + (mMovieReviews == null ? 0 : mMovieReviews.size());
    }

}