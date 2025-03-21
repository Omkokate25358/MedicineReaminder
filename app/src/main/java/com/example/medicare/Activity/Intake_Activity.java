package com.example.medicare.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicare.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class Intake_Activity extends AppCompatActivity {
    private RecyclerView CrecyclerView, mrecyclerView;
    private CalendarAdapter calendarAdapter;
    private ImageButton btnPreviousWeek, btnNextWeek;
    private Calendar currentWeekStart;
    private String selectedDay; // To store the selected day

    // For medicine adapter
    private ArrayList<Medicine> medicineList;
    private MedicineAdapter adapter;

    FirebaseAuth auth;
    private FirebaseFirestore db;

    // to add a medicine
    FloatingActionButton add;

    public interface OnDateSelectedListener {
        void onDateSelected(Calendar date, int position);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intake);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        CrecyclerView = findViewById(R.id.recyclerViewCalendar);
        mrecyclerView = findViewById(R.id.recyclerView);
        btnPreviousWeek = findViewById(R.id.previous);
        btnNextWeek = findViewById(R.id.next);
        add = findViewById(R.id.add_data);

        // Set up calendar RecyclerView
        CrecyclerView.setLayoutManager(new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
        ));

        // Set up medicine RecyclerView
        mrecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize medicine list and adapter
        medicineList = new ArrayList<>();
        adapter = new MedicineAdapter(this, medicineList, medicine -> {
            // Handle Edit Button Click
            Intent intent = new Intent(Intake_Activity.this, Add_Medicine_Activity.class);
            intent.putExtra("medicineId", medicine.getId());
            startActivity(intent);
        });

        // Set adapter to RecyclerView
        mrecyclerView.setAdapter(adapter);

        // Initialize current week start
        currentWeekStart = Calendar.getInstance();
        currentWeekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        // Initialize calendar adapter
        calendarAdapter = new CalendarAdapter(generateWeekDates(), this, (date, position) -> {
            // Get the day of week as a string (e.g., "Monday")
            String[] daysOfWeek = new String[]{"", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
            selectedDay = daysOfWeek[date.get(Calendar.DAY_OF_WEEK)];

            // Show which day is selected (optional)
            Toast.makeText(Intake_Activity.this, "Selected: " + selectedDay, Toast.LENGTH_SHORT).show();

            // Fetch medicine data for the selected day
            fetchMedicineData(selectedDay);
        });

        // Set calendar adapter
        CrecyclerView.setAdapter(calendarAdapter);

        // Set up navigation button listeners
        setupNavigationListeners();

        // Add medicine button click listener
        add.setOnClickListener(v -> {
            Intent i1 = new Intent(Intake_Activity.this, Add_Medicine_Activity.class);
            startActivity(i1);
        });

        // Set default selected day (today) and fetch data
        Calendar today = Calendar.getInstance();
        String[] daysOfWeek = new String[]{"", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        selectedDay = daysOfWeek[today.get(Calendar.DAY_OF_WEEK)];

        // Log the default selected day
        Log.d("MedicareApp", "Default selected day: " + selectedDay);

        // Fetch medicine data for the default selected day
        fetchMedicineData(selectedDay);
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

    private void fetchMedicineData(String selectedDay) {
        Log.d("MedicareApp", "Fetching medicine data for: " + selectedDay);

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        Log.d("MedicareApp", "User ID: " + userId);

        // Add logging for path
        String path = "Users/" + userId + "/Medicines/" + selectedDay + "/Details";
        Log.d("MedicareApp", "Firestore path: " + path);

        DocumentReference userRef = db.collection("Users").document(userId);
        CollectionReference detailsRef = userRef
                .collection("Medicines")
                .document(selectedDay)
                .collection("Details");

        detailsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("MedicareApp", "Query successful. Document count: " + task.getResult().size());
                medicineList.clear();

                if (task.getResult().isEmpty()) {
                    Log.d("MedicareApp", "No medicines found for " + selectedDay);
                    Toast.makeText(Intake_Activity.this, "No medicines found for " + selectedDay, Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    return;
                }

                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Add document ID logging
                    Log.d("MedicareApp", "Processing document: " + document.getId());

                    // Access medicine data inside the auto-generated document
                    Map<String, Object> medicineData = document.getData();

                    // Log the full data
                    Log.d("MedicareApp", "Document data: " + medicineData.toString());

                    // Extract data safely
                    String medicineName = medicineData.get("Medicine Name") != null ? medicineData.get("Medicine Name").toString() : "Unknown";
                    String medicineDose = medicineData.get("Medicine Dose") != null ? medicineData.get("Medicine Dose").toString() : "N/A";
                    String medicineQuantity = medicineData.get("Medicine quantity") != null ? medicineData.get("Medicine quantity").toString() : "0";
                    String medicineTime = medicineData.get("Time") != null ? medicineData.get("Time").toString() : "N/A";

                    Medicine medicine = new Medicine(
                            document.getId(),  // Auto-generated document ID
                            medicineName,
                            medicineDose,
                            medicineQuantity,
                            medicineTime
                    );

                    medicineList.add(medicine);
                }

                Log.d("MedicareApp", "Loaded " + medicineList.size() + " medicines");
                adapter.notifyDataSetChanged();

            } else {
                Log.e("MedicareApp", "Error getting documents: ", task.getException());
                Toast.makeText(Intake_Activity.this, "Error loading medicines: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this activity
        if (selectedDay != null) {
            fetchMedicineData(selectedDay);
        }
    }
}