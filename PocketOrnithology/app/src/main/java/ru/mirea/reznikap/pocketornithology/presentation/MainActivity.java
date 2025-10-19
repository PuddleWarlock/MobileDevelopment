package ru.mirea.reznikap.pocketornithology.presentation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import ru.mirea.reznikap.data.repository.AuthRepositoryImpl;
import ru.mirea.reznikap.data.storage.AppDatabase;
import ru.mirea.reznikap.domain.models.BirdInfo;
import ru.mirea.reznikap.domain.repository.RepositoryCallback;
import ru.mirea.reznikap.domain.usecase.GetBirdInfoUseCase;
import ru.mirea.reznikap.domain.usecase.LogoutUseCase;
import ru.mirea.reznikap.pocketornithology.R;
import ru.mirea.reznikap.data.repository.OrnithologyRepositoryImpl;
import ru.mirea.reznikap.domain.repository.OrnithologyRepository;
import ru.mirea.reznikap.domain.usecase.RecognizeBirdUseCase;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private TextView resultTextView;
    private RecognizeBirdUseCase recognizeBirdUseCase;
    private GetBirdInfoUseCase getBirdInfoUseCase;
    private LogoutUseCase logoutUseCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultTextView = findViewById(R.id.resultTextView);
        Button recognizeBtn = findViewById(R.id.buttonRecognize);
        Button logoutButton = findViewById(R.id.buttonLogout);

        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        AuthRepositoryImpl authRepository = new AuthRepositoryImpl();
        OrnithologyRepository repository = new OrnithologyRepositoryImpl(db.observationDao());
        recognizeBirdUseCase = new RecognizeBirdUseCase(repository);
        getBirdInfoUseCase = new GetBirdInfoUseCase(repository);
        logoutUseCase = new LogoutUseCase(authRepository);

        recognizeBtn.setOnClickListener(v -> dispatchTakePictureIntent());
        logoutButton.setOnClickListener(v -> handleLogout());
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            recognizeAndGetInfo(byteArray);
        }
    }

    private void recognizeAndGetInfo(byte[] imageBytes) {
        resultTextView.setText("Распознавание...");
        recognizeBirdUseCase.execute(imageBytes, new RepositoryCallback<String>() {
            @Override
            public void onSuccess(String birdName) {
                resultTextView.setText("Распознано: " + birdName + "\nЗагрузка информации...");
                fetchBirdInfo(birdName);
            }
            @Override
            public void onFailure(Exception e) {
                resultTextView.setText("Ошибка распознавания: " + e.getMessage());
            }
        });
    }

    private void fetchBirdInfo(String birdName) {
        getBirdInfoUseCase.execute(birdName, new RepositoryCallback<BirdInfo>() {
            @Override
            public void onSuccess(BirdInfo info) {
                resultTextView.setText(info.name + "\n\n" + info.description);
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MainActivity.this, "Ошибка загрузки данных: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleLogout() {
        logoutUseCase.execute();

        Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(MainActivity.this, AuthActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finish();
    }
}