package com.hsproject.proximity.models;

import android.net.wifi.WifiInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Geo implements Serializable {
    @SerializedName("latitude")
    @Expose
    private double latitude;

    @SerializedName("longitude")
    @Expose
    private double longitude;

    @SerializedName("accuracy")
    @Expose
    private float accuracy;

    @SerializedName("nearby_wifi_info_list")
    @Expose
    private List<AccessPoint> nearbyWifiInfos;

    @SerializedName("nearby_ble_info_list")
    @Expose
    private List<BLEDevice> nearbyBLEInfos;

    public Geo(double latitude, double longitude, float accuracy, List<AccessPoint> nearbyWifiInfos, List<BLEDevice> nearbyBLEInfos) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.nearbyWifiInfos = nearbyWifiInfos;
        this.nearbyBLEInfos = nearbyBLEInfos;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public List<AccessPoint> getNearbyWifiInfos() {
        return nearbyWifiInfos;
    }
    public List<BLEDevice> getNearbyBLEInfos() { return nearbyBLEInfos; }
}
