package com.example.medicare.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicare.R;
import com.example.medicare.Receiver.AlarmReceiver;

public class AlarmStopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_stop);

        // Get medicine details from intent
        String medicineName = getIntent().getStringExtra("MEDICINE_NAME");
        String medicineDose = getIntent().getStringExtra("MEDICINE_DOSE");
        String medicineTime = getIntent().getStringExtra("MEDICINE_TIME");

        // Set up UI elements
        TextView tvMedicineName = findViewById(R.id.tvMedicineName);
        TextView tvMedicineDose = findViewById(R.id.tvMedicineDose);
        TextView tvMedicineTime = findViewById(R.id.tvMedicineTime);
        Button btnStopAlarm = findViewById(R.id.btnStopAlarm);

        // Set medicine details
        tvMedicineName.setText("Medicine: " + medicineName);
        tvMedicineDose.setText("Dose: " + medicineDose);
        tvMedicineTime.setText("Time: " + medicineTime);

        // Stop alarm when button is clicked
        btnStopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmReceiver.stopAlarmSound();
                finish();
            }
        });
    }
}