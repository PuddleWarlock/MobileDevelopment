package ru.mirea.reznikap.domain.models;

public class BirdInfo {
    public final String name;
    public final String description;
    public final String imageUrl;

    public BirdInfo(String name, String description, String imageUrl) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }
}