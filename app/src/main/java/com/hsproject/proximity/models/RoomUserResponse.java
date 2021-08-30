package com.hsproject.proximity.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RoomUserResponse implements Serializable {
    @SerializedName("uid")
    @Expose
    private int uid;

    @SerializedName("is_moderator")
    @Expose
    private boolean isModerator;

    @SerializedName("user_info")
    @Expose
    private User user;

    @SerializedName("location")
    @Expose
    private Geo location;

    public int getUid() {
        return uid;
    }

    public boolean isModerator() {
        return isModerator;
    }

    public User getUser() {
        return user;
    }

    public Geo getLocation() { return location; }
}
