package com.example.roulette;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RouletteActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PlaceAdapter adapter;
    private ArrayList<Place> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roulette);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        places = Place.randomlySelected;

        if (places.isEmpty()) {
            Log.d("RouletteActivity", "No places found in randomlySelected list.");
        } else {
            adapter = new PlaceAdapter(this, places);
            recyclerView.setAdapter(adapter);
            Log.d("RouletteActivity", "Number of places: " + places.size());
        }
    }
}
