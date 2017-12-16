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
import android.support.annotation.Nullable;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.com.ericksprengel.android.movies.api.TheMovieDbServicesBuilder;
import br.com.ericksprengel.android.movies.db.local.MoviesLocalDataSource;
import br.com.ericksprengel.android.movies.db.remote.MoviesRemoteDataSource;
import br.com.ericksprengel.android.movies.models.Movie;
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

    Map<String, List<Movie>> mCachedMovieLists = new HashMap<>();

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    boolean mCacheIsDirty = false;


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
    public void getMovies(@NonNull LoadMoviesCallback callback, String listType) {
        checkNotNull(callback);

        // Respond immediately with cache if available
        if (mCachedMovieLists.containsKey(listType)) {
            callback.onMoviesLoaded(getMoviesFromCachedDataSource(listType), listType);
            return;
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
                mMoviesLocalDataSource.getMovies(new LoadMoviesCallback() {
                    @Override
                    public void onMoviesLoaded(List<Movie> movies, String listType) {
                        refreshCache(movies, listType);
                        callback.onMoviesLoaded(getMoviesFromCachedDataSource(listType), listType);
                    }

                    @Override
                    public void onDataNotAvailable(int errorCode, String errorMessage) {
                        callback.onDataNotAvailable(errorCode, errorMessage);
                    }
                }, listType);
                break;
            default:
                throw new IllegalArgumentException("Invalid list type");
        }
    }

    @Override
    public void getReviews(@NonNull LoadReviewsCallback callback) {

    }

    @Override
    public void favoriteMovie(@NonNull Movie movie) {

    }

    @Override
    public void unfavoriteMovie(@NonNull Movie movie) {

    }

    @Override
    public void refreshMovies() {

    }



    private List<Movie> getMoviesFromCachedDataSource(String listType) {
        return new ArrayList<>(mCachedMovieLists.get(listType));
    }

    private void getMoviesFromRemoteDataSource(@NonNull final LoadMoviesCallback callback, String listType) {
        mMoviesRemoteDataSource.getMovies(new LoadMoviesCallback() {
            @Override
            public void onMoviesLoaded(List<Movie> movies, String listType) {
                refreshCache(movies, listType);
                refreshLocalDataSource(movies);
                callback.onMoviesLoaded(getMoviesFromCachedDataSource(listType), listType);
            }

            @Override
            public void onDataNotAvailable(int errorCode, String errorMessage) {
                callback.onDataNotAvailable(errorCode, errorMessage);
            }
        }, listType);
    }

    private void refreshCache(List<Movie> movies, String listType) {
        mCachedMovieLists.put(listType, movies);
    }

    private void refreshLocalDataSource(List<Movie> movies) {
        //TODO: update favorite movies information
    }
}
