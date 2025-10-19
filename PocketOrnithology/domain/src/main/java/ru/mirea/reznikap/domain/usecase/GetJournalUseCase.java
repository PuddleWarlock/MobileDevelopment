package ru.mirea.reznikap.domain.usecase;

import java.util.List;

import ru.mirea.reznikap.domain.models.Observation;
import ru.mirea.reznikap.domain.repository.OrnithologyRepository;
import ru.mirea.reznikap.domain.repository.RepositoryCallback;


public class GetJournalUseCase {
    private final OrnithologyRepository repository;

    public GetJournalUseCase(OrnithologyRepository repository) {
        this.repository = repository;
    }

    public void execute(RepositoryCallback<List<Observation>> callback) {
        repository.getJournal(callback);
    }
}