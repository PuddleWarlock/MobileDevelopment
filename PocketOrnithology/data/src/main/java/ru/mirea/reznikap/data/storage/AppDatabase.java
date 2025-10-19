package ru.mirea.reznikap.data.storage;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ru.mirea.reznikap.data.models.ObservationData;

@Database(entities = {ObservationData.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ObservationDao observationDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "ornithology_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
