package com.example.timesyncauto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;


public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.println(Log.INFO, "TSAuto MyBroadcastReceiver", intent.getAction());
        Task task = new Task();
        task.execute();
    }

    private static class Task extends AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            new MyTime();
            return null;
        }
    }
}