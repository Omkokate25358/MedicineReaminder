package com.example.medicare.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.medicare.AlarmHelper;
import com.example.medicare.R;

public class AlarmActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        // Stop Button
        Button stopButton = findViewById(R.id.stopAlarmButton);
        stopButton.setOnClickListener(v -> {
            AlarmReceiver.stopAlarm();

            AlarmHelper.stopAlarm(this, "medicineName");

            finish(); // Close activity
            // Send broadcast to update gauge view
            Intent stopGaugeIntent = new Intent("UPDATE_GAUGE");
            sendBroadcast(stopGaugeIntent);
        });
    }
}
