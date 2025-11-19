package ru.mirea.reznikap.data.repository;

import com.google.firebase.auth.FirebaseAuth;

import ru.mirea.reznikap.data.storage.UserPrefsStorage;
import ru.mirea.reznikap.domain.repository.AuthRepository;
import ru.mirea.reznikap.domain.repository.RepositoryCallback;

public class AuthRepositoryImpl implements AuthRepository {
    private final FirebaseAuth mAuth;
    private final UserPrefsStorage userPrefs;

    public AuthRepositoryImpl(UserPrefsStorage userPrefs) {
        this.mAuth = FirebaseAuth.getInstance();
        this.userPrefs = userPrefs;
    }

    @Override
    public void login(String email, String password, RepositoryCallback<Void> callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    // 1. Сохраняем email в SharedPreferences
                    userPrefs.saveUserName(email);
                    callback.onSuccess(null);
                })
                .addOnFailureListener(callback::onFailure);
    }

    @Override
    public void register(String email, String password, RepositoryCallback<Void> callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    // 1. Сохраняем email в SharedPreferences
                    userPrefs.saveUserName(email);
                    callback.onSuccess(null);
                })
                .addOnFailureListener(callback::onFailure);
    }

    @Override
    public void logout() {
        mAuth.signOut();
        // Очищаем имя пользователя в Prefs
        userPrefs.saveUserName(null);
    }

    // --- Реализация новых методов ---

    @Override
    public void setGuestMode() {
        // Для гостя сохраняем специальное имя
        userPrefs.saveUserName("Гость");
    }

    @Override
    public boolean isGuest() {
        // Если в Firebase нет пользователя, значит это гость
        return mAuth.getCurrentUser() == null;
    }

    @Override
    public String getUserName() {
        String name = userPrefs.getUserName();
        return name != null ? name : "Гость";
    }
}
