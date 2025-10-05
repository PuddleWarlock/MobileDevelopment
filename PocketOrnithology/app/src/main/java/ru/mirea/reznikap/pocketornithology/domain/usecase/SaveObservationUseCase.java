package ru.mirea.reznikap.pocketornithology.domain.usecase;

import ru.mirea.reznikap.pocketornithology.domain.models.Observation;
import ru.mirea.reznikap.pocketornithology.domain.repository.OrnithologyRepository;

public class SaveObservationUseCase {
    private final OrnithologyRepository repository;
    public SaveObservationUseCase(OrnithologyRepository repository) { this.repository = repository; }
    public boolean execute(Observation observation) { return repository.saveObservation(observation); }
}