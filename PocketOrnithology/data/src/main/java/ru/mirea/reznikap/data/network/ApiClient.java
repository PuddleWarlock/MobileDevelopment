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

             

             
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

             
            httpClient.addInterceptor(chain -> {
                Request original = chain.request();
                Request request = original.newBuilder()
                         
                         
                         
                        .header("User-Agent", "PocketOrnithology/1.0 (test@example.com)")
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            });

             
            OkHttpClient client = httpClient.build();

             

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)  
                    .build();
        }
        return retrofit;
    }
}
