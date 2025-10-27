package com.example.everquillapp.utils;

import android.util.Patterns;

public class ValidationUtils {
    
    public static boolean isValidEmail(String email) {
        return email != null && !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
    
    public static boolean isValidName(String name) {
        return name != null && name.trim().length() >= 2;
    }
    
    public static String getEmailError(String email) {
        if (email == null || email.isEmpty()) {
            return "Email is required";
        }
        if (!isValidEmail(email)) {
            return "Invalid email format";
        }
        return null;
    }
    
    public static String getPasswordError(String password) {
        if (password == null || password.isEmpty()) {
            return "Password is required";
        }
        if (!isValidPassword(password)) {
            return "Password must be at least 6 characters";
        }
        return null;
    }
    
    public static String getNameError(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Name is required";
        }
        if (!isValidName(name)) {
            return "Name must be at least 2 characters";
        }
        return null;
    }
}

