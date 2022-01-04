package com.example.timesyncauto;


import android.app.Application;
import android.content.Context;
import android.content.Intent;

public class MyApp extends Application {
    private static MyApp instance;

    public static Context getContext(){
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();

        startService(new Intent(this, ServiceRun.class));
    }
}