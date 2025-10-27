package com.example.everquillapp.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class UIUtils {
    
    public static void showError(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage(message != null ? message : "An error occurred")
                .setPositiveButton("OK", null)
                .show();
    }
    
    public static void showSuccess(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("Success")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
    
    public static void showConfirmDialog(Context context, String title, String message, 
                                          Runnable onConfirm) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (onConfirm != null) {
                        onConfirm.run();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
    
    public static void hideKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && editText != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }
    
    public static String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }
}

