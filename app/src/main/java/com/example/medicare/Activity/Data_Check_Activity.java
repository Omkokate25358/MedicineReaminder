package com.example.medicare.Activity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.medicare.R;

// firebase
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Data_Check_Activity extends AppCompatActivity {

    private static final String TAG = "Add_Medicine_Activity";

    private TextView selectedTimeText;
    private EditText mediName, medidose, mquantity;
    private CheckBox chkSunday, chkMonday, chkTuesday, chkWednesday, chkThursday, chkFriday, chkSaturday;
    private ImageButton timePickerButton, editTimeButton;
    private Button btn_add;
    private Spinner spinner;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;

    // data members
    private String selectedItem = "";
    private String formattedTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_medicine);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        // Check if user is logged in
        if (user == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if not logged in
            return;
        }

        // Initialize UI elements
        initializeUIElements();
        setupSpinner();
        setupTimePickerButtons();
        setupAddButton();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeUIElements() {
        // UI elements
        spinner = findViewById(R.id.input_type);
        timePickerButton = findViewById(R.id.set_time);
        selectedTimeText = findViewById(R.id.input_reaminder_result);
        editTimeButton = findViewById(R.id.edit_time);
        btn_add = findViewById(R.id.btn_add_medicine);

        // Input fields
        mediName = findViewById(R.id.input_name);
        medidose = findViewById(R.id.input_dose);
        mquantity = findViewById(R.id.input_quantity);

        // Checkboxes for days
        chkSunday = findViewById(R.id.input_day_sunday);
        chkMonday = findViewById(R.id.input_day_monday);
        chkTuesday = findViewById(R.id.input_day_tuesday);
        chkWednesday = findViewById(R.id.input_day_wednesday);
        chkThursday = findViewById(R.id.input_day_thursday);
        chkFriday = findViewById(R.id.input_day_friday);
        chkSaturday = findViewById(R.id.input_day_saturday);

        // Initialize time text
        selectedTimeText.setText("Time: Not set");
        editTimeButton.setVisibility(View.GONE);
    }

    private void setupSpinner() {
        String[] type_options = getResources().getStringArray(R.array.type_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, type_options);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "Medicine type selected: " + selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupTimePickerButtons() {
        // Time picker
        timePickerButton.setOnClickListener(v -> showTimePicker());

        // Edit time when clicking the edit button
        editTimeButton.setOnClickListener(v -> showTimePicker());
    }

    private void setupAddButton() {
        btn_add.setOnClickListener(v -> {
            if (validateInputs()) {
                saveMedicineData();
            }
        });
    }

    private void showTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        boolean isPM = calendar.get(Calendar.AM_PM) == Calendar.PM;

        // TimePicker Dialog in 12-hour format
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, selectedMinute) -> {
                    // Convert 24-hour format to 12-hour format with AM/PM
                    boolean isPm = hourOfDay >= 12;
                    int hour12Format = (hourOfDay == 0 || hourOfDay == 12) ? 12 : hourOfDay % 12;
                    String amPm = isPm ? "PM" : "AM";
                    formattedTime = String.format("%02d:%02d %s", hour12Format, selectedMinute, amPm);

                    // Display selected time
                    selectedTimeText.setText("Time: " + formattedTime);
                    selectedTimeText.setVisibility(View.VISIBLE);

                    // Hide clock button and show edit button
                    timePickerButton.setVisibility(View.GONE);
                    editTimeButton.setVisibility(View.VISIBLE);

                    Log.d(TAG, "Time set to: " + formattedTime);
                }, hour, minute, false); // 'false' for 12-hour format

        timePickerDialog.show();
    }

    private boolean validateInputs() {
        // Validate medicine name
        String name = mediName.getText().toString().trim();
        if (name.isEmpty()) {
            mediName.setError("Medicine name is required");
            mediName.requestFocus();
            return false;
        }

        // Validate dose
        String dose = medidose.getText().toString().trim();
        if (dose.isEmpty()) {
            medidose.setError("Dose is required");
            medidose.requestFocus();
            return false;
        }

        // Validate quantity
        String quantity = mquantity.getText().toString().trim();
        if (quantity.isEmpty()) {
            mquantity.setError("Quantity is required");
            mquantity.requestFocus();
            return false;
        }

        // Validate time selection
        if (formattedTime == null) {
            Toast.makeText(this, "Please select a reminder time", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate medicine type
        if (selectedItem.isEmpty() || selectedItem.equals("Select Type")) {
            Toast.makeText(this, "Please select a medicine type", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate day selection
        if (!chkSunday.isChecked() && !chkMonday.isChecked() && !chkTuesday.isChecked() &&
                !chkWednesday.isChecked() && !chkThursday.isChecked() && !chkFriday.isChecked() &&
                !chkSaturday.isChecked()) {
            Toast.makeText(this, "Please select at least one day", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveMedicineData() {
        String name = mediName.getText().toString().trim();
        String dose = medidose.getText().toString().trim();
        String quantity = mquantity.getText().toString().trim();
        String type = selectedItem.trim();
        String time = formattedTime.trim();
        String userId = user.getUid();

        // Medicine Data
        Map<String, Object> medicineData = new HashMap<>();
        medicineData.put("Medicine Name", name);
        medicineData.put("Medicine Dose", dose);
        medicineData.put("Medicine Quantity", quantity);
        medicineData.put("Medicine Type", type);
        medicineData.put("Time", time);
        medicineData.put("Timestamp", System.currentTimeMillis());

        // Show loading indicator or disable button
        btn_add.setEnabled(false);
        Toast.makeText(this, "Saving medicine data...", Toast.LENGTH_SHORT).show();

        // Counter to track successful uploads
        final int[] successCount = {0};
        final int[] totalToSave = {0};

        // Check which days are selected and save
        if (chkSunday.isChecked()) {
            totalToSave[0]++;
            addMedicineToFirestore("Sunday", medicineData, userId, successCount, totalToSave);
        }
        if (chkMonday.isChecked()) {
            totalToSave[0]++;
            addMedicineToFirestore("Monday", medicineData, userId, successCount, totalToSave);
        }
        if (chkTuesday.isChecked()) {
            totalToSave[0]++;
            addMedicineToFirestore("Tuesday", medicineData, userId, successCount, totalToSave);
        }
        if (chkWednesday.isChecked()) {
            totalToSave[0]++;
            addMedicineToFirestore("Wednesday", medicineData, userId, successCount, totalToSave);
        }
        if (chkThursday.isChecked()) {
            totalToSave[0]++;
            addMedicineToFirestore("Thursday", medicineData, userId, successCount, totalToSave);
        }
        if (chkFriday.isChecked()) {
            totalToSave[0]++;
            addMedicineToFirestore("Friday", medicineData, userId, successCount, totalToSave);
        }
        if (chkSaturday.isChecked()) {
            totalToSave[0]++;
            addMedicineToFirestore("Saturday", medicineData, userId, successCount, totalToSave);
        }
    }

    private void addMedicineToFirestore(String weekday, Map<String, Object> medicineData, String userId,
                                        final int[] successCount, final int[] totalToSave) {
        Log.d(TAG, "Adding medicine for " + weekday);

        db.collection("Users")
                .document(userId)
                .collection("Medicine")
                .document(weekday)
                .collection("Details")
                .add(medicineData)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Medicine added for " + weekday + " with ID: " + documentReference.getId());
                    successCount[0]++;

                    // Check if all uploads are complete
                    if (successCount[0] == totalToSave[0]) {
                        Toast.makeText(Data_Check_Activity.this,
                                "Medicine added successfully for all selected days!",
                                Toast.LENGTH_SHORT).show();

                        // Re-enable button and reset form
                        btn_add.setEnabled(true);
                        resetForm();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding medicine for " + weekday, e);
                    Toast.makeText(Data_Check_Activity.this,
                            "Failed to add medicine for " + weekday + ": " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    btn_add.setEnabled(true);
                });
    }

    private void resetForm() {
        // Clear all inputs for next entry
        mediName.setText("");
        medidose.setText("");
        mquantity.setText("");
        spinner.setSelection(0);

        // Reset time
        formattedTime = null;
        selectedTimeText.setText("Time: Not set");
        timePickerButton.setVisibility(View.VISIBLE);
        editTimeButton.setVisibility(View.GONE);

        // Uncheck all days
        chkSunday.setChecked(false);
        chkMonday.setChecked(false);
        chkTuesday.setChecked(false);
        chkWednesday.setChecked(false);
        chkThursday.setChecked(false);
        chkFriday.setChecked(false);
        chkSaturday.setChecked(false);

        // Set focus to first field
        mediName.requestFocus();
    }
}