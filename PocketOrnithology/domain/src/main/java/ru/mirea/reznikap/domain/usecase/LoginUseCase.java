package ru.mirea.reznikap.domain.usecase;

import ru.mirea.reznikap.domain.repository.AuthRepository;
import ru.mirea.reznikap.domain.repository.RepositoryCallback;

public class LoginUseCase {
    private final AuthRepository repository;
    public LoginUseCase(AuthRepository repository) { this.repository = repository; }
    public void execute(String email, String password, RepositoryCallback<Void> callback) {
        repository.login(email, password, callback);
    }
}
