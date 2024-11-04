package com.example.roulette;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {
    private ArrayList<Place> places;
    private Context context;
    private int expandedPosition = -1;

    public PlaceAdapter(Context context, ArrayList<Place> places) {
        this.context = context;
        this.places = places;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_place, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        Place place = places.get(position);

        holder.placeName.setText(place.getName());
        holder.placeCategory.setText(place.getAddress()); // Set category if available
        place.loadImageIntoView(holder.placeImage);

        final boolean isExpanded = position == expandedPosition;
        holder.placeDescription.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.getDirectionsButton.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            expandedPosition = isExpanded ? -1 : position;
            notifyDataSetChanged();
        });

        holder.getDirectionsButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, MapActivity.class);
            intent.putExtra("lat", place.getLatitude());
            intent.putExtra("long", place.getLongitude());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public static class PlaceViewHolder extends RecyclerView.ViewHolder {
        TextView placeName, placeCategory, placeDescription;
        ImageView placeImage;
        Button getDirectionsButton;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            placeName = itemView.findViewById(R.id.place_name);
            placeCategory = itemView.findViewById(R.id.place_category);
            placeImage = itemView.findViewById(R.id.place_image);
            placeDescription = itemView.findViewById(R.id.place_description);
            getDirectionsButton = itemView.findViewById(R.id.get_directions_button);
        }
    }
}
