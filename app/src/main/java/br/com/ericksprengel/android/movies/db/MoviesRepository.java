/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.ericksprengel.android.movies.db;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.ericksprengel.android.movies.api.TheMovieDbServicesBuilder;
import br.com.ericksprengel.android.movies.db.local.MoviesLocalDataSource;
import br.com.ericksprengel.android.movies.db.remote.MoviesRemoteDataSource;
import br.com.ericksprengel.android.movies.models.Movie;
import br.com.ericksprengel.android.movies.models.MovieReview;
import br.com.ericksprengel.android.movies.models.MovieVideo;
import br.com.ericksprengel.android.movies.util.AppExecutors;

import static br.com.ericksprengel.android.movies.api.TheMovieDbServices.MOVIE_LIST_TYPE_FAVORITE;
import static br.com.ericksprengel.android.movies.api.TheMovieDbServices.MOVIE_LIST_TYPE_POPULAR;
import static br.com.ericksprengel.android.movies.api.TheMovieDbServices.MOVIE_LIST_TYPE_TOP_RATED;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Concrete implementation to load movies from the data sources into a cache.
 * <p>
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 */
public class MoviesRepository implements MoviesDataSource {

    private static MoviesRepository INSTANCE = null;

    private final MoviesDataSource mMoviesRemoteDataSource;

    private final MoviesDataSource mMoviesLocalDataSource;

    private Map<String, List<Movie>> mCachedMovieLists = new HashMap<>();
    private SparseArray<List<MovieVideo>> mCachedMovieVideoLists = new SparseArray<>();
    private SparseArray<List<MovieReview>> mCachedMovieReviewLists = new SparseArray<>();


    private MoviesRepository(@NonNull MoviesDataSource moviesRemoteDataSource,
                             @NonNull MoviesDataSource moviesLocalDataSource) {
        mMoviesRemoteDataSource = checkNotNull(moviesRemoteDataSource);
        mMoviesLocalDataSource = checkNotNull(moviesLocalDataSource);
    }

    public static MoviesRepository getInstance(Context context) {
        if (INSTANCE == null) {
            MoviesRemoteDataSource moviesRemoteDataSource =
                    MoviesRemoteDataSource.getInstance(TheMovieDbServicesBuilder.build(context), context);
            MoviesLocalDataSource moviesLocalDataSource =
                    MoviesLocalDataSource.getInstance(new AppExecutors(), context);
            INSTANCE = new MoviesRepository(moviesRemoteDataSource, moviesLocalDataSource);
        }
        return INSTANCE;
    }

    @Override
    public boolean getMovies(@NonNull LoadMoviesCallback callback, String listType) {
        checkNotNull(callback);

        // Respond immediately with cache if available
        if (mCachedMovieLists.containsKey(listType)) {
            callback.onMoviesLoaded(getMoviesFromCachedDataSource(listType), listType);
            return true;
        }

        // fetch from remote storage for popular and top_rated
        // fetch from local  storage for favorite
        switch (listType) {
            case MOVIE_LIST_TYPE_POPULAR:
            case MOVIE_LIST_TYPE_TOP_RATED:
                getMoviesFromRemoteDataSource(callback, listType);
                break;
            case MOVIE_LIST_TYPE_FAVORITE:
                // Query the local storage if available. If not, query the network.
                getMoviesFromLocalDataSource(callback, listType);
                break;
            default:
                throw new IllegalArgumentException("Invalid list type");
        }
        return false;
    }

    @Override
    public boolean getVideos(@NonNull LoadVideosCallback callback, Movie movie) {
        checkNotNull(callback);
        checkNotNull(movie);

        // Respond immediately with cache if available
        List<MovieVideo> videos = mCachedMovieVideoLists.get(movie.getId());
        if (videos != null) {
            callback.onVideosLoaded(new ArrayList<>(videos));
            return true;
        }

        // fetch from remote storage
        getVideosFromRemoteDataSource(callback, movie);
        return false;
    }

    @Override
    public boolean getReviews(@NonNull LoadReviewsCallback callback, Movie movie) {
        checkNotNull(callback);
        checkNotNull(movie);

        // Respond immediately with cache if available
        List<MovieReview> reviews = mCachedMovieReviewLists.get(movie.getId());
        if (reviews != null) {
            callback.onReviewsLoaded(new ArrayList<>(reviews));
            return true;
        }

        // fetch from remote storage
        getReviewsFromRemoteDataSource(callback, movie);
        return false;
    }

