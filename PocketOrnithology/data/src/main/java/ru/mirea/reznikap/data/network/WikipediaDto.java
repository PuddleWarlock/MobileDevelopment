package ru.mirea.reznikap.data.network;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class WikipediaDto {
    @SerializedName("query")
    public Query query;

    public static class Query {
        @SerializedName("pages")
        public Map<String, Page> pages;
    }

    public static class Page {
        @SerializedName("title")
        public String title;
        @SerializedName("extract")
        public String extract;
        @SerializedName("thumbnail")
        public Thumbnail thumbnail;
    }
    public static class Thumbnail {
        @SerializedName("source")
        public String source;
    }
}
