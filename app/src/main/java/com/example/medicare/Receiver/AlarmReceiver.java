package com.example.medicare.Receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.medicare.Activity.AlarmStopActivity;
import com.example.medicare.R;

public class AlarmReceiver extends BroadcastReceiver {
    private static MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Extract medicine details from intent
        String medicineName = intent.getStringExtra("MEDICINE_NAME");
        String medicineDose = intent.getStringExtra("MEDICINE_DOSE");
        String medicineTime = intent.getStringExtra("MEDICINE_TIME");

        // Create notification channel for Android Oreo and above
        createNotificationChannel(context);

        // Create an intent to stop the alarm
        Intent stopIntent = new Intent(context, AlarmStopActivity.class);
        stopIntent.putExtra("MEDICINE_NAME", medicineName);
        stopIntent.putExtra("MEDICINE_DOSE", medicineDose);
        stopIntent.putExtra("MEDICINE_TIME", medicineTime);

        PendingIntent pendingStopIntent = PendingIntent.getActivity(
                context,
                0,
                stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Create and show notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "MEDICINE_CHANNEL")
                .setSmallIcon(R.drawable.medicine_add)
                .setContentTitle("Medicine Reminder")
                .setContentText(medicineName + " - " + medicineDose)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingStopIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());

        // Play alarm sound
        playAlarmSound(context);
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Medicine Alarm Channel";
            String description = "Channel for Medicine Alarms";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("MEDICINE_CHANNEL", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void playAlarmSound(Context context) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(context, R.raw.sound);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    public static void stopAlarmSound() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}