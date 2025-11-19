package ru.mirea.reznikap.pocketornithology.presentation.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ru.mirea.reznikap.domain.models.BirdInfo;
import ru.mirea.reznikap.domain.models.Observation;
import ru.mirea.reznikap.domain.repository.OrnithologyRepository;
import ru.mirea.reznikap.domain.repository.RepositoryCallback;
import ru.mirea.reznikap.domain.usecase.GetBirdInfoUseCase;
import ru.mirea.reznikap.domain.usecase.GetObservationByIdUseCase;

public class ObservationDetailsViewModel extends ViewModel {

    private final GetObservationByIdUseCase getObservationByIdUseCase;
    private final GetBirdInfoUseCase getBirdInfoUseCase;

    private final MediatorLiveData<Observation> combinedDetails = new MediatorLiveData<>();

    public ObservationDetailsViewModel(OrnithologyRepository repository) {
        this.getObservationByIdUseCase = new GetObservationByIdUseCase(repository);
        this.getBirdInfoUseCase = new GetBirdInfoUseCase(repository);
    }

    public LiveData<Observation> getCombinedDetails() {
        return combinedDetails;
    }

    public void loadObservation(int observationId) {
         
        MutableLiveData<Observation> observationFromDb = new MutableLiveData<>();
        combinedDetails.addSource(observationFromDb, observation -> {
            if (observation != null) {
                combinedDetails.setValue(observation);
                fetchFreshInfo(observation);
            }
        });

        getObservationByIdUseCase.execute(observationId, new RepositoryCallback<Observation>() {
            @Override
            public void onSuccess(Observation data) {
                observationFromDb.setValue(data);
            }
            @Override
            public void onFailure(Exception e) {
                 
            }
        });
    }

    private void fetchFreshInfo(Observation currentObservation) {
         
        MutableLiveData<BirdInfo> freshInfoFromApi = new MutableLiveData<>();
        combinedDetails.addSource(freshInfoFromApi, freshInfo -> {
            if (freshInfo != null) {
                 
                currentObservation.description = freshInfo.description;
                combinedDetails.setValue(currentObservation);
            }
        });

        getBirdInfoUseCase.execute(currentObservation.birdName, new RepositoryCallback<BirdInfo>() {
            @Override
            public void onSuccess(BirdInfo freshInfo) {
                freshInfoFromApi.setValue(freshInfo);
            }
            @Override
            public void onFailure(Exception e) {
                 
            }
        });
    }
}
