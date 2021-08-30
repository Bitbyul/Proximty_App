package com.hsproject.proximity.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AccessPoint {
    @SerializedName("frequency")
    @Expose
    private int frequency;

    @SerializedName("bssid")
    @Expose
    private String bssid;

    @SerializedName("rssi")
    @Expose
    private int rssi;

    public AccessPoint(int frequency, String bssid, int rssi) {
        this.frequency = frequency;
        this.bssid = bssid;
        this.rssi = rssi;
    }

    public int getFrequency() {
        return frequency;
    }

    public String getBssid() {
        return bssid;
    }

    public int getRssi() {
        return rssi;
    }
}
