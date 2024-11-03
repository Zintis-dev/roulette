package com.example.roulette;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class RouletteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roulette);

        TextView placesText = findViewById(R.id.places_text);
        ArrayList<Place> randomPlaces = Place.randomlySelected;

        for (Place p : randomPlaces) {
            placesText.append(p.getName() + "\n");
        }
    }
}