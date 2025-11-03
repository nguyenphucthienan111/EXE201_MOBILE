package com.example.everquillapp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    
    private static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String DISPLAY_FORMAT = "MMM dd, yyyy h:mm a";
    private static final String DATE_ONLY_FORMAT = "MMM dd, yyyy";
    
    public static String formatDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) {
            return "";
        }
        
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(ISO_FORMAT, Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat(DISPLAY_FORMAT, Locale.US);
            Date date = inputFormat.parse(isoDate);
            return date != null ? outputFormat.format(date) : isoDate;
        } catch (ParseException e) {
            return isoDate;
        }
    }
    
    public static String formatDateOnly(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) {
            return "";
        }
        
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(ISO_FORMAT, Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat(DATE_ONLY_FORMAT, Locale.US);
            Date date = inputFormat.parse(isoDate);
            return date != null ? outputFormat.format(date) : isoDate;
        } catch (ParseException e) {
            return isoDate;
        }
    }
    
    public static String getRelativeTime(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) {
            return "";
        }
        
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(ISO_FORMAT, Locale.US);
            Date date = inputFormat.parse(isoDate);
            
            if (date == null) return isoDate;
            
            long diff = System.currentTimeMillis() - date.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;
            
            if (seconds < 60) {
                return "Just now";
            } else if (minutes < 60) {
                return minutes + " min ago";
            } else if (hours < 24) {
                return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
            } else if (days < 7) {
                return days + " day" + (days > 1 ? "s" : "") + " ago";
            } else {
                return formatDateOnly(isoDate);
            }
        } catch (ParseException e) {
            return isoDate;
        }
    }
}


