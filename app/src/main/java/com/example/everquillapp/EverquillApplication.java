package com.example.everquillapp;

import android.app.Application;

public class EverquillApplication extends Application {
    
    private static EverquillApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static EverquillApplication getInstance() {
        return instance;
    }
}


