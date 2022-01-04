package com.example.timesyncauto;

public class StoreNtpTime{
    public long defaultTimeMills = 1640988000000L;
    private long timeMills = 1640988000000L;
    public void set(long value){
        timeMills = value;
    }
    public long get(){
        return timeMills;
    }
}
