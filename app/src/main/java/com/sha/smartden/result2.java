package com.sha.smartden;

import androidx.annotation.NonNull;

public class result2 {
    private int Heart;
    private int BTemp;
    private String Class;

    public result2(int heart, int temperature, String aClass) {
        Heart = heart;
        BTemp = temperature;
        Class = aClass;
    }

    public int getHeart() {
        return Heart;
    }

    public void setHeart(int heart) {
        Heart = heart;
    }

    public int getTemperature() {
        return BTemp;
    }

    public void setTemperature(int temperature) {
        BTemp = temperature;
    }


    public String gettClass() {
        return Class;
    }

    public void settClass(String aClass) {
        Class = aClass;
    }

}
