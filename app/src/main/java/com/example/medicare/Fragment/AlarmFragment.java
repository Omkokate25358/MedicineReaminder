package com.example.medicare.Fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.medicare.R;

public class AlarmFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.fragment_alaram, container, false);

        // Find the button
        Button myButton = view.findViewById(R.id.alaram);

        // Set OnClickListener for Intent
//        myButton.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), Alarm_Activity.class);
//            startActivity(intent);
//        });

        return view;
    }
}
