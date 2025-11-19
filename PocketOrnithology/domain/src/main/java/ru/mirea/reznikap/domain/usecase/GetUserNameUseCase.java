package ru.mirea.reznikap.domain.usecase;

import ru.mirea.reznikap.domain.repository.AuthRepository;

public class GetUserNameUseCase {
    private final AuthRepository repository;

    public GetUserNameUseCase(AuthRepository repository) {
        this.repository = repository;
    }

    public String execute() {
        return repository.getUserName();
    }
}
