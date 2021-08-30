package com.hsproject.proximity.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class KickUserRequest {
    @SerializedName("rid")
    @Expose
    private int rid;

    @SerializedName("target_uid")
    @Expose
    private int target_uid;

    public KickUserRequest(int rid, int target_uid) {
        this.rid = rid;
        this.target_uid = target_uid;
    }
}
