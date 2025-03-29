package com.example.medicare.Receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.medicare.Activity.Home_Activity;
import com.example.medicare.Fragment.Intake_Fragment;
import com.example.medicare.R;

public class MedicineAlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "MedicineReminderChannel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Extract medicine details from intent
        String medicineName = intent.getStringExtra("MEDICINE_NAME");
        String dosage = intent.getStringExtra("MEDICINE_DOSAGE");

        // Create an intent to open medicine details
        Intent detailIntent = new Intent(context, Home_Activity.class);
        detailIntent.putExtra("MEDICINE_NAME", medicineName);
        detailIntent.putExtra("MEDICINE_DOSAGE", dosage);
        detailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                detailIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Create notification
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Medicine Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for medicine intake");
            notificationManager.createNotificationChannel(channel);
        }

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_medicine)
                .setContentTitle("Medicine Reminder")
                .setContentText("Time to take " + medicineName + " - " + dosage)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Show notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}