package ru.mirea.reznikap.data.mappers;

import ru.mirea.reznikap.data.models.ObservationData;
import ru.mirea.reznikap.domain.models.Observation;

public class ObservationMapper {

    public Observation mapToDomain(ObservationData data) {
        return new Observation(data.id, data.birdName, data.description, data.photoPath, data.timestamp);
    }


    public ObservationData mapToData(Observation domain) {
        ObservationData data = new ObservationData();
        data.id = domain.id;
        data.description = domain.description;
        data.birdName = domain.birdName;
        data.photoPath = domain.photoPath;
        data.timestamp = domain.timestamp;
        return data;
    }
}
