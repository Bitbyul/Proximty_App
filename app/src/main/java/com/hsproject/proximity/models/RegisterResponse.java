package com.hsproject.proximity.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RegisterResponse implements Serializable {
    @SerializedName("status_code")
    @Expose
    private int statusCode;

    @SerializedName("created_uid")
    @Expose
    private int createdUid;

    @SerializedName("message")
    @Expose
    private String message;

    public int getStatusCode() {
        return statusCode;
    }
}
