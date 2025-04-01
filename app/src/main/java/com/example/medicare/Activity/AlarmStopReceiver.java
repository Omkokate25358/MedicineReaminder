package com.example.medicare.Activity;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.medicare.AlarmHelper;

public class AlarmStopReceiver extends BroadcastReceiver {
    @Override

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String medicineName = intent.getStringExtra("medicineName");

        Log.d("AlarmStopReceiver", "Received action: " + action + " for medicine: " + medicineName);

        // Stop the alarm sound and vibration
        AlarmReceiver.stopAlarm();

        // Cancel the notification
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(medicineName.hashCode());

        // Send broadcast to update the gauge
        Intent updateIntent = new Intent("UPDATE_GAUGE_VIEW");
        updateIntent.putExtra("medicineName", medicineName);
        context.sendBroadcast(updateIntent);

        Log.d("AlarmStopReceiver", "Sent UPDATE_GAUGE_VIEW broadcast for: " + medicineName);
    }
}