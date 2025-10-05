package ru.mirea.reznikap.pocketornithology.domain.repository;

import android.graphics.Bitmap;
import ru.mirea.reznikap.pocketornithology.domain.models.BirdInfo;
import ru.mirea.reznikap.pocketornithology.domain.models.Observation;
import java.util.List;

public interface OrnithologyRepository {
    String recognizeBird(Bitmap image);
    BirdInfo getBirdInfo(String birdName);
    boolean saveObservation(Observation observation);
    List<Observation> getJournal();
}