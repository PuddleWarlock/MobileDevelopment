package ru.mirea.reznikap.retrofitapp;

import com.google.gson.annotations.SerializedName;

public class Todo {
    @SerializedName("userId")
    private Integer userId;

    @SerializedName("id")
    private Integer id;

    @SerializedName("title")
    private String title;

    @SerializedName("completed")
    private Boolean completed;

    public Todo(Integer userId, Integer id, String title, Boolean completed) {
        this.userId = userId;
        this.id = id;
        this.title = title;
        this.completed = completed;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public String getTitle() { return title; }
    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }
}