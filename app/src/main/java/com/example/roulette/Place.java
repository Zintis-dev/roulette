package com.example.roulette;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Place {
    private String name;
    private String address;
    private double latitude;
    private double longitude;

    public static ArrayList<Place> list = new ArrayList<>();

    public Place(String name, String address, double latitude, double longitude) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;

        list.add(this);
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public static void addToList(Place place) {
        list.add(place);
    }

    public static void findNearbyPlaces(Context context, double latitude, double longitude, int radius, String apiKey) {
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                + latitude + "," + longitude
                + "&radius=" + radius
                + "&type=tourist_attraction&key=" + apiKey;

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        parsePlaceResults(response, context);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error fetching data", Toast.LENGTH_SHORT).show();
                Log.e("Place Error", error.toString());
            }
        });

        queue.add(jsonObjectRequest);
    }

    private static void parsePlaceResults(JSONObject response, Context context) {
        try {
            JSONArray results = response.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject placeJson = results.getJSONObject(i);
                String name = placeJson.optString("name", "N/A");
                String address = placeJson.optString("vicinity", "No address available");

                JSONObject location = placeJson.getJSONObject("geometry").getJSONObject("location");
                double latitude = location.optDouble("lat", 0.0);
                double longitude = location.optDouble("lng", 0.0);

                Place place = new Place(name, address, latitude, longitude);

                Log.d("Place Info", "Name: " + place.getName() + ", Address: " + place.getAddress() + ", Lat: " + place.getLatitude() + ", Lng: " + place.getLongitude());
            }
            Toast.makeText(context, "Found " + list.size() + " places", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Parse Error", "Error parsing place results", e);
        }
    }
}
