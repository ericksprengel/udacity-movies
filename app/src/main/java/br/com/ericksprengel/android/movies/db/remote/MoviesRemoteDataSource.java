package br.com.ericksprengel.android.movies.db.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import br.com.ericksprengel.android.movies.R;
import br.com.ericksprengel.android.movies.api.TheMovieDbApiError;
import br.com.ericksprengel.android.movies.api.TheMovieDbServices;
import br.com.ericksprengel.android.movies.api.TheMovieDbServicesBuilder;
import br.com.ericksprengel.android.movies.db.MoviesDataSource;
import br.com.ericksprengel.android.movies.models.Movie;
import br.com.ericksprengel.android.movies.models.MovieListResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by erick on 16/12/17.
 */

public class MoviesRemoteDataSource implements MoviesDataSource {

    private static final int ERROR_CODE_DEFAULT = 1000;

    private static MoviesRemoteDataSource INSTANCE;

    private TheMovieDbServices mTheMovieDbServices;
    private Context mContext;

    private MoviesRemoteDataSource(TheMovieDbServices theMovieDbServices, Context context) {
        mTheMovieDbServices = theMovieDbServices;
        mContext = context;
    }

    public static MoviesRemoteDataSource getInstance(TheMovieDbServices theMovieDbServices, Context context) {
        if (INSTANCE == null) {
            synchronized (MoviesRemoteDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MoviesRemoteDataSource(theMovieDbServices, context);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void getMovies(@NonNull LoadMoviesCallback callback, String listType) {
        Call<MovieListResponse> call = mTheMovieDbServices.getMovieList(listType);
        call.enqueue(new Callback<MovieListResponse>() {

            @Override
            public void onResponse(Call<MovieListResponse> call, Response<MovieListResponse> response) {
                if(response.isSuccessful()) {
                    MovieListResponse movieListResponse = response.body();
                    if(movieListResponse == null) {
                        callback.onDataNotAvailable(ERROR_CODE_DEFAULT,
                                mContext.getResources().getString(R.string.movie_list_ac_api_request_error));
                    }
                    List<Movie> movies = movieListResponse.getResults();
                    callback.onMoviesLoaded(movies, listType);
                } else {
                    TheMovieDbApiError error = TheMovieDbServicesBuilder.parseError(response, mContext);
                    callback.onDataNotAvailable(error.getStatusCode(), error.getStatusMessage() != null ?
                            error.getStatusMessage() :
                            mContext.getResources().getString(R.string.movie_list_ac_api_request_error));
                }
            }

            @Override
            public void onFailure(Call<MovieListResponse> call, Throwable t) {
                callback.onDataNotAvailable(ERROR_CODE_DEFAULT,
                        mContext.getResources().getString(R.string.connection_error));
            }
        });
    }

    @Override
    public void getReviews(@NonNull LoadReviewsCallback callback) {

    }

    @Override
    public void favoriteMovie(@NonNull Movie movie) {
        // there is no favorite movie remotely
    }

    @Override
    public void unfavoriteMovie(@NonNull Movie movie) {
        // there is no favorite movie remotely
    }

    @Override
    public void refreshMovies() {

    }
}
