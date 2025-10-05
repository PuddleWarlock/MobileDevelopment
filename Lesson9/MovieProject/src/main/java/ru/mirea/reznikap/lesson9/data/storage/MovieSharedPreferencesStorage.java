package ru.mirea.reznikap.lesson9.data.storage;

import android.content.Context;
import android.content.SharedPreferences;

import ru.mirea.reznikap.lesson9.domain.models.Movie;

public class MovieSharedPreferencesStorage {

    private static final String SHARED_PREFS_NAME = "movie_project_prefs";
    private static final String KEY_FAVORITE_MOVIE_ID = "favorite_movie_id";
    private static final String KEY_FAVORITE_MOVIE_NAME = "favorite_movie_name";

    private final SharedPreferences sharedPreferences;


    public MovieSharedPreferencesStorage(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }


    public boolean saveFavoriteMovie(Movie movie) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_FAVORITE_MOVIE_ID, movie.getId());
        editor.putString(KEY_FAVORITE_MOVIE_NAME, movie.getName());
        editor.apply();
        return true;
    }


    public Movie getFavoriteMovie() {
        int id = sharedPreferences.getInt(KEY_FAVORITE_MOVIE_ID, -1);
        if (id == -1) {
            return null;
        }
        String name = sharedPreferences.getString(KEY_FAVORITE_MOVIE_NAME, "Фильм не найден");
        return new Movie(id, name);
    }
}
