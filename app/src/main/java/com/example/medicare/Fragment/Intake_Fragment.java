package com.example.medicare.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicare.Activity.Add_Medicine_Activity;
import com.example.medicare.Activity.CalendarAdapter;
import com.example.medicare.Activity.Edit_Medicine_Activity;
import com.example.medicare.Activity.Medicine;
import com.example.medicare.Activity.MedicineAdapter;
import com.example.medicare.Activity.RoundedThickGaugeView;
import com.example.medicare.AlarmHelper;
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

public class Intake_Fragment extends Fragment {
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

    int totalMedicine;


    // to add a medicine
    FloatingActionButton add;

    RoundedThickGaugeView gaugeView;

    public interface OnDateSelectedListener {
        void onDateSelected(Calendar date, int position);
    }

    public Intake_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intake, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        CrecyclerView = view.findViewById(R.id.recyclerViewCalendar);
        mrecyclerView = view.findViewById(R.id.recyclerView);
        btnPreviousWeek = view.findViewById(R.id.previous);
        btnNextWeek = view.findViewById(R.id.next);
        add = view.findViewById(R.id.add_data);
        gaugeView=view.findViewById(R.id.roundedThickGaugeView);

        // Set up calendar RecyclerView
        CrecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        ));

        // Set up medicine RecyclerView
        mrecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize medicine list and adapter
        medicineList = new ArrayList<>();
        adapter = new MedicineAdapter(getContext(), medicineList, medicine -> {
            // Handle Edit Button Click
            Intent intent = new Intent(getActivity(), Edit_Medicine_Activity.class);
            intent.putExtra("medicineId", medicine.getId());
            startActivity(intent);
        });

        // Set adapter to RecyclerView
        mrecyclerView.setAdapter(adapter);

        // Initialize current week start
        currentWeekStart = Calendar.getInstance();
        currentWeekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        // Initialize calendar adapter
        calendarAdapter = new CalendarAdapter(generateWeekDates(), getContext(), (date, position) -> {
            // Get the day of week as a string (e.g., "Monday")
            String[] daysOfWeek = new String[]{"", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
            selectedDay = daysOfWeek[date.get(Calendar.DAY_OF_WEEK)];

            // Show which day is selected (optional)
            Toast.makeText(getContext(), "Selected: " + selectedDay, Toast.LENGTH_SHORT).show();


            // Fetch medicine data for the selected day
            fetchMedicineData(selectedDay);

            // here i am changing a guage view values
            totalMedicine=countRecyclerViewItems();
            gaugeView.updateGauge(1,totalMedicine,selectedDay,"Today Medicine");
        });

        // Set calendar adapter
        CrecyclerView.setAdapter(calendarAdapter);

        // Set up navigation button listeners
        setupNavigationListeners();

        // Add medicine button click listener
        add.setOnClickListener(v -> {
            Intent i1 = new Intent(getActivity(), Add_Medicine_Activity.class);
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
            Toast.makeText(getContext(), "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        Log.d("MedicareApp", "User ID: " + userId);

        DocumentReference userRef = db.collection("Users").document(userId);
        CollectionReference detailsRef = userRef
                .collection("Medicines")
                .document(selectedDay)
                .collection("Details");

//        detailsRef.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                Log.d("MedicareApp", "Query successful. Document count: " + task.getResult().size());
//                medicineList.clear();
//
//                if (task.getResult().isEmpty()) {
//                    Log.d("MedicareApp", "No medicines found for " + selectedDay);
//                    Toast.makeText(getContext(), "No medicines found for " + selectedDay, Toast.LENGTH_SHORT).show();
//                    adapter.notifyDataSetChanged();
//                    updateGaugeView(0);
//                    return;
//                }
//
//                for (QueryDocumentSnapshot document : task.getResult()) {
//                    Map<String, Object> medicineData = document.getData();
//
//                    // Extract data safely
//                    String medicineName = medicineData.get("Medicine Name") != null ? medicineData.get("Medicine Name").toString() : "Unknown";
//                    String medicineDose = medicineData.get("Medicine Dose") != null ? medicineData.get("Medicine Dose").toString() : "N/A";
//                    String medicineQuantity = medicineData.get("Medicine quantity") != null ? medicineData.get("Medicine quantity").toString() : "0";
//                    String medicineTime = medicineData.get("Time") != null ? medicineData.get("Time").toString() : "N/A";
//
//                    Medicine medicine = new Medicine(
//                            document.getId(),  // Auto-generated document ID
//                            medicineName,
//                            medicineDose,
//                            medicineQuantity,
//                            medicineTime
//                    );
//
//                    medicineList.add(medicine);
//                }
//
//                Log.d("MedicareApp", "Loaded " + medicineList.size() + " medicines");
//                adapter.notifyDataSetChanged();
//
//                // Update gauge view after data is loaded
//                updateGaugeView(medicineList.size());
//
//            } else {
//                Log.e("MedicareApp", "Error getting documents: ", task.getException());
//                Toast.makeText(getContext(), "Error loading medicines: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                updateGaugeView(0);
//            }
//        });
        detailsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                medicineList.clear();

                if (task.getResult().isEmpty()) {
                    Toast.makeText(getContext(), "No medicines found for " + selectedDay, Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    updateGaugeView(0);
                    return;
                }

                int requestCode = 0; // Unique request code for each alarm

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> medicineData = document.getData();

                    // Extract data safely
                    String medicineName = medicineData.get("Medicine Name") != null ? medicineData.get("Medicine Name").toString() : "Unknown";
                    String medicineDose = medicineData.get("Medicine Dose") != null ? medicineData.get("Medicine Dose").toString() : "N/A";
                    String medicineQuantity = medicineData.get("Medicine quantity") != null ? medicineData.get("Medicine quantity").toString() : "0";
                    String medicineTime = medicineData.get("Time") != null ? medicineData.get("Time").toString() : "N/A";

                    Medicine medicine = new Medicine(
                            document.getId(),
                            medicineName,
                            medicineDose,
                            medicineQuantity,
                            medicineTime
                    );

                    medicineList.add(medicine);

                    // Parse medicineTime (Assuming format "HH:mm")
                    String[] timeParts = medicineTime.split(":");
                    if (timeParts.length == 2) {
                        try {
                            int hour = Integer.parseInt(timeParts[0]);
                            int minute = Integer.parseInt(timeParts[1]);

                            // Schedule the alarm
                            AlarmHelper.setAlarm(getContext(), hour, minute, medicineName, requestCode);
                            requestCode++; // Ensure unique request codes for each medicine
                        } catch (NumberFormatException e) {
                            Log.e("Intake_Fragment", "Invalid time format: " + medicineTime, e);
                        }
                    }
                }

                adapter.notifyDataSetChanged();
                updateGaugeView(medicineList.size());

            } else {
                Toast.makeText(getContext(), "Error loading medicines: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                updateGaugeView(0);
            }
        });


    }


    private void updateGaugeView(int totalMedicines) {
        if (gaugeView != null) {
            gaugeView.updateGauge(1, totalMedicines, selectedDay, "Today's Medicine");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when returning to this fragment
        if (selectedDay != null) {
            fetchMedicineData(selectedDay);
        }
    }

    private int countRecyclerViewItems() {
        if (mrecyclerView != null && mrecyclerView.getAdapter() != null) {
            int total=mrecyclerView.getAdapter().getItemCount();
            Toast.makeText(getContext(), "Count "+total, Toast.LENGTH_SHORT).show();
            return total;
        }
        return 0;
    }
}
