package ru.mirea.reznikap.domain.repository;

public interface AuthRepository {
    void login(String email, String password, RepositoryCallback<Void> callback);
    void register(String email, String password, RepositoryCallback<Void> callback);
    void logout();

    void setGuestMode(); // Установить режим гостя
    boolean isGuest();   // Проверка, гость ли это
    String getUserName();
}
