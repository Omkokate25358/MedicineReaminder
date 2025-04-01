package com.example.medicare;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.medicare.Activity.AlarmReceiver;
import com.example.medicare.Activity.AlarmStopReceiver;

import java.util.Calendar;

public class AlarmHelper {

    public static void setAlarm(Context context, int hour, int minute, String medicineName, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("medicine_name", medicineName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Set the time for the alarm
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // Convert to 12-hour format for logging
        int displayHour = (hour > 12) ? (hour - 12) : (hour == 0 ? 12 : hour);
        String amPm = (hour >= 12) ? "PM" : "AM";

        // If the time is in the past, set it for the next day
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        if (alarmManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    Log.e("AlarmHelper", "Exact alarms permission not granted!");
                    return;
                }
            }
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Log.d("AlarmHelper", "Alarm set for " + medicineName + " at " + displayHour + ":"
                    + String.format("%02d", minute) + " " + amPm);
        }
    }

    public static void stopAlarm(Context context, String medicineName) {
        // Stop the alarm logic (if using MediaPlayer, stop it here)

        // Send a broadcast to update the gauge view
        Intent intent = new Intent("UPDATE_GAUGE_VIEW");
        intent.putExtra("medicineName", medicineName);
        context.sendBroadcast(intent);

        // If you still need AlarmStopReceiver for other purposes
        Intent stopIntent = new Intent(context, AlarmStopReceiver.class);
        stopIntent.putExtra("medicineName", medicineName);
        context.sendBroadcast(stopIntent);

    }
}
