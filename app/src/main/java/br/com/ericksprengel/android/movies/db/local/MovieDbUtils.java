package br.com.ericksprengel.android.movies.db.local;

import android.content.Context;
import android.database.Cursor;

import br.com.ericksprengel.android.movies.models.Movie;

/**
 * Created by erick.sprengel on 12/15/2017.
 */

public class MovieDbUtils {

    public static boolean isFavoriteMovie(Context context, Movie movie) {
        Cursor cursor = context.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                    new String[] {"count(*) AS count"},
                MovieContract.MovieEntry._ID + "=?",
                new String[]{String.valueOf(movie.getId())},
                null);

        cursor.moveToFirst();
        boolean isFavorite = cursor.getInt(0) != 0;
        cursor.close();
        return isFavorite;
    }
}
