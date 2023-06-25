package com.sha.smartden;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

public class results {
    private int Temperature;
    private int Fire;
    private int Humidity;
    private int Gas;
    private int Latitude;
    private int Longitude;
    private int Accident;
    private String Class;

    public results(int temperature, int fire, int humidity, int gas, String aClass,int latitude, int longitude, int accident) {
        Temperature = temperature;
        Fire = fire;
        Humidity = humidity;
        Gas = gas;
        Class = aClass;
        Latitude = latitude;
        Longitude = longitude;
        Accident = accident;
    }



    public int getFire() {
        return Fire;
    }

    public void setFire(int fire) {
        Fire = fire;
    }

    public int getHumidity() {
        return Humidity;
    }

    public void setHumidity(int humidity) {
        Humidity = humidity;
    }

    public int getGas() {
        return Gas;
    }

    public void setGas(int gas) {
        Gas = gas;
    }


    public String gettClass() {
        return Class;
    }

    public void settClass(String aClass) {
        Class = aClass;
    }

    public int getMineTemperature() {
        return Temperature;
    }

    public void setMineTemperature(int mineTemperature) {
        Temperature = mineTemperature;
    }

    public int getAccident() {
        return Accident;
    }

    public void setAccident(int accident) {
        Accident = accident;
    }

    public int getLatitude() {
        return Latitude;
    }

    public void setLatitude(int latitude) {
        Latitude = latitude;
    }

    public int getLongitude() {
        return Longitude;
    }

    public void setLongitude(int longitude) {
        Longitude = longitude;
    }
}