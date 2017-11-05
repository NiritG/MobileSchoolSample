package com.mssample.nirit.mobileschoolsample;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GoogleMapsResultStructure {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("rating")
    private Double rating;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
