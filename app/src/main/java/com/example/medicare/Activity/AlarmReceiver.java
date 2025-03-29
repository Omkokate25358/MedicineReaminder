package com.example.medicare.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    private MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("TRIGGER_ALARM".equals(intent.getAction())) {
            playAlarmSound(context);
        }
    }

    private void playAlarmSound(Context context) {
        try {
            // Stop any existing media player
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }

            // Get default alarm sound
            mediaPlayer = MediaPlayer.create(
                    context,
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            );

            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error playing alarm sound", e);
        }
    }
}