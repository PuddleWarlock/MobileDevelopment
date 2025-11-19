package ru.mirea.reznikap.pocketornithology.presentation.factories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import ru.mirea.reznikap.data.repository.AuthRepositoryImpl;
import ru.mirea.reznikap.data.repository.OrnithologyRepositoryImpl;
import ru.mirea.reznikap.data.storage.AppDatabase;
import ru.mirea.reznikap.data.storage.UserPrefsStorage;
import ru.mirea.reznikap.domain.repository.AuthRepository;
import ru.mirea.reznikap.domain.repository.OrnithologyRepository;
import ru.mirea.reznikap.domain.usecase.CheckIsGuestUseCase;
import ru.mirea.reznikap.domain.usecase.GetBirdInfoUseCase;
import ru.mirea.reznikap.domain.usecase.GetUserNameUseCase;
import ru.mirea.reznikap.domain.usecase.LogoutUseCase;
import ru.mirea.reznikap.domain.usecase.RecognizeBirdUseCase;
import ru.mirea.reznikap.domain.usecase.SaveObservationUseCase;
import ru.mirea.reznikap.pocketornithology.presentation.viewmodels.JournalViewModel;
import ru.mirea.reznikap.pocketornithology.presentation.viewmodels.ObservationDetailsViewModel;
import ru.mirea.reznikap.pocketornithology.presentation.viewmodels.RecognitionViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final OrnithologyRepository ornithologyRepository;
    private final AuthRepository authRepository;

    public ViewModelFactory(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context.getApplicationContext());
        this.ornithologyRepository = new OrnithologyRepositoryImpl(db.observationDao());
        UserPrefsStorage userPrefs = new UserPrefsStorage(context);
        this.authRepository = new AuthRepositoryImpl(userPrefs);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RecognitionViewModel.class)) {
             
            return (T) new RecognitionViewModel(
                    new RecognizeBirdUseCase(ornithologyRepository),
                    new GetBirdInfoUseCase(ornithologyRepository),
                    new SaveObservationUseCase(ornithologyRepository),
                    new LogoutUseCase(authRepository),
                    new GetUserNameUseCase(authRepository),
                    new CheckIsGuestUseCase(authRepository)
            );
        }

        if (modelClass.isAssignableFrom(ObservationDetailsViewModel.class)) {
            return (T) new ObservationDetailsViewModel(ornithologyRepository);
        }

        if (modelClass.isAssignableFrom(JournalViewModel.class)) {
            return (T) new JournalViewModel(ornithologyRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
