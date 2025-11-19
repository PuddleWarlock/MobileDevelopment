package ru.mirea.reznikap.domain.usecase;

import ru.mirea.reznikap.domain.models.Observation;
import ru.mirea.reznikap.domain.repository.OrnithologyRepository;
import ru.mirea.reznikap.domain.repository.RepositoryCallback;

public class GetObservationByIdUseCase {
    private final OrnithologyRepository repository;

    public GetObservationByIdUseCase(OrnithologyRepository repository) {
        this.repository = repository;
    }

     
     
     
    public void execute(int observationId, RepositoryCallback<Observation> callback) {
        repository.getObservationById(observationId, callback);
    }
}
