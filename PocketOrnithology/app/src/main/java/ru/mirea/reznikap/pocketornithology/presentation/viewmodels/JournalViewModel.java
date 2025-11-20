package ru.mirea.reznikap.pocketornithology.presentation.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ru.mirea.reznikap.domain.models.Observation;
import ru.mirea.reznikap.domain.repository.OrnithologyRepository;
import ru.mirea.reznikap.domain.repository.RepositoryCallback;
import ru.mirea.reznikap.domain.usecase.DeleteObservationUseCase;
import ru.mirea.reznikap.domain.usecase.GetJournalUseCase;

public class JournalViewModel extends ViewModel {

    private final GetJournalUseCase getJournalUseCase;
    private final DeleteObservationUseCase deleteObservationUseCase;
    private final MutableLiveData<List<Observation>> observations = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public JournalViewModel(OrnithologyRepository repository) {
        this.getJournalUseCase = new GetJournalUseCase(repository);
        this.deleteObservationUseCase = new DeleteObservationUseCase(repository);
        loadJournal();
    }

    public LiveData<List<Observation>> getObservations() {
        return observations;
    }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void loadJournal() {
        isLoading.setValue(true);
        getJournalUseCase.execute(new RepositoryCallback<List<Observation>>() {
            @Override
            public void onSuccess(List<Observation> data) {
                observations.setValue(data);
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(Exception e) {
                error.setValue("Ошибка загрузки журнала: " + e.getMessage());
                isLoading.setValue(false);
            }
        });
    }

    public void deleteObservation(Observation observation) {
        deleteObservationUseCase.execute(observation.id, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                // После успешного удаления перезагружаем список
                loadJournal();
            }

            @Override
            public void onFailure(Exception e) {
                error.setValue("Не удалось удалить запись");
            }
        });
    }
}
