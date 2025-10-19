package ru.mirea.reznikap.domain.repository;

import ru.mirea.reznikap.domain.models.BirdInfo;
import ru.mirea.reznikap.domain.models.Observation;
import java.util.List;

public interface OrnithologyRepository {
    void recognizeBird(byte[] imageBytes, RepositoryCallback<String> callback);
    void getBirdInfo(String birdName, RepositoryCallback<BirdInfo> callback);
    void saveObservation(Observation observation, RepositoryCallback<Void> callback);
    void getJournal(RepositoryCallback<List<Observation>> callback);
}