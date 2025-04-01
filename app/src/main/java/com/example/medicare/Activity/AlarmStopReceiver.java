package com.example.medicare.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.medicare.AlarmHelper;

public class AlarmStopReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Extract medicine name and update UI
        String medicineName = intent.getStringExtra("medicineName");
        Intent updateIntent = new Intent("UPDATE_GAUGE_VIEW");
        updateIntent.putExtra("medicineName", medicineName);
        context.sendBroadcast(updateIntent);

        AlarmHelper.stopAlarm(context, medicineName);
//        Intent updateIntent = new Intent("UPDATE_GAUGE_VIEW");
//        updateIntent.putExtra("medicineName", medicineName);
//        context.sendBroadcast(updateIntent);
    }
}
