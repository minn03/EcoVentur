package com.example.ecoventur.ui.greenspace;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.ecoventur.MainActivity;
import com.example.ecoventur.R;

public class notificationScheduler {
    private static final String CHANNEL_ID = "Green Space Discovery";
    private static final String CHANNEL_NAME = "Green Event Reminders";
    private static final int CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_DEFAULT;
    private static final String CHANNEL_DESCRIPTION = "Reminders for upcoming green events";

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, CHANNEL_IMPORTANCE);
            channel.setDescription(CHANNEL_DESCRIPTION);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public static void createNotification(Context context, String textTitle, String textContent, int notificationId) {
        // tester: immediately send notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)// insert app icon here
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(textContent))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setContentIntent() // on notification tap
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (notificationManager.areNotificationsEnabled()) {
            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                createNotificationChannel(context);
            }
            try {
                notificationManager.notify(notificationId, builder.build());
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(context, "Notifications are disabled, please enable them to receive reminders.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            try {
                pendingIntent.send();
                if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                    createNotificationChannel(context);
                }
                if (notificationManager.areNotificationsEnabled()) {
                    try {
                        notificationManager.notify(notificationId, builder.build());
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(context, "You will not be notified for upcoming events. To enable reminder, enable notifications and save this event to wishlist again.", Toast.LENGTH_LONG).show();
                }
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }
    public static void cancelNotification(Context context, int notificationId) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(notificationId);
    }
    public static void scheduleNotification(Context context, String textTitle, String textContent, int notificationId, long scheduledTime) {
        Intent intentOnTap = new Intent(context, MainActivity.class);
        intentOnTap.putExtra("openFragment", "greenSpaceDiscovery");
        intentOnTap.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntentOnTap = PendingIntent.getActivity(context, 0, intentOnTap, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)// insert app icon here
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(textContent))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntentOnTap)
                .setAutoCancel(true);

        Intent notificationIntent = new Intent(context, notificationReceiver.class);
        notificationIntent.putExtra("notificationId", notificationId);
        notificationIntent.putExtra("notification", builder.build());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (notificationManager.areNotificationsEnabled()) {
            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                createNotificationChannel(context);
            }
            if (alarmManager != null && scheduledTime > System.currentTimeMillis()) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, scheduledTime, pendingIntent);
            }
        }
        else {
            Toast.makeText(context, "Notifications are disabled, please enable them to receive reminders.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            PendingIntent pendingIntentSettings = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            try {
                pendingIntentSettings.send();
                if (notificationManager.areNotificationsEnabled()) {
                    if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                        createNotificationChannel(context);
                    }
                    if (alarmManager != null && scheduledTime > System.currentTimeMillis()) {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, scheduledTime, pendingIntent);
                    }
                }
                else {
                    Toast.makeText(context, "You will not be notified for upcoming events. To enable reminder, enable notifications and save this event to wishlist again.", Toast.LENGTH_LONG).show();
                }
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }
    public static void cancelScheduledNotification(Context context, int notificationId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(context, notificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
