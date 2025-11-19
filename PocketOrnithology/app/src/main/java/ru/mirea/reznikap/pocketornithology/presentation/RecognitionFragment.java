package ru.mirea.reznikap.pocketornithology.presentation;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import ru.mirea.reznikap.pocketornithology.R;
import ru.mirea.reznikap.pocketornithology.presentation.factories.ViewModelFactory;
import ru.mirea.reznikap.pocketornithology.presentation.viewmodels.RecognitionViewModel;

public class RecognitionFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_STORAGE_PERMISSION = 101;

    private RecognitionViewModel viewModel;
    private ImageView resultImageView;
    private TextView resultBirdName;
    private CardView resultCardView;
    private ProgressBar progressBar;
    private Button recognizeBtn, galleryBtn, saveBtn;

    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<String> requestStoragePermissionLauncher;
    private ActivityResultLauncher<Intent> takePictureLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestCameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        launchCamera();
                    } else {
                        Toast.makeText(requireContext(), "Нужен доступ к камере", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestStoragePermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        launchGallery();
                    } else {
                        Toast.makeText(requireContext(), "Нужен доступ к хранилищу", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            processImage(imageBitmap);
                        }
                    }
                }
        );

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            try {
                                Bitmap imageBitmap;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                    imageBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireActivity().getContentResolver(), imageUri));
                                } else {
                                    imageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                                }
                                processImage(imageBitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recognition, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resultImageView = view.findViewById(R.id.resultImageView);
        resultBirdName = view.findViewById(R.id.resultBirdName);
        resultCardView = view.findViewById(R.id.resultCardView);
        progressBar = view.findViewById(R.id.progressBar);
        recognizeBtn = view.findViewById(R.id.buttonRecognize);
        galleryBtn = view.findViewById(R.id.buttonGallery);
        saveBtn = view.findViewById(R.id.buttonSave);

        ViewModelFactory factory = new ViewModelFactory(requireContext());
        viewModel = new ViewModelProvider(requireActivity(), factory).get(RecognitionViewModel.class);

        setupObservers();

        recognizeBtn.setOnClickListener(v -> checkCameraPermissionAndLaunch());
        galleryBtn.setOnClickListener(v -> checkStoragePermissionAndLaunch());

        saveBtn.setOnClickListener(v -> {
            byte[] dataToSave = viewModel.getCurrentImageBytes();
            if (dataToSave != null) {
                String path = saveImageToInternalStorage(dataToSave);
                if (path != null) {
                    viewModel.saveCurrentObservation(path);
                }
            }
        });
    }

    private void setupObservers() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading ->
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE));

        viewModel.getImageBitmap().observe(getViewLifecycleOwner(), bitmap -> {
            if (bitmap != null) resultImageView.setImageBitmap(bitmap);
        });

        viewModel.getBirdInfo().observe(getViewLifecycleOwner(), birdInfo -> {
            if (birdInfo != null) {
                resultBirdName.setText(birdInfo.name);
                resultCardView.setVisibility(View.VISIBLE);
                recognizeBtn.setVisibility(View.GONE);
                galleryBtn.setVisibility(View.GONE);

                if (viewModel.isGuest()) {
                    saveBtn.setVisibility(View.GONE);
                } else {
                    saveBtn.setVisibility(View.VISIBLE);
                }
            } else {
                resetUI();
            }
        });

        viewModel.getSaveSuccess().observe(getViewLifecycleOwner(), saved -> {
            if (saved) {
                Toast.makeText(requireContext(), "Сохранено в журнал!", Toast.LENGTH_SHORT).show();
                reset();
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show());
    }

    public void reset() {
        viewModel.resetState();
    }
    private void resetUI() {
        resultCardView.setVisibility(View.INVISIBLE);
        saveBtn.setVisibility(View.GONE);
        recognizeBtn.setVisibility(View.VISIBLE);
        galleryBtn.setVisibility(View.VISIBLE);
        resultBirdName.setText("");
        resultImageView.setImageResource(0);
    }

    private String saveImageToInternalStorage(byte[] imageBytes) {
        try {
            String fileName = "bird_" + System.currentTimeMillis() + ".jpg";
            File file = new File(requireContext().getFilesDir(), fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(imageBytes);
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void checkStoragePermissionAndLaunch() {
        String permission = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            launchGallery();
        } else {
            requestStoragePermissionLauncher.launch(permission);
        }
    }

    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            takePictureLauncher.launch(takePictureIntent);
        }
    }

    private void launchGallery() {
        Intent pickPictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(pickPictureIntent);
    }

    private void processImage(Bitmap imageBitmap) {
        if (imageBitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            viewModel.startRecognition(stream.toByteArray());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK || data == null) return;

        Bitmap imageBitmap = null;
        try {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                if (extras != null) imageBitmap = (Bitmap) extras.get("data");
            } else if (requestCode == REQUEST_IMAGE_GALLERY) {
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        imageBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireActivity().getContentResolver(), imageUri));
                    } else {
                        imageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (imageBitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            viewModel.startRecognition(stream.toByteArray());
        }
    }
}