package com.example.medicare.Fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicare.Activity.AlarmAdapter;
import com.example.medicare.Activity.Home_Activity;
import com.example.medicare.Activity.Medicine;
import com.example.medicare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlarmFragment extends Fragment {
    private RecyclerView recyclerView;
    private AlarmAdapter adapter;
    private List<Medicine> medicineList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private Button profile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alarm, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewAlarms);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        profile=view.findViewById(R.id.profile);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getContext(), Home_Activity.class);
                startActivity(i);
            }
        });

        // Initialize medicine list and adapter
        medicineList = new ArrayList<>();
        adapter = new AlarmAdapter(getContext(), medicineList);
        recyclerView.setAdapter(adapter);




        // Initialize add button

        // Fetch medicine data
        fetchMedicineData();
    }

    private void fetchMedicineData() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        medicineList.clear();

        // Fetch medicines for each day
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        int[] completedRequests = {0};
        
        for (String day : days) {
            db.collection("Users")
                    .document(userId)
                    .collection("Medicines")
                    .document(day)
                    .collection("Details")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Map<String, Object> medicineData = document.getData();
                            
                            String medicineName = medicineData.get("Medicine Name") != null ? 
                                    medicineData.get("Medicine Name").toString() : "Unknown";
                            String medicineDose = medicineData.get("Medicine Dose") != null ? 
                                    medicineData.get("Medicine Dose").toString() : "N/A";
                            String medicineQuantity = medicineData.get("Medicine quantity") != null ? 
                                    medicineData.get("Medicine quantity").toString() : "0";
                            String medicineTime = medicineData.get("Time") != null ? 
                                    medicineData.get("Time").toString() : "N/A";

                            Medicine medicine = new Medicine(
                                    document.getId(),
                                    medicineName,
                                    medicineDose,
                                    medicineQuantity,
                                    medicineTime
                            );
                            medicine.setMedicineDay(day);
                            medicineList.add(medicine);
                        }
                        
                        completedRequests[0]++;
                        if (completedRequests[0] == days.length) {
                            // All days have been processed, update the adapter
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error loading medicines: " + e.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                        completedRequests[0]++;
                        if (completedRequests[0] == days.length) {
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchMedicineData();
    }
}
