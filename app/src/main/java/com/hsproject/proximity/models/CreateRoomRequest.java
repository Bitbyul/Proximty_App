package com.hsproject.proximity.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CreateRoomRequest implements Serializable {

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

    @SerializedName("timeout_min")
    @Expose
    protected int timeoutMin;

    @SerializedName("latitude")
    @Expose
    protected double latitude;

    @SerializedName("longitude")
    @Expose
    protected double longitude;

    public CreateRoomRequest(String name, int capacity, int locationType, String categoryType, String preferredType, int timeoutMin, double latitude, double longitude) {
        this.name = name;
        this.capacity = capacity;
        this.locationType = locationType;
        this.categoryType = categoryType;
        this.preferredType = preferredType;
        this.timeoutMin = timeoutMin;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
