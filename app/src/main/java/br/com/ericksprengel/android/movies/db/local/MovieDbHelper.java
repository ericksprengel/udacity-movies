package br.com.ericksprengel.android.movies.db.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

        final String CREATE_TABLE = "CREATE TABLE "  + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID                      + " INTEGER PRIMARY KEY, " +
                MovieContract.MovieEntry.COLUMN_OVERVIEW          + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE    + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_VIDEO             + " INTEGER, " +
                MovieContract.MovieEntry.COLUMN_TITLE             + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_GENRE_IDS         + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_POSTER_PATH       + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_BACKDROP_PATH     + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE      + " INTEGER NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE      + " REAL NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_POPULARITY        + " REAL, " +
                MovieContract.MovieEntry.COLUMN_ADULT             + " INTEGER NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_VOTE_COUNT        + " INTEGER NOT NULL," +
                MovieContract.MovieEntry.COLUMN_FAVORITE          + " INTEGER NOT NULL);";
        db.execSQL(CREATE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}
