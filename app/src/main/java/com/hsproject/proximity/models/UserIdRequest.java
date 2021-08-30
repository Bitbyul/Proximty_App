package com.hsproject.proximity.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserIdRequest {
    @SerializedName("uid")
    @Expose
    private int uid;

    public UserIdRequest(int uid) {
        this.uid = uid;
    }
}
