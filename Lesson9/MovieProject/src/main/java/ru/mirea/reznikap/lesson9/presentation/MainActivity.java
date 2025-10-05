package ru.mirea.reznikap.lesson9.presentation;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ru.mirea.reznikap.lesson9.R;
import ru.mirea.reznikap.lesson9.data.repository.MovieRepositoryImpl;
import ru.mirea.reznikap.lesson9.domain.models.Movie;
import ru.mirea.reznikap.lesson9.domain.repository.MovieRepository;
import ru.mirea.reznikap.lesson9.domain.usecases.GetFavoriteFilmUseCase;
import ru.mirea.reznikap.lesson9.domain.usecases.SaveFilmToFavoriteUseCase;

public class MainActivity extends AppCompatActivity {

    private MovieRepository movieRepository;
    private EditText movieNameEditText;
    private TextView resultTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieNameEditText = findViewById(R.id.editTextMovie);
        resultTextView = findViewById(R.id.textViewMovie);
        Button saveButton = findViewById(R.id.buttonSaveMovie);
        Button getButton = findViewById(R.id.buttonGetMovie);


        movieRepository = new MovieRepositoryImpl(this);

        // Создаем экземпляры UseCase'ов
        final GetFavoriteFilmUseCase getFavoriteFilmUseCase = new GetFavoriteFilmUseCase(movieRepository);
        final SaveFilmToFavoriteUseCase saveFilmToFavoriteUseCase = new SaveFilmToFavoriteUseCase(movieRepository);

        saveButton.setOnClickListener(v -> {
            String name = movieNameEditText.getText().toString();
            if (!name.isEmpty()) {
                Movie movieToSave = new Movie(1, name);
                boolean result = saveFilmToFavoriteUseCase.execute(movieToSave);
                resultTextView.setText(new StringBuilder().append("Результат сохранения: ").append(result).toString());
            }
        });

        getButton.setOnClickListener(v -> {
            Movie favoriteMovie = getFavoriteFilmUseCase.execute();
            resultTextView.setText(new StringBuilder().append("Любимый фильм: ").append(favoriteMovie.getName()).toString());
        });
    }
}