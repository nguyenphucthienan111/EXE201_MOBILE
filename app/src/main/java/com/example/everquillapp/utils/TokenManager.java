package com.example.everquillapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREF_NAME = "everquill_prefs";
    private static final String KEY_TOKEN = "access_token";
    private static final String KEY_USER = "user_data";
    
    private final SharedPreferences prefs;
    
    public TokenManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }
    
    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }
    
    public void saveUserData(String userData) {
        prefs.edit().putString(KEY_USER, userData).apply();
    }
    
    public String getUserData() {
        return prefs.getString(KEY_USER, null);
    }
    
    public void clearAll() {
        prefs.edit().clear().apply();
    }
    
    public boolean isLoggedIn() {
        return getToken() != null;
    }
}


