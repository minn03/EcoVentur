package com.example.ecoventur.ui.greenspace.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecoventur.R;
import com.example.ecoventur.ui.greenspace.GreenSpace;
import com.example.ecoventur.ui.greenspace.GreenSpaceDetailsActivity;

import java.util.ArrayList;

public class GreenSpacesAdapter extends RecyclerView.Adapter<GreenSpacesAdapter.GreenSpaceViewHolder> {
    private ArrayList<GreenSpace> greenSpaces;
    private String UID;
    public GreenSpacesAdapter(ArrayList<GreenSpace> greenSpaces, String UID) {
        this.greenSpaces = greenSpaces;
        this.UID = UID;
    }
    @NonNull
    @Override
    public GreenSpaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_green_space, parent, false);
        return new GreenSpaceViewHolder(view, UID);
    }

    @Override
    public void onBindViewHolder(@NonNull GreenSpaceViewHolder holder, int position) {
        GreenSpace currentSpace = greenSpaces.get(position);
        holder.bind(currentSpace, greenSpaces);
    }

    @Override
    public int getItemCount() {
        return greenSpaces.size();
    }

    static class GreenSpaceViewHolder extends RecyclerView.ViewHolder {
        ImageView IVGreenSpace;
        TextView TVGreenSpaceName, TVApproxDist, TVRating;
        CardView CVGreenSpace;
        private String UID;
        public GreenSpaceViewHolder(@NonNull View itemView, String UID) {
            super(itemView);
            IVGreenSpace = itemView.findViewById(R.id.IVGreenSpace);
            TVGreenSpaceName = itemView.findViewById(R.id.TVGreenSpaceName);
            TVApproxDist = itemView.findViewById(R.id.TVApproxDist);
            TVRating = itemView.findViewById(R.id.TVRating);
            CVGreenSpace = itemView.findViewById(R.id.CVGreenSpaceItem);
            this.UID = UID;
        }
        public void bind(GreenSpace space, ArrayList<GreenSpace> greenSpaces) {
            if (space.getImageLink() != null){
                Glide.with(itemView.getContext())
                        .load(space.getImageLink())
                        .into(IVGreenSpace);
            }
            else {
                Glide.with(itemView.getContext())
                        .load(R.drawable.greenspace_placeholder)
                        .into(IVGreenSpace);
            }
            TVGreenSpaceName.setText(space.getName());
            TVApproxDist.setText(String.format("Approx. %.1f km", space.getApproxDistance()));
            if (space.getRating() == -1) TVRating.setText("-.-");
            else TVRating.setText(String.format("%.1f", space.getRating()));
            CVGreenSpace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        GreenSpace clickedSpace = greenSpaces.get(position);
                        Intent intent = new Intent(v.getContext(), GreenSpaceDetailsActivity.class);
                        intent.putExtra("placeId", clickedSpace.getPlaceId());
                        intent.putExtra("UID", UID);
                        v.getContext().startActivity(intent);
                    }
                }
            });
        }
    }
}
