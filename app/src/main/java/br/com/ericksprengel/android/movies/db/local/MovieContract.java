package br.com.ericksprengel.android.movies.db.local;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import br.com.ericksprengel.android.movies.BuildConfig;
import br.com.ericksprengel.android.movies.models.Movie;

public class MovieContract {

    private static final int TYPE_BOOLEAN_FALSE = 0;
    private static final int TYPE_BOOLEAN_TRUE = 1;

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
        static final String COLUMN_FAVORITE = "favorite";

        public static ContentValues getContentValues(Movie movie) {
            ContentValues values = new ContentValues();
            values.put(_ID, movie.getId());
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
            values.put(COLUMN_FAVORITE, movie.isFavorite());
            return values;
        }

        public static Movie getMovie(Cursor cursor) {
            Movie movie = new Movie();
            movie.setId(cursor.getInt(cursor.getColumnIndex(_ID)));
            movie.setOverview(cursor.getString(cursor.getColumnIndex(COLUMN_OVERVIEW)));
            movie.setOriginalLanguage(cursor.getString(cursor.getColumnIndex(COLUMN_ORIGINAL_LANGUAGE)));
            movie.setOriginalTitle(cursor.getString(cursor.getColumnIndex(COLUMN_ORIGINAL_TITLE)));
            movie.setVideo(cursor.getInt(cursor.getColumnIndex(COLUMN_VIDEO)) == TYPE_BOOLEAN_TRUE);
            movie.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
            movie.setGenreIds(getAsIntegerList(cursor.getString(cursor.getColumnIndex(COLUMN_GENRE_IDS))));
            movie.setPosterPath(cursor.getString(cursor.getColumnIndex(COLUMN_POSTER_PATH)));
            movie.setBackdropPath(cursor.getString(cursor.getColumnIndex(COLUMN_BACKDROP_PATH)));
            movie.setReleaseDate(new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_RELEASE_DATE))));
            movie.setVoteAverage(cursor.getInt(cursor.getColumnIndex(COLUMN_VOTE_AVERAGE)));
            movie.setPopularity(cursor.getDouble(cursor.getColumnIndex(COLUMN_POPULARITY)));
            movie.setAdult(cursor.getInt(cursor.getColumnIndex(COLUMN_ADULT)) == TYPE_BOOLEAN_TRUE);
            movie.setVoteCount(cursor.getInt(cursor.getColumnIndex(COLUMN_VOTE_COUNT)));
            movie.setFavorite(cursor.getInt(cursor.getColumnIndex(COLUMN_FAVORITE)) == TYPE_BOOLEAN_TRUE);
            return movie;
        }

        private static List<Integer> getAsIntegerList(String value) {
            String[] strings = value.replace("[", "").replace("]", "").split(", ");
            List<Integer> result = new ArrayList<>();
            for (String string : strings) {
                result.add(Integer.parseInt(string));
            }
            return result;
        }
    }
}
