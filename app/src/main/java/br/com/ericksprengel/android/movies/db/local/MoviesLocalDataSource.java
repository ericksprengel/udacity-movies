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

package br.com.ericksprengel.android.movies.db.local;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;

import br.com.ericksprengel.android.movies.db.MoviesDataSource;
import br.com.ericksprengel.android.movies.models.Movie;
import br.com.ericksprengel.android.movies.util.AppExecutors;

import static br.com.ericksprengel.android.movies.api.TheMovieDbServices.MOVIE_LIST_TYPE_FAVORITE;
import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Concrete implementation of a data source as a db.
 */
public class MoviesLocalDataSource implements MoviesDataSource {

    private static volatile MoviesLocalDataSource INSTANCE;
    private final AppExecutors mAppExecutors;
    private final Context mContext;

    // Prevent direct instantiation.
    private MoviesLocalDataSource(@NonNull AppExecutors appExecutors, @NonNull Context context) {
        mAppExecutors = appExecutors;
        mContext = context;
    }

    public static MoviesLocalDataSource getInstance(@NonNull AppExecutors appExecutors, @NonNull Context context) {
        if (INSTANCE == null) {
            synchronized (MoviesLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MoviesLocalDataSource(appExecutors, context);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void getMovies(@NonNull final MoviesDataSource.LoadMoviesCallback callback, String listType) {
        if(!listType.equals("favorites")) {
            return;
        }

        Runnable runnable = (Runnable) () -> {

            Cursor cursor = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                    null, MovieContract.MovieEntry.COLUMN_FAVORITE + "=?", new String[]{"1"}, null);
            List<Movie> movies = new ArrayList<>();
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                movies.add(MovieContract.MovieEntry.getMovie(cursor)); //add the item
                cursor.moveToNext();
            }

            mAppExecutors.mainThread().execute(() -> {
                 callback.onMoviesLoaded(movies, MOVIE_LIST_TYPE_FAVORITE);
            });
        };

        mAppExecutors.diskIO().execute(runnable);
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


    // TO CONVERT!!!


    public static void save(Context context, Movie movie) {
        context.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.getContentValues(movie));
    }


    public static void delete(Context context, Movie movie) {
        Uri uri = MovieContract.MovieEntry.CONTENT_URI.buildUpon()
                .appendPath(String.valueOf(movie.getId()))
                .build();
        context.getContentResolver().delete(uri,null, null);
    }

    public static boolean isFavoriteMovie(Context context, Movie movie) {
        Cursor cursor = context.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                new String[] {"count(*) AS count"},
                MovieContract.MovieEntry._ID + "=?",
                new String[]{String.valueOf(movie.getId())},
                null);

        cursor.moveToFirst();
        return cursor.getInt(0) != 0;
    }
}
