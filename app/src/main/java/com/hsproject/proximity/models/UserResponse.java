package com.hsproject.proximity.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("status_code")
    @Expose
    private int statusCode;

    @SerializedName("user")
    @Expose
    private User user;

    public User getUser() {
        return user;
    }
}
