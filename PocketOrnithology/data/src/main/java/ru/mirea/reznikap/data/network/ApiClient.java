package ru.mirea.reznikap.data.network;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

    private static final String BASE_URL = "https://ru.wikipedia.org/w/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {

            // --- НОВЫЙ КОД: СОЗДАНИЕ КЛИЕНТА С USER-AGENT ---

            // 1. Создаем OkHttp клиент, который будет основой для Retrofit
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            // 2. Добавляем Interceptor, который будет добавлять заголовок в каждый запрос
            httpClient.addInterceptor(chain -> {
                Request original = chain.request();
                Request request = original.newBuilder()
                        // 3. Устанавливаем заголовок User-Agent.
                        //    Формат: НазваниеПриложения/версия (контактная информация)
                        //    Это стандартная практика для API Википедии.
                        .header("User-Agent", "PocketOrnithology/1.0 (test@example.com)")
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            });

            // 4. Создаем экземпляр OkHttpClient
            OkHttpClient client = httpClient.build();

            // --- КОНЕЦ НОВОГО КОДА ---

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client) // <-- ПЕРЕДАЕМ НАШ НАСТРОЕННЫЙ КЛИЕНТ В RETROFIT
                    .build();
        }
        return retrofit;
    }
}
