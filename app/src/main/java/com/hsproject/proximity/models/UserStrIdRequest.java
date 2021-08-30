package com.hsproject.proximity.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserStrIdRequest {
    @SerializedName("id")
    @Expose
    private String id;

    public UserStrIdRequest(String id) {
        this.id = id;
    }
}
