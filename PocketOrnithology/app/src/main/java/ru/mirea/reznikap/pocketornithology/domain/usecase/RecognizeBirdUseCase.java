package ru.mirea.reznikap.pocketornithology.domain.usecase;

import android.graphics.Bitmap;
import ru.mirea.reznikap.pocketornithology.domain.repository.OrnithologyRepository;

public class RecognizeBirdUseCase {
    private final OrnithologyRepository repository;
    public RecognizeBirdUseCase(OrnithologyRepository repository) { this.repository = repository; }
    public String execute(Bitmap image) { return repository.recognizeBird(image); }
}