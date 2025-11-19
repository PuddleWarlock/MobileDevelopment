package ru.mirea.reznikap.domain.usecase;

import ru.mirea.reznikap.domain.repository.AuthRepository;

public class CheckIsGuestUseCase {
    private final AuthRepository repository;

    public CheckIsGuestUseCase(AuthRepository repository) {
        this.repository = repository;
    }

    public boolean execute() {
        return repository.isGuest();
    }
}
