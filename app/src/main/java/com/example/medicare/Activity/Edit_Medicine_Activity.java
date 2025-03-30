package com.example.medicare.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Edit_Medicine_Activity extends AppCompatActivity {

    private EditText editMedicineName, editMedicineDose, editMedicineQuantity;
    private Spinner daySpinner;
    private TimePicker timePicker;
    private Button updateButton, deleteButton;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String medicineId;
    private String currentDay;

    private static final String TAG = "EditMedicineActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_medicine);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        editMedicineName = findViewById(R.id.edit_medicine_name);
        editMedicineDose = findViewById(R.id.edit_medicine_dose);
        editMedicineQuantity = findViewById(R.id.edit_medicine_quantity);
        daySpinner = findViewById(R.id.day_spinner);
        timePicker = findViewById(R.id.time_picker);
        updateButton = findViewById(R.id.update_button);
        deleteButton = findViewById(R.id.delete_button);

        // Set 24-hour view for the TimePicker
        timePicker.setIs24HourView(true);

        // Get medicineId from intent
        medicineId = getIntent().getStringExtra("medicineId");
        if (medicineId == null) {
            Toast.makeText(this, "Medicine ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up day spinner
        setupDaySpinner();

        // Fetch and display the medicine data
        fetchMedicineData();

        // Set up update button
        updateButton.setOnClickListener(v -> updateMedicine());

        // Set up delete button
        deleteButton.setOnClickListener(v -> deleteMedicine());
    }

    private void setupDaySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.days_of_week,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(adapter);

        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentDay = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nothing to do
            }
        });
    }

    private void fetchMedicineData() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userId = user.getUid();

        // Since we need to first find which day this medicine belongs to
        String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        for (String day : daysOfWeek) {
            DocumentReference medicineRef = db.collection("Users")
                    .document(userId)
                    .collection("Medicines")
                    .document(day)
                    .collection("Details")
                    .document(medicineId);

            medicineRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Found the medicine in this day
                        Map<String, Object> medicineData = document.getData();
                        displayMedicineData(medicineData, day);
                    }
                }
            });
        }
    }

    private void displayMedicineData(Map<String, Object> medicineData, String day) {
        // Set the day spinner
        ArrayAdapter adapter = (ArrayAdapter) daySpinner.getAdapter();
        int position = adapter.getPosition(day);
        daySpinner.setSelection(position);
        currentDay = day;

        // Display medicine data in UI
        String medicineName = medicineData.get("Medicine Name") != null ?
                medicineData.get("Medicine Name").toString() : "";
        String medicineDose = medicineData.get("Medicine Dose") != null ?
                medicineData.get("Medicine Dose").toString() : "";
        String medicineQuantity = medicineData.get("Medicine quantity") != null ?
                medicineData.get("Medicine quantity").toString() : "";
        String medicineTime = medicineData.get("Time") != null ?
                medicineData.get("Time").toString() : "";

        // Set the EditText values
        editMedicineName.setText(medicineName);
        editMedicineDose.setText(medicineDose);
        editMedicineQuantity.setText(medicineQuantity);

        // Set the TimePicker
        if (!medicineTime.isEmpty()) {
            try {
                String[] timeParts = medicineTime.split(":");
                int hour = Integer.parseInt(timeParts[0]);
                int minute = Integer.parseInt(timeParts[1]);

                // Use the appropriate method based on API level
                timePicker.setHour(hour);
                timePicker.setMinute(minute);
            } catch (Exception e) {
                Log.e(TAG, "Error parsing time: " + e.getMessage());
            }
        }
    }

    private void updateMedicine() {
        String medicineName = editMedicineName.getText().toString().trim();
        String medicineDose = editMedicineDose.getText().toString().trim();
        String medicineQuantity = editMedicineQuantity.getText().toString().trim();

        // Validate input
        if (medicineName.isEmpty() || medicineDose.isEmpty() || medicineQuantity.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the time from TimePicker
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        // Format time
        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);

        // Get current user
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        String selectedDay = currentDay;

        // Check if the day selection has changed
        String originalDay = daySpinner.getSelectedItem().toString();
        boolean dayChanged = !originalDay.equals(selectedDay);

        // Create updated medicine data
        Map<String, Object> updatedMedicine = new HashMap<>();
        updatedMedicine.put("Medicine Name", medicineName);
        updatedMedicine.put("Medicine Dose", medicineDose);
        updatedMedicine.put("Medicine quantity", medicineQuantity);
        updatedMedicine.put("Time", formattedTime);

        if (dayChanged) {
            // If day changed, we need to delete from old day and add to new day

            // First, delete from old day
            db.collection("Users")
                    .document(userId)
                    .collection("Medicines")
                    .document(currentDay)
                    .collection("Details")
                    .document(medicineId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // Then add to new day (we'll create a new document ID)
                        db.collection("Users")
                                .document(userId)
                                .collection("Medicines")
                                .document(originalDay)
                                .collection("Details")
                                .add(updatedMedicine)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(Edit_Medicine_Activity.this,
                                            "Medicine updated successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(Edit_Medicine_Activity.this,
                                            "Error updating medicine: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Edit_Medicine_Activity.this,
                                "Error deleting old medicine: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // If day remains same, just update the document
            db.collection("Users")
                    .document(userId)
                    .collection("Medicines")
                    .document(currentDay)
                    .collection("Details")
                    .document(medicineId)
                    .update(updatedMedicine)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(Edit_Medicine_Activity.this,
                                "Medicine updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Edit_Medicine_Activity.this,
                                "Error updating medicine: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void deleteMedicine() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();

        // Delete the medicine document
        db.collection("Users")
                .document(userId)
                .collection("Medicines")
                .document(currentDay)
                .collection("Details")
                .document(medicineId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Edit_Medicine_Activity.this,
                            "Medicine deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Edit_Medicine_Activity.this,
                            "Error deleting medicine: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}