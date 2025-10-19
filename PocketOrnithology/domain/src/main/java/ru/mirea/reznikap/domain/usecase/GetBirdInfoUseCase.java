package ru.mirea.reznikap.domain.usecase;

import ru.mirea.reznikap.domain.models.BirdInfo;
import ru.mirea.reznikap.domain.repository.OrnithologyRepository;
import ru.mirea.reznikap.domain.repository.RepositoryCallback;

public class GetBirdInfoUseCase {
    private final OrnithologyRepository repository;

    public GetBirdInfoUseCase(OrnithologyRepository repository) {
        this.repository = repository;
    }

    public void execute(String birdName, RepositoryCallback<BirdInfo> callback) {
        repository.getBirdInfo(birdName, callback);
    }
}