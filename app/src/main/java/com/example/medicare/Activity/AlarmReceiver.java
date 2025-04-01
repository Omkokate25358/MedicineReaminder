package com.example.medicare.Activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.medicare.R;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "MedicineAlarmChannel";
    private static MediaPlayer mediaPlayer;
    private static Vibrator vibrator;

    @Override
    public void onReceive(Context context, Intent intent) {
        String medicineName = intent.getStringExtra("medicine_name");
        Log.d("AlarmReceiver", "Alarm triggered for: " + medicineName);

        // Create notification channel for Android 8.0+
        createNotificationChannel(context);

        // Create notification with action buttons
        showNotification(context, medicineName);

        // Start alarm sound and vibration
        playAlarmSound(context);
        startVibration(context);
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Medicine Reminders",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel for medicine reminders");

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

//    private void showNotification(Context context, String medicineName) {
//        // Intent for when user taps "Taken" button
//        Intent takenIntent = new Intent(context, AlarmStopReceiver.class);
//        takenIntent.setAction("MEDICINE_TAKEN");
//        takenIntent.putExtra("medicineName", medicineName);
//        PendingIntent takenPendingIntent = PendingIntent.getBroadcast(
//                context,
//                0,
//                takenIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//
//        // Create notification
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_notification) // Make sure you have this icon
//                .setContentTitle("Medicine Reminder")
//                .setContentText("Time to take " + medicineName)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setAutoCancel(true)
//                .addAction(R.drawable.ic_danger, "Taken", takenPendingIntent); // Make sure you have this icon
//
//        // Show notification
//        NotificationManager notificationManager =
//                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(1, builder.build());
//    }

    private void showNotification(Context context, String medicineName) {
        // Create unique request code based on medicine name
        int requestCode = medicineName.hashCode();

        // Intent for when user taps "Taken" button
        Intent takenIntent = new Intent(context, AlarmStopReceiver.class);
        takenIntent.setAction("MEDICINE_TAKEN");
        takenIntent.putExtra("medicineName", medicineName);

        // Use FLAG_IMMUTABLE or FLAG_MUTABLE depending on your Android version requirements
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent takenPendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                takenIntent,
                flags);

        // Create notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Make sure you have this icon
                .setContentTitle("Medicine Reminder")
                .setContentText("Time to take " + medicineName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true)
                .addAction(android.R.drawable.ic_menu_edit, "Taken", takenPendingIntent);

        // Show notification
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Use unique notification ID based on medicine name to avoid overriding notifications
        notificationManager.notify(requestCode, builder.build());
    }
    private void playAlarmSound(Context context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.sound); // Make sure you have this sound file
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }

    private void startVibration(Context context) {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                        VibrationEffect.createWaveform(new long[]{0, 500, 500}, 0)
                );
            } else {
                vibrator.vibrate(new long[]{0, 500, 500}, 0);
            }
        }
    }

    public static void stopAlarm() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (vibrator != null) {
            vibrator.cancel();
            vibrator = null;
        }
    }
}