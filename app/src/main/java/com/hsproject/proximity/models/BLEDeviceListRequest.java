package com.hsproject.proximity.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BLEDeviceListRequest {
    @SerializedName("ble_list")
    @Expose
    private ArrayList<BLEDevice> bleDeviceArrayList;
    public BLEDeviceListRequest(ArrayList<BLEDevice> bleDeviceArrayList) {
        this.bleDeviceArrayList=bleDeviceArrayList;
    }
}
