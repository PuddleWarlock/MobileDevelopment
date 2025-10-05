package ru.mirea.reznikap.pocketornithology.presentation;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;

import ru.mirea.reznikap.pocketornithology.R;
import ru.mirea.reznikap.pocketornithology.data.repository.OrnithologyRepositoryImpl;
import ru.mirea.reznikap.pocketornithology.domain.models.Observation;
import ru.mirea.reznikap.pocketornithology.domain.repository.OrnithologyRepository;
import ru.mirea.reznikap.pocketornithology.domain.usecase.GetJournalUseCase;
import ru.mirea.reznikap.pocketornithology.domain.usecase.RecognizeBirdUseCase;

public class MainActivity extends AppCompatActivity {

    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        resultTextView = findViewById(R.id.resultTextView);
        Button recognizeBtn = findViewById(R.id.buttonRecognize);
        Button journalBtn = findViewById(R.id.buttonJournal);

        OrnithologyRepository repository = new OrnithologyRepositoryImpl();

        RecognizeBirdUseCase recognizeBirdUseCase = new RecognizeBirdUseCase(repository);
        GetJournalUseCase getJournalUseCase = new GetJournalUseCase(repository);

        recognizeBtn.setOnClickListener(v -> {
            String birdName = recognizeBirdUseCase.execute(null);
            resultTextView.setText(new StringBuilder().append("Распознано (тест): ").append(birdName).toString());
        });

        journalBtn.setOnClickListener(v -> {
            List<Observation> journal = getJournalUseCase.execute();
            if (journal != null && !journal.isEmpty()) {
                resultTextView.setText(new StringBuilder().append("Записей в журнале (тест): ").append(journal.size()).append("\n").append("Первая запись: ").append(journal.get(0).birdName).toString());
            } else {
                resultTextView.setText("Журнал пуст.");
            }
        });
    }
}