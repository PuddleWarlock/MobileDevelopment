package ru.mirea.reznikap.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "observations")
public class ObservationData {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String description;
    public String birdName;
    public String photoPath;
    public long timestamp;
}