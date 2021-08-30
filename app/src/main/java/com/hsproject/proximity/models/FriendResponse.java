package com.hsproject.proximity.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FriendResponse {
    @SerializedName("uid")
    @Expose
    private int uid;

    @SerializedName("user_info")
    @Expose
    private User user;

    @SerializedName("location")
    @Expose
    private Geo geo;

    public int getUid() {
        return uid;
    }

    public User getUser() {
        return user;
    }

    public Geo getGeo() { return geo; }
}
