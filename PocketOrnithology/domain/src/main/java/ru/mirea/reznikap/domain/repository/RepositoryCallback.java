package ru.mirea.reznikap.domain.repository;

public interface RepositoryCallback<T> {
    void onSuccess(T data);
    void onFailure(Exception e);
}
