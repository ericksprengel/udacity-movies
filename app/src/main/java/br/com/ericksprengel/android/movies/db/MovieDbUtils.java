package br.com.ericksprengel.android.movies.db;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import br.com.ericksprengel.android.movies.MovieDetailsActivity;
import br.com.ericksprengel.android.movies.models.Movie;

/**
 * Created by erick.sprengel on 12/15/2017.
 */

public class MovieDbUtils {
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

    public static List<Movie> getFavoriteMovies(Context context) {
        Cursor cursor = context.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                null, MovieContract.MovieEntry.COLUMN_FAVORITE + "=?", new String[]{"1"}, null);
        List<Movie> movies = new ArrayList<>();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            movies.add(MovieContract.MovieEntry.getMovie(cursor)); //add the item
            cursor.moveToNext();
        }
        return movies;
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
