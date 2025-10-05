package ru.mirea.reznikap.pocketornithology.data.repository;

import android.graphics.Bitmap;
import ru.mirea.reznikap.pocketornithology.domain.models.BirdInfo;
import ru.mirea.reznikap.pocketornithology.domain.models.Observation;
import ru.mirea.reznikap.pocketornithology.domain.repository.OrnithologyRepository;
import java.util.ArrayList;
import java.util.List;

public class OrnithologyRepositoryImpl implements OrnithologyRepository {

    @Override
    public String recognizeBird(Bitmap image) {
        return "Синица большая";
    }

    @Override
    public BirdInfo getBirdInfo(String birdName) {
        return new BirdInfo(
            birdName,
            "Синицы — семейство небольших птиц из отряда воробьинообразных. " +
            "Они широко распространены в Северном полушарии и Африке.",
            "https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/Parus_major_in_winter-2.jpg/800px-Parus_major_in_winter-2.jpg"
        );
    }

    @Override
    public boolean saveObservation(Observation observation) {
        System.out.println("Сохранение наблюдения для птицы: " + observation.birdName);
        return true;
    }

    @Override
    public List<Observation> getJournal() {
        List<Observation> journal = new ArrayList<>();
        journal.add(new Observation(1, "Снегирь", "/path/to/fake/image1.jpg", System.currentTimeMillis()));
        journal.add(new Observation(2, "Воробей", "/path/to/fake/image2.jpg", System.currentTimeMillis() - 86400000));
        return journal;
    }
}