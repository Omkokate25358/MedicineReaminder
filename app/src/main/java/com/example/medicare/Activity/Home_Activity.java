package com.example.medicare.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicare.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Home_Activity extends AppCompatActivity {

    FloatingActionButton add;
    Button check;

    FirebaseAuth auth;

    private RecyclerView recyclerView;
    private CalendarAdapter calendarAdapter;
    private ImageButton btnPreviousWeek, btnNextWeek;
    private Calendar currentWeekStart;
    


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        add=findViewById(R.id.add_remainder);
        check=findViewById(R.id.btncheck);


        recyclerView = findViewById(R.id.recyclerViewCalendar);
        btnPreviousWeek = findViewById(R.id.previous);
        btnNextWeek = findViewById(R.id.next);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
        ));

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(Home_Activity.this, LoginActivity.class));
            finish();
        }

        // intent to add medicine
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Home_Activity.this, Add_Medicine_Activity.class);
                startActivity(i);
            }
        });
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Home_Activity.this, Intake_Activity.class);
                startActivity(i);
            }
        });

        currentWeekStart = Calendar.getInstance();
        currentWeekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        // Create and set adapter with initial dates
        calendarAdapter = new CalendarAdapter(generateWeekDates(), this);
        recyclerView.setAdapter(calendarAdapter);

        // Set up navigation button listeners
        setupNavigationListeners();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;


        });
    }

    private List<Calendar> generateWeekDates() {
        List<Calendar> dates = new ArrayList<>();

        // Generate 7 days from the current week start
        for (int i = 0; i < 7; i++) {
            Calendar dayCalendar = (Calendar) currentWeekStart.clone();
            dayCalendar.add(Calendar.DATE, i);
            dates.add(dayCalendar);
        }

        return dates;
    }

    private void setupNavigationListeners() {
        btnPreviousWeek.setOnClickListener(v -> {
            // Move to previous week
            currentWeekStart.add(Calendar.WEEK_OF_YEAR, -1);

            // Update adapter with new dates
            calendarAdapter.updateDates(generateWeekDates());
        });

        btnNextWeek.setOnClickListener(v -> {
            // Move to next week
            currentWeekStart.add(Calendar.WEEK_OF_YEAR, 1);

            // Update adapter with new dates
            calendarAdapter.updateDates(generateWeekDates());
        });
    }


}