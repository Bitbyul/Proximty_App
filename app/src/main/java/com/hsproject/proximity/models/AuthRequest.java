package com.hsproject.proximity.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthRequest {
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("pw")
    @Expose
    private String pw;

    public AuthRequest(String id, String pw) {
        this.id = id;
        this.pw = pw;
    }
}
