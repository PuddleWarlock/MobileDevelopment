package ru.mirea.reznikap.pocketornithology.domain.usecase;

import ru.mirea.reznikap.pocketornithology.domain.models.Observation;
import ru.mirea.reznikap.pocketornithology.domain.repository.OrnithologyRepository;
import java.util.List;

public class GetJournalUseCase {
    private final OrnithologyRepository repository;
    public GetJournalUseCase(OrnithologyRepository repository) { this.repository = repository; }
    public List<Observation> execute() { return repository.getJournal(); }
}