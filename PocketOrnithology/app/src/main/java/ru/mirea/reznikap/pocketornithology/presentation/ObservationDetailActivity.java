package ru.mirea.reznikap.pocketornithology.presentation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.mirea.reznikap.pocketornithology.R;
import ru.mirea.reznikap.pocketornithology.presentation.factories.ViewModelFactory;
import ru.mirea.reznikap.pocketornithology.presentation.viewmodels.ObservationDetailsViewModel;


public class ObservationDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation_detail);

        int observationId = getIntent().getIntExtra("OBSERVATION_ID", -1);

        ImageView imageView = findViewById(R.id.detailImageView);
        TextView nameView = findViewById(R.id.detailBirdName);
        TextView dateView = findViewById(R.id.detailDate);
        TextView descView = findViewById(R.id.detailDescription);
        TextView navJournal = findViewById(R.id.navJournal); // Нижняя панель - кнопка Журнал
        TextView navRecog = findViewById(R.id.navRecognition); // Или добавьте ID в XML

        ViewModelFactory factory = new ViewModelFactory(getApplicationContext());
        ObservationDetailsViewModel viewModel = new ViewModelProvider(this, factory).get(ObservationDetailsViewModel.class);

        viewModel.loadObservation(observationId);
        if (navRecog != null) {
            navRecog.setOnClickListener(v -> {
                startActivity(new Intent(ObservationDetailActivity.this, MainActivity.class));
            });
        }
        if (navJournal != null) {
            navJournal.setOnClickListener(v -> {
                startActivity(new Intent(ObservationDetailActivity.this, JournalActivity.class));
            });
            viewModel.getCombinedDetails().observe(this, observation -> {
                if (observation == null) return;

                nameView.setText(observation.birdName);
                descView.setText(observation.description);

                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
                dateView.setText(sdf.format(new Date(observation.timestamp)));

                if (observation.photoPath != null) {
                    File imgFile = new File(observation.photoPath);
                    if (imgFile.exists()) {
                        Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        imageView.setImageBitmap(bmp);
                    }
                }
            });

            // Навигация (просто закрываем эту активити, возвращаясь в журнал)
            // Находим TextView "Журнал" в нижнем меню, но так как мы уже "в журнале",
            // по сути это действие "назад".
            // В макете у вас нижняя навигация. Кнопка "Распознавание" должна вести на MainActivity

        }
    }
}

