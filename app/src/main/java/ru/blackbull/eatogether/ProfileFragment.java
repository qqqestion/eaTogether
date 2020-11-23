package ru.blackbull.eatogether;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class ProfileFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_profile, container, false);
        ImageButton editProfileBtn = layout.findViewById(R.id.profile_settings);
        editProfileBtn.setOnClickListener((View.OnClickListener) getContext());

        ImageButton menuBtn = layout.findViewById(R.id.profile_menu_btn);
        menuBtn.setOnClickListener((View.OnClickListener) getContext());
        return layout;
    }
}