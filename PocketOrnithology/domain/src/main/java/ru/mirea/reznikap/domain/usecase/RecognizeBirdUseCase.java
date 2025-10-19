package ru.mirea.reznikap.domain.usecase;


import ru.mirea.reznikap.domain.repository.OrnithologyRepository;
import ru.mirea.reznikap.domain.repository.RepositoryCallback;

public class RecognizeBirdUseCase {
    private final OrnithologyRepository repository;

    public RecognizeBirdUseCase(OrnithologyRepository repository) {
        this.repository = repository;
    }

    public void execute(byte[] imageBytes, RepositoryCallback<String> callback) {
        repository.recognizeBird(imageBytes, callback);
    }
}