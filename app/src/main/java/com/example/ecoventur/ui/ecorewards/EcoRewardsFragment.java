package com.example.ecoventur.ui.ecorewards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.ecoventur.R;
import com.example.ecoventur.databinding.FragmentEcorewardsBinding;

public class EcoRewardsFragment extends Fragment {

    private FragmentEcorewardsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        AppCompatActivity activity = (AppCompatActivity) requireActivity();

        // Set the title for the Toolbar in the hosting activity
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("EcoRewards");
        }
        com.example.ecoventur.ui.ecorewards.EcoRewardsViewModel EcoRewardsViewModel =
                new ViewModelProvider(this).get(com.example.ecoventur.ui.ecorewards.EcoRewardsViewModel.class);

        binding = FragmentEcorewardsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textEcorewards;
        EcoRewardsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        Button redeemVoucherButton = binding.BtnE001;
        Button ecoCoinsBalanceButton = binding.BtnE002;
        Button ecoRewardsCatalogButton = binding.BtnE003;

        redeemVoucherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the redeem voucher destination
                Navigation.findNavController(v).navigate(R.id.e00101);
            }
        });

        ecoCoinsBalanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the eco coins balance destination
                Navigation.findNavController(v).navigate(R.id.e002);
            }
        });

        ecoRewardsCatalogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the eco rewards catalog destination
                Navigation.findNavController(v).navigate(R.id.e003);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}