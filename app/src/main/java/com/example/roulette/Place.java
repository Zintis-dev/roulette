package com.example.roulette;

import java.util.ArrayList;

public class Place {
    private String name;
    private String address;
    private double latitude;
    private double longitude;

    public static ArrayList<Place> placesList = new ArrayList<>();

    public Place(String name, String address, double latitude, double longitude) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public static void addPlace(Place place) {
        placesList.add(place);
    }
}
