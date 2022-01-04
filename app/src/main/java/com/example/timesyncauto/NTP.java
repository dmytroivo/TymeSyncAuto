package com.example.timesyncauto;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class NTP extends AsyncTask<String, Integer, Long> {
    TextView text_view;
    StoreNtpTime ntpStoreNtpTime;
    boolean is_foreground;
    String TIME_SERVER = "time-a.nist.gov";

    public NTP(TextView textV, StoreNtpTime storeNtpTime){
        text_view = textV;
        ntpStoreNtpTime = storeNtpTime;
        is_foreground = true;
    }

    public NTP(StoreNtpTime storeNtpTime){
        is_foreground = false;
        ntpStoreNtpTime = storeNtpTime;
    }

    @Override
    protected Long doInBackground(String ...args) {
        long timeMills;
        NTPUDPClient timeClient = new NTPUDPClient();
        try {
            Log.println(Log.INFO, "NTP doInBackground", "start...");
            InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
            TimeInfo timeInfo = timeClient.getTime(inetAddress);
            timeMills = timeInfo.getMessage().getTransmitTimeStamp().getTime();
        } catch (UnknownHostException err){
            timeMills = -1L;
        } catch (IOException err) {
            timeMills = -2L;
        } catch (Exception e) {
            timeMills = -3L;
        }
        timeClient.close();
        Log.println(Log.INFO, "NTP doInBackground", "done.");
        return timeMills;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (is_foreground){
            text_view.setText("try get time...");
        }
        Log.println(Log.INFO, "NTP onPreExecute", "try get time...");
    }

    @Override
    protected void onCancelled() {
        if (is_foreground){
            text_view.setText("task cancelled, try again");
        }
        super.onCancelled();
        Log.println(Log.INFO, "NTP onCancelled", "cancel");
    }

    @Override
    protected void onPostExecute(Long timeMills) {
        String result = "";
        super.onPostExecute(timeMills);
        if (timeMills == -1L){
            result = "UnknownHostException";
            if (is_foreground){
                text_view.setText(result);
            }
            Wifi.refresh_wifi();
        } else if (timeMills == -2L) {
            result = "IOException";
            if (is_foreground){
                text_view.setText(result);
            }
        } else if (timeMills == -3L) {
            result = "Exception";
            if (is_foreground){
                text_view.setText(result);
            }
        } else {
            if (is_foreground) {
                String value = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.UK).format(timeMills);
                text_view.setText(value);
            }
            result = timeMills.toString();
            ntpStoreNtpTime.set(timeMills);
        }
        Log.println(Log.INFO, "NTP onPostExecute", result);
    }
}

