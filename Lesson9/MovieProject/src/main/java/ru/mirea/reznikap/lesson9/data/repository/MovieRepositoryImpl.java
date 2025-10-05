package ru.mirea.reznikap.lesson9.data.repository;

import android.content.Context;

import ru.mirea.reznikap.lesson9.data.storage.MovieSharedPreferencesStorage;
import ru.mirea.reznikap.lesson9.domain.models.Movie;
import ru.mirea.reznikap.lesson9.domain.repository.MovieRepository;

public class MovieRepositoryImpl implements MovieRepository {

    private final MovieSharedPreferencesStorage movieStorage;

    // Конструктор теперь принимает Context, чтобы передать его в хранилище
    public MovieRepositoryImpl(Context context) {
        this.movieStorage = new MovieSharedPreferencesStorage(context);
    }

    @Override
    public boolean saveMovie(Movie movie) {
        // Делегируем сохранение нашему классу-хранилищу
        return movieStorage.saveFavoriteMovie(movie);
    }

    @Override
    public Movie getMovie() {
        // Делегируем получение нашему классу-хранилищу
        Movie movie = movieStorage.getFavoriteMovie();
        // Если ничего не сохранено, вернем фильм по умолчанию, чтобы избежать null
        if (movie == null) {
            return new Movie(0, "Ничего не сохранено");
        }
        return movie;
    }
}
