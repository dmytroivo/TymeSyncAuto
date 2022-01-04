package com.example.timesyncauto;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table_layout);

        TextView timeNtpValue = (TextView) findViewById(R.id.time_ntp_value);
        TextView timeDeviceValue = (TextView) findViewById(R.id.time_device_value);
        TextView textLog = (TextView) findViewById(R.id.text_log);
        Button button_get_ntp_time = (Button) findViewById(R.id.get_ntp_time);
        Button button_set_device_time = (Button) findViewById(R.id.set_device_time);
        Switch switch_auto = (Switch) findViewById(R.id.switch_auto);

        new MyTime(timeNtpValue, timeDeviceValue, button_get_ntp_time,
                   button_set_device_time, switch_auto, textLog);
    }
}