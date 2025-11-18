package ru.mirea.reznikap.pocketornithology.presentation;

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

public class JournalActivity extends AppCompatActivity {

    private JournalViewModel viewModel;
    private ObservationAdapter adapter;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        // Инициализация RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        emptyView = findViewById(R.id.empty_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ObservationAdapter();
        recyclerView.setAdapter(adapter);

        // Инициализация ViewModel
        ViewModelFactory factory = new ViewModelFactory(getApplicationContext());
        viewModel = new ViewModelProvider(this, factory).get(JournalViewModel.class);

        // Подписка на данные
        viewModel.getObservations().observe(this, observations -> {
            if (observations == null || observations.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                adapter.setObservations(observations);
            }
        });

        viewModel.getError().observe(this, errorMsg -> {
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
        });
    }
}
