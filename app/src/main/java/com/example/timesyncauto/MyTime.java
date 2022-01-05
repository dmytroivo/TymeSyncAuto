package com.example.timesyncauto;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class MyTime {
    TextView timeNtpValue;
    TextView timeDeviceValue;
    TextView textLog;
    Button button_get_ntp_time;
    Button button_set_device_time;
    Switch switch_auto;
    StoreNtpTime storeNtpTime = new StoreNtpTime();
    Boolean switch_value = true;
    NTP ntp_background;
    NTP ntp_foreground;
    private static int count_update = 0;
    private static int count_set = 0;

    protected String time2str(long timeInMillis) {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.UK).format(timeInMillis);
    }

    public MyTime(){

        Integer period = 1000*5; // 5 min.
        Timer timer = new Timer();
        Log.println(Log.INFO, "TSAuto MyTime Timer.schedule", "START");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (switch_value == true) {
                    Log.println(Log.INFO, "TSAuto MyTime Timer.schedule", "RUN update_ntp_time");
                    update_ntp_time();
                    count_update++;
                    if (count_update >= 5) {
                        timer.cancel();
                        timer.purge();
                        Log.println(Log.INFO, "TSAuto MyTime Timer.schedule", "STOP update_ntp_time");
                        count_update = 0;
                        return;
                    }
                }
            }
        }, 0, period);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (switch_value == true) {
                    Log.println(Log.INFO, "TSAuto MyTime Timer.schedule", "RUN set_device_time");
                    set_device_time();
                }
                count_set++;
                if (count_set >= 5) {
                    timer.cancel();
                    timer.purge();
                    count_set = 0;
                    Log.println(Log.INFO, "TSAuto MyTime Timer.schedule", "STOP set_device_time");
                    return;
                }
            }
        }, 2000, period);

    }

    public MyTime(TextView timeNtpV, TextView timeDeviceV,
                  Button b_get_ntp_time, Button b_set_device_time,
                  Switch switch_a, TextView text_l) {
        timeNtpValue = timeNtpV;
        timeDeviceValue = timeDeviceV;
        textLog = text_l;
        button_get_ntp_time = b_get_ntp_time;
        button_set_device_time = b_set_device_time;
        switch_auto = switch_a;

        button_get_ntp_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_ntp_time(true);
                long timeDeviceInMillis = get_device_time();
                timeDeviceValue.setText(time2str(timeDeviceInMillis));
            }
        });

        button_set_device_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                set_device_time(true);
            }
        });

        switch_auto.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    switch_value = true;
                } else {
                    switch_value = false;
                }
            }
        });
    }

    protected long get_device_time() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis();
    }

    protected void update_ntp_time() {
        if (ntp_background == null
                || ntp_background.getStatus() == AsyncTask.Status.FINISHED
                || ntp_background.isCancelled()) {
            ntp_background = new NTP(storeNtpTime);
            ntp_background.execute();
        } else if (ntp_background.getStatus() != AsyncTask.Status.FINISHED) {
            ntp_background.cancel(true);
            ntp_background = new NTP(storeNtpTime);
            ntp_background.execute();
        }
    }

    protected void update_ntp_time(boolean is_foreground) {
        assert is_foreground : "in function 'update_ntp_time' param is_foreground must true or not passed";
        textLog.setText("");
        if (ntp_foreground == null
                || ntp_foreground.getStatus() == AsyncTask.Status.FINISHED
                || ntp_foreground.isCancelled()) {
            ntp_foreground = new NTP(timeNtpValue, storeNtpTime);
            ntp_foreground.execute();
        } else if (ntp_foreground.getStatus() != AsyncTask.Status.RUNNING) {
            ntp_foreground.cancel(true);
            ntp_foreground = new NTP(timeNtpValue, storeNtpTime);
            ntp_foreground.execute();
        }
    }

    private String process_set_time(){
        long timeMills = storeNtpTime.get();
        long timeDeviceMills = get_device_time();
        String strDate = "";
        String text_result = "check internet";
        Log.println(Log.INFO, "TSAuto MyTime process_set_time", "start");
        if (timeMills > 0) {
            if (timeMills != storeNtpTime.defaultTimeMills) {
                if (Math.abs(timeDeviceMills - timeMills) > 1000 * 60 * 2) {
                    try {
                        strDate = new SimpleDateFormat("MMddHHmmyyyy.ss", Locale.UK).format(timeMills);
                        ProcessBuilder pb = new ProcessBuilder("sh", "-c", "su");
                        Process process = pb.start();
                        DataOutputStream os = new DataOutputStream(process.getOutputStream());
                        os.writeBytes("settings put global auto_time 0\n");
                        os.flush();
                        os.writeBytes("settings put global auto_time_zone 0\n");
                        os.flush();
                        os.writeBytes("date " + strDate + "\n");
                        os.flush();
                        os.writeBytes("am broadcast -a android.intent.action.TIME_SET\n");
                        os.flush();
                        text_result = "ok";
                    } catch (IOException e) {
                        e.printStackTrace();
                        text_result = "error";
                    }
                } else {
                    text_result = "sync no need";
                }
            }
            Log.println(Log.INFO, "TSAuto MyTime process_set_time", "done: " + text_result);
        } else {
            text_result = "error";
        }
        return text_result;
    }

    protected void set_device_time(){
        process_set_time();
    }

    protected void set_device_time(boolean is_foreground) {
        assert is_foreground : "in function 'set_device_time' param is_foreground must true or not passed";
        textLog.setText("");
        String result = process_set_time();
        textLog.setText(result);
    }
}
