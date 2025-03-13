package com.example.medicare.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Intake_Activity extends AppCompatActivity {
    private RecyclerView CrecyclerView,mrecyclerView;
    private CalendarAdapter calendarAdapter;
    private ImageButton btnPreviousWeek, btnNextWeek;
    private Calendar currentWeekStart;

    // For medicine adapter
    private ArrayList medicineList;
    private MedicineAdapter adapter;

    FirebaseAuth auth;
    private FirebaseFirestore db;


    //to add a medicine
    FloatingActionButton add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intake);

        CrecyclerView = findViewById(R.id.recyclerViewCalendar);

        btnPreviousWeek = findViewById(R.id.previous);
        btnNextWeek = findViewById(R.id.next);

        // Set up RecyclerView
        CrecyclerView.setLayoutManager(new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
        ));

        // recyler view for medicine
        mrecyclerView = findViewById(R.id.recyclerView);
        mrecyclerView.setLayoutManager(new LinearLayoutManager(this));
// Initialize current week start
        currentWeekStart = Calendar.getInstance();
//        currentWeekStart.setMinimalDaysInFirstWeek(7);
        currentWeekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        // Create and set adapter with initial dates
        calendarAdapter = new CalendarAdapter(generateWeekDates(), this);
        CrecyclerView.setAdapter(calendarAdapter);

        // Adapter list medicine
        medicineList = new ArrayList<>();
        adapter = new MedicineAdapter(this, medicineList, medicine -> {
            // Handle Edit Button Click
            Intent intent = new Intent(Intake_Activity.this, Add_Medicine_Activity.class);
            intent.putExtra("medicineId", medicine.getId());
            startActivity(intent);
        });

        mrecyclerView.setAdapter(adapter);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // floating button
        add=findViewById(R.id.add_data);


        add.setOnClickListener(v -> {
            Intent i1=new Intent(Intake_Activity.this,Add_Medicine_Activity.class);
            startActivity(i1);
        });


        // Set up navigation button listeners
        setupNavigationListeners();

        // for week to generate


        fetchMedicineData();




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

    private void fetchMedicineData() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        CollectionReference medicinesRef = db.collection("Users").document(userId).collection("Medicines");

        medicinesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                medicineList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Medicine medicine = new Medicine(
                            document.getId(),
                            document.getString("Medicine Name") != null ? document.getString("Medicine Name") : "Unknown",
                            document.getString("Medicine Dose") != null ? document.getString("Medicine Dose") : "N/A",
                            document.getString("Medicine quantity") != null ? document.getString("Medicine quantity") : "0",
                            document.getString("Time") != null ? document.getString("Time") : "N/A"
                    );
                    medicineList.add(medicine);
                }
                adapter.notifyDataSetChanged();
            } else {
                Log.e("Firestore", "Error getting documents: ", task.getException());
            }
        });
    }
}


