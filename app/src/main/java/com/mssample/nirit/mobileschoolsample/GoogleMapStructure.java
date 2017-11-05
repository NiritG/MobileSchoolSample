package com.mssample.nirit.mobileschoolsample;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GoogleMapStructure {

    @SerializedName("html_attributions")
    private List<String> html_attributions;

    @SerializedName("results")
    private List<GoogleMapsResultStructure> results;

    @SerializedName("status")
    private String status;

    public List<GoogleMapsResultStructure> getResults() {
        return results;
    }

    public String getStatus() {
        return status;
    }
}
