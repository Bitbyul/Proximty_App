package com.hsproject.proximity.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModifyRequest {
    @SerializedName("search_permit")
    @Expose
    private int searchPermit;

    public ModifyRequest(int searchPermit) {
        this.searchPermit = searchPermit;
    }
}
