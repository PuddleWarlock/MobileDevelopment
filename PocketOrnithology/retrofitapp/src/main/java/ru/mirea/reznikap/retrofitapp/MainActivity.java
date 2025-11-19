package ru.mirea.reznikap.retrofitapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final String BASE_URL = "https://jsonplaceholder.typicode.com/";

    private RecyclerView recyclerView;
    private TodoAdapter todoAdapter;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

         
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

         
        fetchTodos();
    }

    private void fetchTodos() {
        Call<List<Todo>> call = apiService.getTodos();

        call.enqueue(new Callback<List<Todo>>() {
            @Override
            public void onResponse(Call<List<Todo>> call, Response<List<Todo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Todo> todos = response.body();

                     
                    todoAdapter = new TodoAdapter(MainActivity.this, todos, (todo, position) -> {
                        updateTodoStatus(todo);
                    });
                    recyclerView.setAdapter(todoAdapter);
                } else {
                    Log.e(TAG, "onResponse: " + response.code());
                    Toast.makeText(MainActivity.this, "Ошибка загрузки", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Todo>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

     
    private void updateTodoStatus(Todo todo) {
        Call<Todo> call = apiService.updateTodo(todo.getId(), todo);

        call.enqueue(new Callback<Todo>() {
            @Override
            public void onResponse(Call<Todo> call, Response<Todo> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Статус обновлен на сервере: " + response.body().getTitle(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Update Successful: " + response.code());
                } else {
                    Toast.makeText(MainActivity.this, "Ошибка обновления", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Todo> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Ошибка сети при обновлении", Toast.LENGTH_SHORT).show();
            }
        });
    }
}