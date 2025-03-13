package com.example.medicare.Activity;

import android.app.TimePickerDialog;
import android.os.Bundle;
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


    private ImageButton timePickerButton, editTimeButton;

    private Button btn_add;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    private FirebaseUser user;


    // data members
    String selectedItem;
    String formattedTime;

    FirebaseFirestore db;




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
        Spinner spinner = findViewById(R.id.input_type);
        timePickerButton = findViewById(R.id.set_time);
        selectedTimeText = findViewById(R.id.input_reaminder_result);
        editTimeButton = findViewById(R.id.edit_time);

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



        String[] type_options = getResources().getStringArray(R.array.type_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, type_options);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 selectedItem = parent.getItemAtPosition(position).toString();
                Toast.makeText(Add_Medicine_Activity.this, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });


        // Time picker
        timePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        // Edit time when clicking the edit button
        editTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        // add button
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSelectedDays();
            }


        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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
                        editTimeButton.setVisibility(View.VISIBLE);
                    }
                }, hour, minute, false); // 'false' for 12-hour format

        timePickerDialog.show();

    }

    private void saveSelectedDays() {

        ArrayList<String> selectedDays = new ArrayList<>();

        if (chkSunday.isChecked()) selectedDays.add("Sunday");
        if (chkMonday.isChecked()) selectedDays.add("Monday");
        if (chkTuesday.isChecked()) selectedDays.add("Tuesday");
        if (chkWednesday.isChecked()) selectedDays.add("Wednesday");
        if (chkThursday.isChecked()) selectedDays.add("Thursday");
        if (chkFriday.isChecked()) selectedDays.add("Friday");
        if (chkSaturday.isChecked()) selectedDays.add("Saturday");




        String name=mediName.getText().toString().trim();
        String dose=medidose.getText().toString().trim();
        String quantity=mquantity.getText().toString().trim();
        String type=selectedItem.trim();
        String time=formattedTime.trim();

        if (user != null) {
            String userId = user.getUid();
            // Reference to User's Medicine Collection
            DocumentReference userRef = db.collection("Users").document(userId);
            CollectionReference medicinesRef = userRef.collection("Medicines");

            // Medicine Data
            Map<String, Object> medicine = new HashMap<>();
            medicine.put("selectedDays", selectedDays);
            medicine.put("Medicine Name", name);
            medicine.put("Medicine Dose", dose);
            medicine.put("Medicine quantity", quantity);
            medicine.put("Medicine type", type);
            medicine.put("Time", time);


            // Add Medicine Data to Firestore
            medicinesRef.add(medicine)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Medicine Added!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        }



    }

}