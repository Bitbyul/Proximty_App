package com.hsproject.proximity.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RoomIdRequest {
    @SerializedName("rid")
    @Expose
    private int rid;

    public RoomIdRequest(int rid) {
        this.rid = rid;
    }
}
