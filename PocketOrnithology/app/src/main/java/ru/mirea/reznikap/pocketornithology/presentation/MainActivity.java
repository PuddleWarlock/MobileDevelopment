package ru.mirea.reznikap.pocketornithology.presentation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_STORAGE_PERMISSION = 101;
    private TextView resultBirdName;
    private ImageView resultImageView;
    private CardView resultCardView;
    private Button recognizeBtn, galleryBtn, saveBtn, logoutBtn;
    private ProgressBar progressBar;
    private TextView navJournal, navRecognition;

    private RecognitionViewModel viewModel;
    private byte[] lastImageBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        initViews();
        initViewModel();
        setupListeners();
        setupObservers();
    }

    private void initViews() {
        resultBirdName = findViewById(R.id.resultBirdName);
        resultImageView = findViewById(R.id.resultImageView);
        resultCardView = findViewById(R.id.resultCardView);
        recognizeBtn = findViewById(R.id.buttonRecognize);
        galleryBtn = findViewById(R.id.buttonGallery);
        saveBtn = findViewById(R.id.buttonSave);
        logoutBtn = findViewById(R.id.buttonLogout);
        progressBar = findViewById(R.id.progressBar);
        navJournal = findViewById(R.id.navJournal);
        navRecognition = findViewById(R.id.navRecognition);
    }

    private void initViewModel() {
        ViewModelFactory factory = new ViewModelFactory(getApplicationContext());
        viewModel = new ViewModelProvider(this, factory).get(RecognitionViewModel.class);
    }

    private void setupListeners() {
        recognizeBtn.setOnClickListener(v -> checkCameraPermissionAndLaunch());
        galleryBtn.setOnClickListener(v -> checkStoragePermissionAndLaunch());
        logoutBtn.setOnClickListener(v -> handleLogout());

        saveBtn.setOnClickListener(v -> {
            // Берем байты из ViewModel, они там надежно хранятся
            byte[] dataToSave = viewModel.getCurrentImageBytes();

            if (dataToSave != null) {
                String path = saveImageToInternalStorage(dataToSave);
                if (path != null) {
                    viewModel.saveCurrentObservation(path);
                } else {
                    Toast.makeText(this, "Ошибка записи файла", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Нет изображения для сохранения", Toast.LENGTH_SHORT).show();
            }
        });


        // ЛОГИКА СБРОСА (Кнопка "Распознавание" внизу)
        navRecognition.setOnClickListener(v -> {
            resetUI(); // Возвращаем экран в исходное состояние
        });

        // Навигация в журнал
        navJournal.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, JournalActivity.class));

        });
    }
    private void resetUI() {
        resultCardView.setVisibility(View.INVISIBLE);
        saveBtn.setVisibility(View.GONE);

        recognizeBtn.setVisibility(View.VISIBLE);
        galleryBtn.setVisibility(View.VISIBLE);

        resultBirdName.setText("");
        resultImageView.setImageResource(0); // Очистка картинки
        lastImageBytes = null; // Очистка байтов
    }
    private void setupObservers() {
        viewModel.getIsLoading().observe(this, isLoading ->
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE));


        viewModel.getImageBitmap().observe(this, bitmap -> {
            if (bitmap != null) {
                resultImageView.setImageBitmap(bitmap);
            }
        });
        viewModel.getBirdInfo().observe(this, birdInfo -> {
            resultBirdName.setText(birdInfo.name);

            // Показываем результат
            resultCardView.setVisibility(View.VISIBLE);
            saveBtn.setVisibility(View.VISIBLE);
            recognizeBtn.setVisibility(View.GONE);
            galleryBtn.setVisibility(View.GONE);
        });

        viewModel.getError().observe(this, errorMsg ->
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show());

        viewModel.getLogoutEvent().observe(this, loggedOut -> {
            if (loggedOut) {
                startActivity(new Intent(this, AuthActivity.class));
                finish();
            }
        });

        viewModel.getSaveSuccess().observe(this, saved -> {
            if (saved) {
                Toast.makeText(this, "Успешно сохранено в журнал!", Toast.LENGTH_SHORT).show();
                resetUI(); // Сбрасываем экран после сохранения
            }
        });
    }

    private String saveImageToInternalStorage(byte[] imageBytes) {
        try {
            // Создаем уникальное имя файла
            String fileName = "bird_" + System.currentTimeMillis() + ".jpg";

            // Получаем путь к папке приложения (внутреннее хранилище)
            File file = new File(getFilesDir(), fileName);

            // Записываем байты
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(imageBytes);
            fos.close();

            // Возвращаем абсолютный путь к файлу (например, /data/user/0/.../bird_123.jpg)
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
                // Для галереи используем надежный способ
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        imageBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), imageUri));
                    } else {
                        imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    }
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