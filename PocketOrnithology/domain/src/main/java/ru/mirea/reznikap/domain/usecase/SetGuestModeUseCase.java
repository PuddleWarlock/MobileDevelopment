package ru.mirea.reznikap.domain.usecase;

import ru.mirea.reznikap.domain.repository.AuthRepository;

public class SetGuestModeUseCase {
    private final AuthRepository authRepository;

    public SetGuestModeUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void execute() {
        authRepository.setGuestMode();
    }
}