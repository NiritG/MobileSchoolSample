package com.mssample.nirit.mobileschoolsample;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.gson.Gson;
import com.inneractive.api.ads.sdk.InneractiveAdManager;
import com.inneractive.api.ads.sdk.InneractiveAdView;
import com.mssample.nirit.mobileschoolsample.GoogleMapStructure;
import com.mssample.nirit.mobileschoolsample.NetworkUtility;
import com.squareup.picasso.Picasso;

import java.io.*;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private GoogleMapStructure mapper = null;
    NetworkUtility.HttpCallback placesFromGoogleListener = null;
    private Location lg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Display AppleSeeds logo
        displayImage();

        // Get device last known location
        lg = LocationUtility.getDeviceLocation(this);


        // Init INN 3rd party library
        InneractiveAdManager.initialize(this);
        InneractiveAdManager.setLogLevel(Log.VERBOSE);

    }


    // Called after button is clicked
    public void requestData(View view) {
        retrievePlacesFromGoogle(lg);
    }

    // Called after button is clicked
    public void displayData(View view) {
        
        // Display google map parsed data
        if (mapper != null) {
            displayGoogleMapOutput(mapper);
        }

        // Display banner ad
        displayAd();
    }

    private void displayImage() {

        // Find ad layout
        ImageView imageView = (ImageView)findViewById(R.id.image_view);

        // Use Picasso as a 3rd party library to load image
        Picasso.with(this)
                .load("http://appleseedsnwa.org/wp-content/uploads/2013/09/appleseeds_web_logo2.png")
                .resize(300, 200)
                .into(imageView);
    }

    // Use Google Maps API to retrieve places within 3000 radius
    private void retrievePlacesFromGoogle(Location lg) {

        // Null check and assignment of default lg if needed
        if (lg == null) {
            lg = new Location("");
            lg.setLatitude(32.101981d);
            lg.setLongitude(34.864191d);
            Log.d("INN","Couldn't retrieve device location, will use a default value: 32.101981,34.864191");
        }

        // Setup of place type and radius for search
        String placeType = "restaurant";
        int radius = 3000;


        // Create listener
        if (placesFromGoogleListener == null) {
            placesFromGoogleListener = new NetworkUtility.HttpCallback() {
                @Override
                public void onFailure() {
                    Log.d("INN", "Failed to retrieve data from Google Places");
                }

                @Override
                public void onSuccess(InputStream inputStream) {
                    Log.d("INN", "Data from Google Places retrieved");
                    mapper = parseGoogleMapOutput(inputStream);
                }
            };
        }

        // Use OkHttp (3rd party library) to send google a request (call a wrapper class)
        new NetworkUtility().get("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+ lg.getLatitude() + "," + lg.getLongitude() + "&radius=" + Integer.toString(radius) + "&type=" + placeType + "&key=AIzaSyBnDExiP34KuVx72yWgQo2HmEpQr4EQbBY", placesFromGoogleListener);
    }


    // Parse the txt file based on the declared json structure using Gson as a 3rd part library
    private GoogleMapStructure parseGoogleMapOutput (InputStream ims) {

        try {
            Log.d("INN", "Start response parsing");

            Gson gson = new Gson();
            Reader reader = new InputStreamReader(ims);

            GoogleMapStructure gsonObj = gson.fromJson(reader, GoogleMapStructure.class);

            Log.d("INN", "Response parsing is done");
            return gsonObj;

        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void displayGoogleMapOutput (GoogleMapStructure mapper) {
        TextView textView = (TextView) findViewById(R.id.restaurant_text_view);
        // Cleanup
        textView.setText("");
        if (mapper.getStatus().equals("OK") && mapper.getResults() != null) {
            List<GoogleMapsResultStructure> results = mapper.getResults();
            if (results != null) {
                for (int i = 0 ; i < results.size() ; i++) {
                    if (!results.get(i).getName().isEmpty()) {
                        textView.append(results.get(i).getName() + "\n");
                        Log.d("INN", "Restaurant name:" + results.get(i).getName());
                    }
                }
            }
        }
    }


    private void displayAd () {
        // Find ad layout
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.ad_layout);

        // Create INN banner view
        InneractiveAdView mBanner = new InneractiveAdView(this, "Nirit_MobileSchool_Android", InneractiveAdView.AdType.Banner);
        mBanner.setRefreshInterval(30);

        // Attach banner view to layout
        layout.addView(mBanner);

        // Load ad to the banner view
        mBanner.loadAd();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        // Destroy INN
        if (isFinishing()) {
            InneractiveAdManager.destroy();
        }
    }
}
