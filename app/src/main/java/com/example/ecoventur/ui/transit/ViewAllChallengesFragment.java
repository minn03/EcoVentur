package com.example.ecoventur.ui.transit;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecoventur.R;
import com.example.ecoventur.ui.transit.adapters.AllChallengesAdapter;
import com.example.ecoventur.ui.transit.model.AllChallenges;

import java.util.List;

public class ViewAllChallengesFragment extends Fragment {

    private List<AllChallenges> allChallengesList;
    private RecyclerView recyclerView;
    private AllChallengesAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("ViewAllChallengesFragment", "onCreateView");

        View root = inflater.inflate(R.layout.fragment_view_all_challenges, container, false);

        allChallengesList = TransitFragment.allChallengesList;

        // Initialize RecyclerView and set Layout Manager
        recyclerView = root.findViewById(R.id.view_all_recycler_view);
        adapter = new AllChallengesAdapter(getContext(), allChallengesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return root;
    }
}