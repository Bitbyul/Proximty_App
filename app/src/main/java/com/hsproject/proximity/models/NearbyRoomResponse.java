package com.hsproject.proximity.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NearbyRoomResponse extends Room implements Serializable, Comparable<NearbyRoomResponse> {

    @SerializedName("now_user_count")
    @Expose
    private int nowUserCount;

    @SerializedName("is_friend_joined")
    @Expose
    private int isFriendJoined;

    @SerializedName("distance")
    @Expose
    private double distance;

    public int getNowUserCount() {
        return nowUserCount;
    }

    public int isFriendJoined() { return isFriendJoined; }

    public double getDistance() {
        return distance;
    }

    public RoomResponse toRoomResponse() {
        return new RoomResponse(this.rid, false, this);
    }

    @Override
    public int compareTo(NearbyRoomResponse o) {
        if(this.distance < o.getDistance()) {
            return -1;
        } else if(this.distance > o.getDistance()) {
            return 1;
        }
        return 0;
    }
}
