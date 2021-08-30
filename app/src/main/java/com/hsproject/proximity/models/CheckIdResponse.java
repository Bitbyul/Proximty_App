package com.hsproject.proximity.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckIdResponse {
    @SerializedName("is_duplicated")
    @Expose
    private int isDuplicated;

    public int isDuplicated() {
        return isDuplicated;
    }
}
