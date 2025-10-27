package com.example.everquillapp.api;

public class ApiConfig {
    // Change this to your backend URL
    public static final String BASE_URL = "http://10.0.2.2:3000/api/"; // For Android Emulator
    // public static final String BASE_URL = "https://your-backend.onrender.com/api/"; // For production
    
    public static final int CONNECT_TIMEOUT = 30; // seconds
    public static final int READ_TIMEOUT = 30; // seconds
    public static final int WRITE_TIMEOUT = 30; // seconds
}

