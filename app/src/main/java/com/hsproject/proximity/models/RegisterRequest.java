package com.hsproject.proximity.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("pw")
    @Expose
    private String pw;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("photo")
    @Expose
    private String photo;

    public RegisterRequest(String id, String pw, String name, String email, String phone, String photo) {
        this.id = id;
        this.pw = pw;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.photo = photo;
    }
}
