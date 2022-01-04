package com.example.timesyncauto;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;


public class Wifi {

    static void refresh_wifi(){
        try {
            WifiManager wifiMgr = (WifiManager) MyApp.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiMgr.isWifiEnabled()){
                ProcessBuilder pb = new ProcessBuilder("sh", "-c", "su");
                Process process = pb.start();
                DataOutputStream os = new DataOutputStream(process.getOutputStream());
                os.writeBytes("svc wifi disable\n");
                os.flush();
                os.writeBytes("svc wifi enable\n");
                os.flush();
                Log.println(Log.DEBUG, "refresh_wifi", "success");
            } else {
                Log.println(Log.DEBUG, "refresh_wifi", "is disabled");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.println(Log.DEBUG, "refresh_wifi", "IOException");
        }
    }
}
