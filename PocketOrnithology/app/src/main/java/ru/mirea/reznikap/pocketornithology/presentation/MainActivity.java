package ru.mirea.reznikap.pocketornithology.presentation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
import ru.mirea.reznikap.pocketornithology.presentation.factories.ViewModelFactory;
import ru.mirea.reznikap.pocketornithology.presentation.viewmodels.RecognitionViewModel;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_STORAGE_PERMISSION = 101;
    private TextView resultTextView;
    private ProgressBar progressBar;
    private RecognitionViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- 1. Инициализация ViewModel через Фабрику (ПРАВИЛЬНЫЙ СПОСОБ) ---
        ViewModelFactory viewModelFactory = new ViewModelFactory(getApplicationContext());
        viewModel = new ViewModelProvider(this, viewModelFactory).get(RecognitionViewModel.class);

        // --- 2. Инициализация UI ---
        resultTextView = findViewById(R.id.resultTextView); // Используйте свои ID
        progressBar = findViewById(R.id.progressBar); // Убедитесь, что ProgressBar есть в layout
        Button galleryBtn = findViewById(R.id.buttonGallery);
        Button recognizeBtn = findViewById(R.id.buttonRecognize);
        Button logoutButton = findViewById(R.id.buttonLogout);
        Button journalBtn = findViewById(R.id.buttonJournal);
        journalBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, JournalActivity.class);
            startActivity(intent);
        });

        // --- 3. Настройка наблюдателей (Observers) ---
        setupObservers();

        // --- 4. Настройка слушателей нажатий ---
        recognizeBtn.setOnClickListener(v -> checkCameraPermissionAndLaunch());
        galleryBtn.setOnClickListener(v -> checkStoragePermissionAndLaunch());
        logoutButton.setOnClickListener(v -> handleLogout());
    }

    private void setupObservers() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getBirdInfo().observe(this, birdInfo -> {
            resultTextView.setText(birdInfo.name + "\n\n" + birdInfo.description);
        });

        viewModel.getError().observe(this, errorMessage -> {
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            resultTextView.setText("Произошла ошибка");
        });

        // Новый наблюдатель для события выхода из аккаунта
        viewModel.getLogoutEvent().observe(this, isLoggedOut -> {
            if (isLoggedOut) {
                Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void checkCameraPermissionAndLaunch() {
        Log.d(TAG, "Проверка разрешения для камеры...");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Разрешения для камеры нет. Запрашиваем...");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            Log.d(TAG, "Разрешение для камеры есть. Запускаем камеру.");
            dispatchTakePictureIntent();
        }
    }

    private void checkStoragePermissionAndLaunch() {
        String permission = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        Log.d(TAG, "Проверка разрешения для хранилища: " + permission);
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Разрешения для хранилища нет. Запрашиваем...");
            ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_STORAGE_PERMISSION);
        } else {
            Log.d(TAG, "Разрешение для хранилища есть. Запускаем галерею.");
            dispatchPickPictureIntent();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Разрешение для камеры получено. Запускаем камеру.");
                dispatchTakePictureIntent();
            } else {
                Log.d(TAG, "Разрешение для камеры отклонено.");
                Toast.makeText(this, "Разрешение на использование камеры отклонено", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Разрешение для хранилища получено. Запускаем галерею.");
                dispatchPickPictureIntent();
            } else {
                Log.d(TAG, "Разрешение для хранилища отклонено.");
                Toast.makeText(this, "Разрешение на доступ к галерее отклонено", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void dispatchPickPictureIntent() {
        Intent pickPictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (pickPictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(pickPictureIntent, REQUEST_IMAGE_GALLERY);
        } else {
            Log.e(TAG, "Не найдено приложение для галереи!");
            Toast.makeText(this, "Приложение галереи не найдено", Toast.LENGTH_SHORT).show();
        }
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Log.e(TAG, "Не найдено приложение для камеры!");
            Toast.makeText(this, "Приложение камеры не найдено", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) {
            Log.d(TAG, "onActivityResult: Результат не OK или данные пустые.");
            return;
        }

        Bitmap imageBitmap = null;

        try {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Log.d(TAG, "Получен результат от камеры.");
                Bundle extras = data.getExtras();
                if (extras != null) {
                    imageBitmap = (Bitmap) extras.get("data");
                }
            } else if (requestCode == REQUEST_IMAGE_GALLERY) {
                Log.d(TAG, "Получен результат от галереи.");
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Ошибка при обработке изображения", e);
            Toast.makeText(this, "Не удалось обработать изображение", Toast.LENGTH_SHORT).show();
        }

        if (imageBitmap != null) {
            Log.d(TAG, "Изображение успешно получено. Запускаем распознавание.");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            viewModel.startRecognition(byteArray);
        } else {
            Log.w(TAG, "Изображение (Bitmap) оказалось null.");
        }
    }

    private void handleLogout() {
        viewModel.logout();
    }
}