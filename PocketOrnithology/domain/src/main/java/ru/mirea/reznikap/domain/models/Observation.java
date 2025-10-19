package ru.mirea.reznikap.domain.models;

public class Observation {
    public final int id;
    public final String birdName;
    public final String photoPath;
    public final long timestamp;

    public Observation(int id, String birdName, String photoPath, long timestamp) {
        this.id = id;
        this.birdName = birdName;
        this.photoPath = photoPath;
        this.timestamp = timestamp;
    }
}