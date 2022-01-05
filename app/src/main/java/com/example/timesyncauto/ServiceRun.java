package com.example.timesyncauto;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class ServiceRun extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new MyTime();
        Log.println(Log.INFO, "TSAuto ServiceRun", "START");
        return super.onStartCommand(intent, flags, startId);
    }
}
