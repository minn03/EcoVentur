package com.example.ecoventur.ui.greenspace;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;

public class notificationReceiver extends BroadcastReceiver {
    @SuppressLint("MissingPermission") // permission is checked in notificationScheduler
    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra("notificationId", 0);
        Notification notification = intent.getParcelableExtra("notification");
        if (notification != null) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(notificationId, notification);
        }
    }
}
