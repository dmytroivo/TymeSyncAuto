package com.example.timesyncauto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        new MyTime();
        Log.println(Log.INFO, "TSAuto MyBroadcastReceiver", "START");
    }
}