package ru.mirea.reznikap.domain.usecase;

import ru.mirea.reznikap.domain.models.Observation;
import ru.mirea.reznikap.domain.repository.OrnithologyRepository;
import ru.mirea.reznikap.domain.repository.RepositoryCallback;

public class GetObservationByIdUseCase {
    private final OrnithologyRepository repository;

    public GetObservationByIdUseCase(OrnithologyRepository repository) {
        this.repository = repository;
    }

    // Возвращает LiveData, как и предполагается в ViewModel
    // Для этого нужно будет доработать репозиторий и DAO
    // Пока сделаем заглушку с колбэком
    public void execute(int observationId, RepositoryCallback<Observation> callback) {
        repository.getObservationById(observationId, callback);
    }
}
