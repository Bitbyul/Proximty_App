package com.hsproject.proximity.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationRequest {
    @SerializedName("latitude")
    @Expose
    private double latitude;

    @SerializedName("longitude")
    @Expose
    private double longitude;

    @SerializedName("accuracy")
    @Expose
    private float accuracy;

    public LocationRequest(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = 0.0f;
    }

    public LocationRequest(double latitude, double longitude, float accuracy) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
    }

}
