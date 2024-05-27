package com.example.ecoventur.ui.greenspace.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoventur.R;
import com.example.ecoventur.ui.greenspace.GreenEvent;
import com.example.ecoventur.ui.greenspace.GreenEventDetailsActivity;

import java.util.ArrayList;

public class GreenEventsAdapter extends RecyclerView.Adapter<GreenEventsAdapter.GreenEventViewHolder> {
    private ArrayList<GreenEvent> greenEvents;
    public GreenEventsAdapter(ArrayList<GreenEvent> greenEvents) {
        this.greenEvents = greenEvents;
    }
    @NonNull
    @Override
    public GreenEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_green_event, parent, false);
        return new GreenEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GreenEventViewHolder holder, int position) {
        GreenEvent currentEvent = greenEvents.get(position);
        holder.bind(currentEvent, greenEvents);
    }

    @Override
    public int getItemCount() {
        return greenEvents.size();
    }

    static class GreenEventViewHolder extends RecyclerView.ViewHolder {
        TextView TVEventName, TVEventDate_Venue, TVEventEcoCoins;
        CardView CVGreenEvent;
        public GreenEventViewHolder(@NonNull View itemView) {
            super(itemView);
            TVEventName = itemView.findViewById(R.id.TVEventName);
            TVEventDate_Venue = itemView.findViewById(R.id.TVDate_Venue);
            TVEventEcoCoins = itemView.findViewById(R.id.TVEcoCoins);
            CVGreenEvent = itemView.findViewById(R.id.CVGreenEventItem);
        }
        public void bind(GreenEvent event, ArrayList<GreenEvent> greenEvents) {
            TVEventName.setText(event.getName());
            TVEventDate_Venue.setText(event.getDate() + "\n" + event.getVenue());
            TVEventEcoCoins.setText(String.valueOf(event.getEcoCoins()));
            CVGreenEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        GreenEvent clickedEvent = greenEvents.get(position);
                        Intent intent = new Intent(v.getContext(), GreenEventDetailsActivity.class);
                        intent.putExtra("eventId", clickedEvent.getEventId());
                        v.getContext().startActivity(intent);
                    }
                }
            });
        }
    }
}
