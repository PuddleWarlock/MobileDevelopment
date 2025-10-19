package ru.mirea.reznikap.domain.usecase;

import ru.mirea.reznikap.domain.models.Observation;
import ru.mirea.reznikap.domain.repository.OrnithologyRepository;
import ru.mirea.reznikap.domain.repository.RepositoryCallback;


public class SaveObservationUseCase {
    private final OrnithologyRepository repository;

    public SaveObservationUseCase(OrnithologyRepository repository) {
        this.repository = repository;
    }

    public void execute(Observation observation, RepositoryCallback<Void> callback) {
        repository.saveObservation(observation, callback);
    }
}