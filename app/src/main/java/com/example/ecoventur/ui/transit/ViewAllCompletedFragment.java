package com.example.ecoventur.ui.transit;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecoventur.R;
import com.example.ecoventur.ui.transit.adapters.CompletedAdapter;
import com.example.ecoventur.ui.transit.model.Completed;

import java.util.List;

public class ViewAllCompletedFragment extends Fragment {

    private List<Completed> allCompletedList;
    private RecyclerView recyclerView;
    private CompletedAdapter adapter;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("ViewAllChallengesFragment", "onCreateView");

        View root = inflater.inflate(R.layout.fragment_view_all_completed, container, false);

        allCompletedList = TransitFragment.allCompletedList;

        // Initialize RecyclerView and set Layout Manager
        recyclerView = root.findViewById(R.id.view_all_completed_recycler_view);
        adapter = new CompletedAdapter(getContext(), allCompletedList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return root;
    }
}