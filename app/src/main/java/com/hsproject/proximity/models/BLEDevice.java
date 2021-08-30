package com.hsproject.proximity.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BLEDevice {
    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("rssi")
    @Expose
    private int rssi;

    public BLEDevice(String address, int rssi) {
        this.address = address;
        this.rssi = rssi;
    }

    public String getAddress() {
        return address;
    }

    public int getRssi() {
        return rssi;
    }
}
