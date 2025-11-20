package ru.mirea.reznikap.domain.usecase;

import ru.mirea.reznikap.domain.repository.OrnithologyRepository;
import ru.mirea.reznikap.domain.repository.RepositoryCallback;

public class DeleteObservationUseCase {
    private final OrnithologyRepository repository;

    public DeleteObservationUseCase(OrnithologyRepository repository) {
        this.repository = repository;
    }

    public void execute(int id, RepositoryCallback<Void> callback) {
        repository.deleteObservation(id, callback);
    }
}
