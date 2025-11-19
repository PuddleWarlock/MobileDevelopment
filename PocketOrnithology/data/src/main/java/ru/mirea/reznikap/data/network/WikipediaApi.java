package ru.mirea.reznikap.data.network;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WikipediaApi {

    @GET("api.php?action=query&prop=extracts|pageimages&exintro&explaintext&pithumbsize=500&format=json")
    Call<WikipediaDto> getBirdExtract(@Query("titles") String birdName);
}
