package com.example.medicare.Activity;// MainActivity.java
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.medicare.Fragment.AlarmFragment;
import com.example.medicare.Fragment.Intake_Fragment;
import com.example.medicare.Fragment.ProfileFragment;
import com.example.medicare.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Home_Activity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_data) {
                selectedFragment = new Intake_Fragment();
            } else if (itemId == R.id.nav_alarm) {
                selectedFragment = new AlarmFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }

            return false;
        });

        // Set default fragment on first launch
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_data);
        }
    }
}