package com.hsproject.proximity.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RoomResponse implements Serializable {
    @SerializedName("rid")
    @Expose
    private int rid;

    @SerializedName("is_moderator")
    @Expose
    private boolean isModerator;

    @SerializedName("room_info")
    @Expose
    private Room room;

    @SerializedName("joined_timestamp")
    @Expose
    protected String joinedTimestamp;

    public RoomResponse(int rid, boolean isModerator, Room room) {
        this.rid = rid;
        this.isModerator = isModerator;
        this.room = room;
    }

    public int getRid() {
        return rid;
    }
    public boolean isModerator() {
        return isModerator;
    }
    public String getJoinedTimestamp() {
        return joinedTimestamp;
    }

    public Room getRoom() {
        return room;
    }
}
