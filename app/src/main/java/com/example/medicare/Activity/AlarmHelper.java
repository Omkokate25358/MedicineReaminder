package com.example.medicare.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;

public class AlarmHelper {
    private static final String TAG = "AlarmHelper";
    private final Context context;
    private final AlarmManager alarmManager;

    public AlarmHelper(Context context) {
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void setExactAlarm(int hour, int minute) {
        // Create an intent for the AlarmReceiver
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction("TRIGGER_ALARM");

        // Create a pending intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Prepare calendar for alarm time
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Adjust time if it's already passed
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Set alarm based on Android version
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
            }

            Log.d(TAG, "Alarm set for: " + calendar.getTime());
        } catch (SecurityException e) {
            Log.e(TAG, "Permission denied for setting alarm", e);
        }
    }

    public void cancelAlarm() {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction("TRIGGER_ALARM");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Cancel the alarm
        alarmManager.cancel(pendingIntent);
        Log.d(TAG, "Alarm cancelled");
    }
}
