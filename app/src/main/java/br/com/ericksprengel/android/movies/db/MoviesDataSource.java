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

import android.support.annotation.NonNull;

import java.util.List;

import br.com.ericksprengel.android.movies.models.Movie;
import br.com.ericksprengel.android.movies.models.MovieReview;
import br.com.ericksprengel.android.movies.models.MovieVideo;

/**
 * Main entry point for accessing tasks data.
 * <p>
 * For simplicity, only getTasks() and getTask() have callbacks. Consider adding callbacks to other
 * methods to inform the user of network/database errors or successful operations.
 * For example, when a new task is created, it's synchronously stored in cache but usually every
 * operation on database or network should be executed in a different thread.
 */
public interface MoviesDataSource {

    interface LoadMoviesCallback {
        void onMoviesLoaded(List<Movie> movies, String listType);
        void onDataNotAvailable(int errorCode, String errorMessage);
    }

    interface LoadVideosCallback {
        void onVideosLoaded(List<MovieVideo> videos);
        void onDataNotAvailable(int errorCode, String errorMessage);
    }

    interface LoadReviewsCallback {
        void onReviewsLoaded(List<MovieReview> reviews);
        void onDataNotAvailable(int errorCode, String errorMessage);
    }

    void getMovies(@NonNull LoadMoviesCallback callback, String listType);
    void getReviews(@NonNull LoadReviewsCallback callback);

    void favoriteMovie(@NonNull Movie movie);

    void unfavoriteMovie(@NonNull Movie movie);

    void refreshMovies();
}
