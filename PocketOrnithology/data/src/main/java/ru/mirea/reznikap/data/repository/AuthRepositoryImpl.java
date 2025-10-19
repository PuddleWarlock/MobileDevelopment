package ru.mirea.reznikap.data.repository;

import com.google.firebase.auth.FirebaseAuth;

import ru.mirea.reznikap.domain.repository.AuthRepository;
import ru.mirea.reznikap.domain.repository.RepositoryCallback;

public class AuthRepositoryImpl implements AuthRepository {
    private final FirebaseAuth mAuth;
    public AuthRepositoryImpl() {
        mAuth = FirebaseAuth.getInstance();
    }
    @Override
    public void login(String email, String password, RepositoryCallback<Void> callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }
    @Override
    public void register(String email, String password, RepositoryCallback<Void> callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }
    @Override
    public void logout() {
        mAuth.signOut();
    }
}
