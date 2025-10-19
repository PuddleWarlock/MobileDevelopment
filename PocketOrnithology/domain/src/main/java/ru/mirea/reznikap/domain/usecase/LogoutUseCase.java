package ru.mirea.reznikap.domain.usecase;

import ru.mirea.reznikap.domain.repository.AuthRepository;

public class LogoutUseCase {
    private final AuthRepository repository;

    public LogoutUseCase(AuthRepository repository) {
        this.repository = repository;
    }

    public void execute() {
        repository.logout();
    }
}
