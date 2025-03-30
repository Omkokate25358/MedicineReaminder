package com.example.medicare.Activity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Add_Medicine_Activity extends AppCompatActivity {

    private TextView selectedTimeText;
    EditText mediName,medidose,mquantity;
    private CheckBox chkSunday, chkMonday, chkTuesday, chkWednesday, chkThursday, chkFriday, chkSaturday;


    private Button timePickerButton, editTimeButton;

    private Button btn_add;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    private FirebaseUser user;


    // data members
    String selectedItem;
    String formattedTime;

    FirebaseFirestore db;


    // for autocomplete the text
    private AutoCompleteTextView medicineTypeInput;

    private androidx.appcompat.widget.SwitchCompat alarmToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_medicine);

        // All firebase operation

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser(); // Get the logged-in user
        db = FirebaseFirestore.getInstance();

        if (user == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        //data member

        timePickerButton = findViewById(R.id.set_time);
        selectedTimeText = findViewById(R.id.input_reaminder_result);
//        editTimeButton = findViewById(R.id.edit_time);

        //data member week days
        chkSunday = findViewById(R.id.input_day_sunday);
        chkMonday = findViewById(R.id.input_day_monday);
        chkTuesday = findViewById(R.id.input_day_tuesday);
        chkWednesday = findViewById(R.id.input_day_wednesday);
        chkThursday = findViewById(R.id.input_day_thursday);
        chkFriday = findViewById(R.id.input_day_friday);
        chkSaturday = findViewById(R.id.input_day_saturday);
        btn_add = findViewById(R.id.btn_add_medicine);

        // feild data
        mediName=findViewById(R.id.input_name);
        medidose=findViewById(R.id.input_dose);
        mquantity=findViewById(R.id.input_quantity);

        medicineTypeInput = findViewById(R.id.input_type);

        alarmToggle = findViewById(R.id.alarm_toggle);

        // auto complete the words
        // Get medicine types from database or predefined list
        String[] medicineTypes = getMedicineTypes();

        // Create adapter for autocomplete
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                medicineTypes
        );
        // Set the adapter to the AutoCompleteTextView
        medicineTypeInput.setAdapter(adapter);

        // Optional: Set threshold for showing suggestions (after typing X characters)
        medicineTypeInput.setThreshold(1);

        // Time picker
        timePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        // Edit time when clicking the edit button
//        editTimeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showTimePicker();
//            }
//        });

        // add button
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSelectedDays();
            }


        });



    }

    private void  showTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        boolean isPM = calendar.get(Calendar.AM_PM) == Calendar.PM;

        // TimePicker Dialog in 12-hour format
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Convert 24-hour format to 12-hour format with AM/PM
                        boolean isPM = hourOfDay >= 12;
                        int hour12Format = (hourOfDay == 0 || hourOfDay == 12) ? 12 : hourOfDay % 12;
                        String amPm = isPM ? "PM" : "AM";
                        formattedTime = String.format("%02d:%02d %s", hour12Format, minute, amPm);

                        // Display selected time
                        selectedTimeText.setText("Time: " + formattedTime);
                        selectedTimeText.setVisibility(View.VISIBLE);

                        // Hide clock button and show edit button
                        timePickerButton.setVisibility(View.GONE);
//                        editTimeButton.setVisibility(View.VISIBLE);
                    }
                }, hour, minute, false); // 'false' for 12-hour format

        timePickerDialog.show();

    }

    private void saveSelectedDays() {

        ArrayList<String> selectedDays = new ArrayList<>();
        if (!chkMonday.isChecked() && !chkTuesday.isChecked() && !chkWednesday.isChecked() &&
                !chkThursday.isChecked() && !chkFriday.isChecked() && !chkSaturday.isChecked() &&
                !chkSunday.isChecked()) {
            Toast.makeText(this, "Please select at least one weekday.", Toast.LENGTH_SHORT).show();
            return;
        }




        String name=mediName.getText().toString().trim();
        String dose=medidose.getText().toString().trim();
        String quantity=mquantity.getText().toString().trim();
        String type=medicineTypeInput.getText().toString();
        String time=formattedTime.trim();

        if (user != null) {

            // Medicine Data
            Map<String, Object> medicine = new HashMap<>();
//            medicine.put("selectedDays", selectedDays);
            medicine.put("Medicine Name", name);
            medicine.put("Medicine Dose", dose);
            medicine.put("Medicine quantity", quantity);
            medicine.put("Medicine type", type);
            medicine.put("Time", time);
            medicine.put("isAlarm", alarmToggle.isChecked());

            // getting feild from firebase
            String userId = user.getUid();


//            add data to monday

            if(chkMonday.isChecked()) addDataToFirebase("Monday",medicine,userId);
            if(chkTuesday.isChecked()) addDataToFirebase("Tuesday",medicine,userId);
            if(chkWednesday.isChecked()) addDataToFirebase("Wednesday",medicine,userId);
            if(chkThursday.isChecked()) addDataToFirebase("Thursday",medicine,userId);
            if(chkFriday.isChecked()) addDataToFirebase("Friday",medicine,userId);
            if(chkSaturday.isChecked()) addDataToFirebase("Saturday",medicine,userId);
            if(chkSunday.isChecked()) addDataToFirebase("Sunday",medicine,userId);

}
    }

    private void addDataToFirebase(String weekDay,Map<String,Object> medicineData,String userId){
        db.collection("Users").document(userId) // Replace 'userId' with the actual user ID
                .collection("Medicines")
                .document(weekDay)  // Adding data specifically under "Monday"
                .collection("Details")  // Optional, if you want detailed entries
                .add(medicineData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Medicine added for Monday!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String[] getMedicineTypes() {
        // You can either get this from a database or use a predefined list
        return new String[] {
                "Tablet", "Capsule", "Syrup", "Injection", "Drops",
                "Inhaler", "Cream", "Ointment", "Spray", "Patch",
                "Suppository", "Suspension", "Solution", "Powder", "Lozenge"
        };
    }

}