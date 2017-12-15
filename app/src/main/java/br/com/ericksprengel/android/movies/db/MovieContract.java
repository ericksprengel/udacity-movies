package br.com.ericksprengel.android.movies.db;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.Arrays;

import br.com.ericksprengel.android.movies.BuildConfig;
import br.com.ericksprengel.android.movies.models.Movie;

/**
 * Created by erick on 14/12/17.
 */

public class MovieContract {

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID;

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        static final String TABLE_NAME = "movies";

        static final String COLUMN_OVERVIEW = "overview";
        static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";
        static final String COLUMN_ORIGINAL_TITLE = "original_title";
        static final String COLUMN_VIDEO = "video";
        static final String COLUMN_TITLE = "title";
        static final String COLUMN_GENRE_IDS = "genre_ids";
        static final String COLUMN_POSTER_PATH = "poster_path";
        static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        static final String COLUMN_RELEASE_DATE = "release_date";
        static final String COLUMN_VOTE_AVERAGE = "vote_average";
        static final String COLUMN_POPULARITY = "popularity";
        static final String COLUMN_ADULT = "adult";
        static final String COLUMN_VOTE_COUNT = "vote_count";

        public static ContentValues getContentValues(Movie movie) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_OVERVIEW, movie.getOverview());
            values.put(COLUMN_ORIGINAL_LANGUAGE, movie.getOriginalLanguage());
            values.put(COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
            values.put(COLUMN_VIDEO, movie.isVideo());
            values.put(COLUMN_TITLE, movie.getTitle());
            values.put(COLUMN_GENRE_IDS, Arrays.toString(movie.getGenreIds().toArray()));
            values.put(COLUMN_POSTER_PATH, movie.getPosterPath());
            values.put(COLUMN_BACKDROP_PATH, movie.getBackdropPath());
            values.put(COLUMN_RELEASE_DATE, movie.getReleaseDate().getTime());
            values.put(COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
            values.put(COLUMN_POPULARITY, movie.getPopularity());
            values.put(COLUMN_ADULT, movie.isAdult());
            values.put(COLUMN_VOTE_COUNT, movie.getVoteCount());
            return values;
        }
    }
}
