package ru.mirea.reznikap.pocketornithology.presentation.viewmodels;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ru.mirea.reznikap.domain.models.BirdInfo;
import ru.mirea.reznikap.domain.models.Observation;
import ru.mirea.reznikap.domain.repository.AuthRepository;
import ru.mirea.reznikap.domain.repository.OrnithologyRepository;
import ru.mirea.reznikap.domain.repository.RepositoryCallback;
import ru.mirea.reznikap.domain.usecase.GetBirdInfoUseCase;
import ru.mirea.reznikap.domain.usecase.LogoutUseCase;
import ru.mirea.reznikap.domain.usecase.RecognizeBirdUseCase;
import ru.mirea.reznikap.domain.usecase.SaveObservationUseCase;

public class RecognitionViewModel extends ViewModel {

    private final RecognizeBirdUseCase recognizeBirdUseCase;
    private final GetBirdInfoUseCase getBirdInfoUseCase;
    private final SaveObservationUseCase saveObservationUseCase;
    private final LogoutUseCase logoutUseCase; // Добавляем LogoutUseCase

    private final MutableLiveData<String> recognitionResult = new MutableLiveData<>();
    private final MutableLiveData<BirdInfo> birdInfo = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> logoutEvent = new MutableLiveData<>();
    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>();
    private final MutableLiveData<Bitmap> imageBitmap = new MutableLiveData<>();

    private byte[] currentImageBytes;
    private BirdInfo currentBirdInfo;

    public RecognitionViewModel(OrnithologyRepository ornithologyRepository, AuthRepository authRepository) {
        this.recognizeBirdUseCase = new RecognizeBirdUseCase(ornithologyRepository);
        this.getBirdInfoUseCase = new GetBirdInfoUseCase(ornithologyRepository);
        this.saveObservationUseCase = new SaveObservationUseCase(ornithologyRepository);
        this.logoutUseCase = new LogoutUseCase(authRepository); // Инициализируем
    }

    public LiveData<String> getRecognitionResult() { return recognitionResult; }
    public LiveData<BirdInfo> getBirdInfo() { return birdInfo; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getSaveSuccess() { return saveSuccess; }
    public LiveData<Boolean> getLogoutEvent() { return logoutEvent; } // Геттер для события
    public LiveData<Bitmap> getImageBitmap() { return imageBitmap; }
    public byte[] getCurrentImageBytes() { return currentImageBytes; }

    public void startRecognition(byte[] imageBytes) {
        currentImageBytes = imageBytes;
        Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        imageBitmap.setValue(bmp);
        isLoading.setValue(true);
        recognizeBirdUseCase.execute(imageBytes, new RepositoryCallback<String>() {
            @Override
            public void onSuccess(String birdName) {
                recognitionResult.setValue(birdName);
                fetchBirdInfo(birdName);
            }
            @Override
            public void onFailure(Exception e) {
                error.setValue("Ошибка распознавания: " + e.getMessage());
                isLoading.setValue(false);
            }
        });
    }

    private void fetchBirdInfo(String birdName) {
        getBirdInfoUseCase.execute(birdName, new RepositoryCallback<BirdInfo>() {
            @Override
            public void onSuccess(BirdInfo info) {
                currentBirdInfo = info;
                birdInfo.setValue(info);
                isLoading.setValue(false);
            }
            @Override
            public void onFailure(Exception e) {
                error.setValue("Ошибка загрузки данных: " + e.getMessage());
                isLoading.setValue(false);
            }
        });
    }

    public void saveCurrentObservation(String photoPath) {
        if (currentBirdInfo == null) return;

        Observation observation = new Observation(
                0, // ID автогенерируется
                currentBirdInfo.name,
                currentBirdInfo.description, // Сохраняем описание
                photoPath,
                System.currentTimeMillis()
        );

        saveObservationUseCase.execute(observation, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                saveSuccess.setValue(true);
            }
            @Override
            public void onFailure(Exception e) {
                error.setValue("Не удалось сохранить: " + e.getMessage());
            }
        });
    }

    // Новый метод для выхода из аккаунта
    public void logout() {
        logoutUseCase.execute();
        logoutEvent.setValue(true); // Сообщаем View, что нужно выполнить навигацию
    }
}