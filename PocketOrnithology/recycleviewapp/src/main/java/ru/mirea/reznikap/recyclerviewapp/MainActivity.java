package ru.mirea.reznikap.recyclerviewapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.mirea.reznikap.recyclerviewapp.Adapter.EventRecyclerAdapter;
import ru.mirea.reznikap.recyclerviewapp.HistoryEvents.HistoricalEvent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<HistoricalEvent> events = new ArrayList<>();

        events.add(new HistoricalEvent("Русско-Турецкая война 1568 года",
                "— война между Русским царством и Османской империей, первая из русско-турецких войн.",
            R.drawable.p1568));
        events.add(new HistoricalEvent("Русско-Турецкая война 1672 года",
                "— война Русского царства с Османской империей и вассальным ему Крымским ханством.",
                R.drawable.p1672));
        events.add(new HistoricalEvent("Русско-Турецкая война 1686 года",
                "— часть Великой Турецкой войны (1683—1699), масштабного европейского военного конфликта.",
                R.drawable.p1686));
        events.add(new HistoricalEvent("Русско-Турецкая война 1710 года",
                "— война между Русским царством и Османской империей.",
                R.drawable.p1710));
        events.add(new HistoricalEvent("Русско-Турецкая война 1735 года",
                "— война между Российской (в союзе со Священной Римской Империей) и Османской империями.",
                R.drawable.p1735));
        events.add(new HistoricalEvent("Русско-Турецкая война 1735 года",
                "— война между Российской (в союзе со Священной Римской Империей) и Османской империями.",
                R.drawable.p1735));
        events.add(new HistoricalEvent("Русско-Турецкая война 1735 года",
                "— война между Российской (в союзе со Священной Римской Империей) и Османской империями.",
                R.drawable.p1735));
        events.add(new HistoricalEvent("Русско-Турецкая война 1735 года",
                "— война между Российской (в союзе со Священной Римской Империей) и Османской империями.",
                R.drawable.p1735));

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        EventRecyclerAdapter adapter = new EventRecyclerAdapter(this, events);
        recyclerView.setAdapter(adapter);
    }
}