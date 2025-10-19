package ru.mirea.reznikap.data.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPrefsStorage {
    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_USER_NAME = "user_name";
    private final SharedPreferences sharedPreferences;

    public UserPrefsStorage(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveUserName(String name) {
        sharedPreferences.edit().putString(KEY_USER_NAME, name).apply();
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "Default User");
    }
}
