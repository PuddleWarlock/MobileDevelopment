package ru.mirea.reznikap.retrofitapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    // Задание 1: Получение списка
    @GET("todos")
    Call<List<Todo>> getTodos();

    // Задание со стр. 12: Обновление задачи при клике на CheckBox
    @PUT("todos/{id}")
    Call<Todo> updateTodo(@Path("id") int id, @Body Todo todo);
}
