package ru.mirea.reznikap.data.storage;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import ru.mirea.reznikap.data.models.ObservationData;

@Dao
public interface ObservationDao {
    @Query("SELECT * FROM observations ORDER BY timestamp DESC")
    List<ObservationData> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ObservationData observation);
}
