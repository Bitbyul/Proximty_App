package com.hsproject.proximity.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Room implements Serializable {
    @SerializedName("rid")
    @Expose
    protected int rid;

    @SerializedName("name")
    @Expose
    protected String name;

    @SerializedName("capacity")
    @Expose
    protected int capacity;

    @SerializedName("location_type")
    @Expose
    protected int locationType;

    @SerializedName("category_type")
    @Expose
    protected String categoryType;

    @SerializedName("preferred_type")
    @Expose
    protected String preferredType;

    @SerializedName("timeout_timestamp")
    @Expose
    protected String timeoutTimestamp;

    @SerializedName("latitude")
    @Expose
    protected double latitude;

    @SerializedName("longitude")
    @Expose
    protected double longitude;

    public int getRid() {
        return rid;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getLocationType() {
        return locationType;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public String getPreferredType() {
        return preferredType;
    }

    public String getTimeoutTimestamp() {
        return timeoutTimestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
