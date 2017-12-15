package br.com.ericksprengel.android.movies.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import br.com.ericksprengel.android.movies.db.MovieContract.MovieEntry;
import br.com.ericksprengel.android.movies.models.Movie;

/**
 * Created by erick on 14/12/17.
 */

class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "moviesDb.db";

    private static final int VERSION = 1;


    MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_TABLE = "CREATE TABLE "  + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID                      + " INTEGER PRIMARY KEY, " +
                MovieEntry.COLUMN_OVERVIEW          + " TEXT, " +
                MovieEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT, " +
                MovieEntry.COLUMN_ORIGINAL_TITLE    + " TEXT, " +
                MovieEntry.COLUMN_VIDEO             + " INTEGER, " +
                MovieEntry.COLUMN_TITLE             + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_GENRE_IDS         + " TEXT, " +
                MovieEntry.COLUMN_POSTER_PATH       + " TEXT, " +
                MovieEntry.COLUMN_BACKDROP_PATH     + " TEXT, " +
                MovieEntry.COLUMN_RELEASE_DATE      + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE      + " REAL NOT NULL, " +
                MovieEntry.COLUMN_POPULARITY        + " REAL, " +
                MovieEntry.COLUMN_ADULT             + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_VOTE_COUNT        + " INTEGER NOT NULL);";
        db.execSQL(CREATE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}
