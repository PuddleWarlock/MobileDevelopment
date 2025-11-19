package ru.mirea.reznikap.pocketornithology.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.mirea.reznikap.pocketornithology.R;
import ru.mirea.reznikap.pocketornithology.presentation.adapters.ObservationAdapter;
import ru.mirea.reznikap.pocketornithology.presentation.factories.ViewModelFactory;
import ru.mirea.reznikap.pocketornithology.presentation.viewmodels.JournalViewModel;

public class JournalActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        TextView emptyView = findViewById(R.id.empty_view);
        TextView navRecognition = findViewById(R.id.navRecognition);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Адаптер с обработкой нажатия
        ObservationAdapter adapter = new ObservationAdapter(observation -> {
            Intent intent = new Intent(this, ObservationDetailActivity.class);
            intent.putExtra("OBSERVATION_ID", observation.id);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        ViewModelFactory factory = new ViewModelFactory(getApplicationContext());
        JournalViewModel viewModel = new ViewModelProvider(this, factory).get(JournalViewModel.class);

        viewModel.getObservations().observe(this, list -> {
            if (list.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                adapter.setObservations(list);
            }
        });

        // Навигация назад в распознавание
        navRecognition.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
    }
}
