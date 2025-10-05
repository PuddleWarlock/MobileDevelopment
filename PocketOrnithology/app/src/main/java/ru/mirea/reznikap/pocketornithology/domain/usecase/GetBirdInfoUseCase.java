package ru.mirea.reznikap.pocketornithology.domain.usecase;

import ru.mirea.reznikap.pocketornithology.domain.models.BirdInfo;
import ru.mirea.reznikap.pocketornithology.domain.repository.OrnithologyRepository;

public class GetBirdInfoUseCase {
    private final OrnithologyRepository repository;
    public GetBirdInfoUseCase(OrnithologyRepository repository) { this.repository = repository; }
    public BirdInfo execute(String birdName) { return repository.getBirdInfo(birdName); }
}