    @Override
    public void favoriteMovie(@NonNull Movie movie) {
        if(mCachedMovieLists.containsKey(MOVIE_LIST_TYPE_FAVORITE)) {
            List<Movie> movies = mCachedMovieLists.get(MOVIE_LIST_TYPE_FAVORITE);
            Integer id = movie.getId();
            boolean isAlreadyCached = movies.stream()
                    .map(Movie::getId)
                    .anyMatch(id::equals);
            if(!isAlreadyCached) {
                movies.add(movie);
            }
        }
        mMoviesLocalDataSource.favoriteMovie(movie);
    }

    @Override
    public void unfavoriteMovie(@NonNull Movie movie) {
        if(mCachedMovieLists.containsKey(MOVIE_LIST_TYPE_FAVORITE)) {
            List<Movie> movies = mCachedMovieLists.get(MOVIE_LIST_TYPE_FAVORITE);
            for(Movie m : movies) {
                if(m.getId() == movie.getId()) {
                    movies.remove(m);
                    break;
                }
            }
        }
        mMoviesLocalDataSource.unfavoriteMovie(movie);
    }



    // getMovies aux

    private List<Movie> getMoviesFromCachedDataSource(String listType) {
        return new ArrayList<>(mCachedMovieLists.get(listType));
    }

    public void getMoviesFromLocalDataSource(@NonNull final LoadMoviesCallback callback, String listType) {
        mMoviesLocalDataSource.getMovies(new LoadMoviesCallback() {
            @Override
            public void onMoviesLoaded(List<Movie> movies, String listType) {
                refreshMoviesCache(movies, listType);
                callback.onMoviesLoaded(getMoviesFromCachedDataSource(listType), listType);
            }

            @Override
            public void onDataNotAvailable(int errorCode, String errorMessage) {
                callback.onDataNotAvailable(errorCode, errorMessage);
            }
        }, listType);
    }

    private void getMoviesFromRemoteDataSource(@NonNull final LoadMoviesCallback callback, String listType) {
        mMoviesRemoteDataSource.getMovies(new LoadMoviesCallback() {
            @Override
            public void onMoviesLoaded(List<Movie> movies, String listType) {
                refreshMoviesCache(movies, listType);
                refreshLocalDataSource(movies);
                callback.onMoviesLoaded(getMoviesFromCachedDataSource(listType), listType);
            }

            @Override
            public void onDataNotAvailable(int errorCode, String errorMessage) {
                callback.onDataNotAvailable(errorCode, errorMessage);
            }
        }, listType);
    }

    private void refreshMoviesCache(List<Movie> movies, String listType) {
        mCachedMovieLists.put(listType, movies);
    }

    private void refreshLocalDataSource(List<Movie> movies) {
        //TODO: update favorite movies information
    }



    // getVideos aux

    private List<MovieVideo> getVideosFromCachedDataSource(Movie movie) {
        return new ArrayList<>(mCachedMovieVideoLists.get(movie.getId()));
    }

    private void getVideosFromRemoteDataSource(@NonNull final LoadVideosCallback callback, Movie movie) {
        mMoviesRemoteDataSource.getVideos(new LoadVideosCallback() {
            @Override
            public void onVideosLoaded(List<MovieVideo> videos) {
                refreshVideosCache(videos, movie);
                callback.onVideosLoaded(getVideosFromCachedDataSource(movie));
            }

            @Override
            public void onDataNotAvailable(int errorCode, String errorMessage) {
                callback.onDataNotAvailable(errorCode, errorMessage);
            }
        }, movie);
    }

    private void refreshVideosCache(List<MovieVideo> videos, Movie movie) {
        mCachedMovieVideoLists.put(movie.getId(), videos);
    }



    // getReviews aux

    private List<MovieReview> getReviewsFromCachedDataSource(Movie movie) {
        return new ArrayList<>(mCachedMovieReviewLists.get(movie.getId()));
    }

    private void getReviewsFromRemoteDataSource(@NonNull final LoadReviewsCallback callback, Movie movie) {
        mMoviesRemoteDataSource.getReviews(new LoadReviewsCallback() {
            @Override
            public void onReviewsLoaded(List<MovieReview> reviews) {
                refreshReviewsCache(reviews, movie);
                callback.onReviewsLoaded(getReviewsFromCachedDataSource(movie));
            }

            @Override
            public void onDataNotAvailable(int errorCode, String errorMessage) {
                callback.onDataNotAvailable(errorCode, errorMessage);
            }
        }, movie);
    }

    private void refreshReviewsCache(List<MovieReview> reviews, Movie movie) {
        mCachedMovieReviewLists.put(movie.getId(), reviews);
    }
}
