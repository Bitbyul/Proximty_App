package com.hsproject.proximity.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AccessPointListRequest {
    @SerializedName("ap_list")
    @Expose
    private ArrayList<AccessPoint> accessPointArrayList;
    public AccessPointListRequest(ArrayList<AccessPoint> accessPointArrayList) {
        this.accessPointArrayList=accessPointArrayList;
    }
}
