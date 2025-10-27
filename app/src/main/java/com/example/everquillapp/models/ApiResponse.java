package com.example.everquillapp.models;

import com.google.gson.annotations.SerializedName;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String error;
    
    // Multiple possible field names for token
    @SerializedName("access_token")
    private String accessToken;
    
    private String token; // Alternative field name
    
    @SerializedName("refresh_token")
    private String refreshToken;
    
    private User user;

    // Constructors
    public ApiResponse() {}

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getAccessToken() {
        // Return whichever field is present
        if (accessToken != null && !accessToken.isEmpty()) {
            return accessToken;
        }
        if (token != null && !token.isEmpty()) {
            return token;
        }
        return null;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

