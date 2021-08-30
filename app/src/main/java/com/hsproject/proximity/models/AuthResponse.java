package com.hsproject.proximity.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("status_code")
    @Expose
    private int statusCode;

    @SerializedName("access_token")
    @Expose
    private String authToken;

    @SerializedName("refresh_token")
    @Expose
    private String refreshToken;


    public AuthResponse(int statusCode, String authToken, String refreshToken) {
        this.statusCode = statusCode;
        this.authToken = authToken;
        this.refreshToken = refreshToken;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getAuthToken() {
        return authToken;
    }
    public String getRefreshToken() {
        return refreshToken;
    }

    @NonNull
    @Override
    public String toString() {
        return "AuthResponse(statusCode=" + statusCode + ", authToken=" + authToken + ", refreshToken=" + refreshToken + ")";
    }
}
