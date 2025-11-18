package ru.mirea.reznikap.data.repository;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mirea.reznikap.data.mappers.ObservationMapper;
import ru.mirea.reznikap.data.models.ObservationData;
import ru.mirea.reznikap.data.network.ApiClient;
import ru.mirea.reznikap.data.network.WikipediaApi;
import ru.mirea.reznikap.data.network.WikipediaDto;
import ru.mirea.reznikap.data.storage.ObservationDao;
import ru.mirea.reznikap.domain.models.BirdInfo;
import ru.mirea.reznikap.domain.models.Observation;
import ru.mirea.reznikap.domain.repository.OrnithologyRepository;
import ru.mirea.reznikap.domain.repository.RepositoryCallback;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class OrnithologyRepositoryImpl implements OrnithologyRepository {

    private final ObservationDao observationDao;
    private final WikipediaApi wikipediaApi;
    private final ObservationMapper mapper = new ObservationMapper();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    public OrnithologyRepositoryImpl(ObservationDao dao) {
        this.observationDao = dao;
        this.wikipediaApi = ApiClient.getClient().create(WikipediaApi.class);
    }

    @Override
    public void recognizeBird(byte[] imageBytes, RepositoryCallback<String> callback) {
        executor.execute(() -> {
            try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                String result = "Большая синица";
                mainThreadHandler.post(() -> callback.onSuccess(result));
            } catch (Exception e) {
                mainThreadHandler.post(() -> callback.onFailure(e));
            }
        });
    }

    @Override
    public void getBirdInfo(String birdName, RepositoryCallback<BirdInfo> callback) {
        wikipediaApi.getBirdExtract(birdName).enqueue(new Callback<WikipediaDto>() {
            @Override
            public void onResponse(@NonNull Call<WikipediaDto> call, @NonNull Response<WikipediaDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WikipediaDto.Page page = response.body().query.pages.values().iterator().next();
                    BirdInfo info = new BirdInfo(page.title, page.extract, "");
                    callback.onSuccess(info);
                } else {
                    callback.onFailure(new Exception("API Error: " + response.code()));
                }
            }
            @Override
            public void onFailure(@NonNull Call<WikipediaDto> call, @NonNull Throwable t) {
                callback.onFailure(new Exception(t));
            }
        });
    }

    @Override
    public void saveObservation(Observation observation, RepositoryCallback<Void> callback) {
        executor.execute(() -> {
            try {
                observationDao.insert(mapper.mapToData(observation));
                mainThreadHandler.post(() -> callback.onSuccess(null));
            } catch (Exception e) {
                mainThreadHandler.post(() -> callback.onFailure(e));
            }
        });
    }

    @Override
    public void getJournal(RepositoryCallback<List<Observation>> callback) {
        executor.execute(() -> {
            try {
                List<Observation> journal = observationDao.getAll().stream()
                        .map(mapper::mapToDomain)
                        .collect(Collectors.toList());
                mainThreadHandler.post(() -> callback.onSuccess(journal));
            } catch (Exception e) {
                mainThreadHandler.post(() -> callback.onFailure(e));
            }
        });
    }

    @Override
    public void getObservationById(int id, RepositoryCallback<Observation> callback) {
        executor.execute(() -> {
            try {
                ObservationData data = observationDao.getById(id);
                Observation observation = mapper.mapToDomain(data);
                mainThreadHandler.post(() -> callback.onSuccess(observation));
            } catch (Exception e) {
                mainThreadHandler.post(() -> callback.onFailure(e));
            }
        });
    }
}