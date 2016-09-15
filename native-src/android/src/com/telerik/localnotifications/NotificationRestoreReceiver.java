/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  android.app.AlarmManager
 *  android.app.Notification
 *  android.app.NotificationManager
 *  android.app.PendingIntent
 *  android.content.BroadcastReceiver
 *  android.content.Context
 *  android.content.Intent
 *  android.content.SharedPreferences
 *  android.net.Uri
 *  android.os.Parcelable
 *  android.support.v4.app.NotificationCompat
 *  android.support.v4.app.NotificationCompat$Builder
 *  android.util.Log
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.telerik.localnotifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationRestoreReceiver
extends BroadcastReceiver {
    static final String TAG = "NotificationRestoreReceiver";

    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LocalNotificationsPlugin", 0);
        Set<? extends Map.Entry<?,?>> notificationOptions = sharedPreferences.getAll().entrySet();

        for (Map.Entry item : notificationOptions) {
            String notificationId = (String)item.getKey();
            String notificationString = (String)item.getValue();
            try {
                JSONObject options = new JSONObject(notificationString);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                        .setDefaults(0)
                        .setContentTitle((CharSequence)options.optString("title"))
                        .setContentText((CharSequence)options.optString("body"))
                        .setSmallIcon(options.optInt("icon")).setAutoCancel(true)
                        .setSound(options.has("sound") ? Uri.parse((String)("android.resource://" + context.getPackageName() + "/raw/" + options.optString("sound"))) : Uri.parse((String)("android.resource://" + context.getPackageName() + "/raw/notify")) )
                        .setNumber(options.optInt("badge"))
                        .setTicker((CharSequence)options.optString("ticker"));

                //Toast.makeText(context, "Custom sound name: " + options.optString("sound") , Toast.LENGTH_LONG).show();

                Intent clickIntent = new Intent(context, (Class)NotificationClickedActivity.class).putExtra("pushBundle", notificationString).setFlags(1073741824);
                PendingIntent pendingContentIntent = PendingIntent.getActivity((Context)context, (int)(options.optInt("id") + 100), (Intent)clickIntent, (int)134217728);
                builder.setContentIntent(pendingContentIntent);
                Notification notification = builder.build();
                Intent notificationIntent = new Intent(context, (Class)NotificationPublisher.class).setAction(options.getString("id"))
                        .putExtra(NotificationPublisher.NOTIFICATION_ID, options.optInt("id"))
                        .putExtra(NotificationPublisher.SOUND, options.optString("sound"))
                        .putExtra(NotificationPublisher.NOTIFICATION, (Parcelable)notification);
                PendingIntent pendingIntent = PendingIntent.getBroadcast((Context)context, (int)(options.optInt("id") + 200), (Intent)notificationIntent, (int)268435456);
                long triggerTime = options.getLong("atTime");
                Date triggerDate = new Date(triggerTime);
                boolean wasInThePast = new Date().after(triggerDate);
                AlarmManager alarmManager = (AlarmManager)context.getSystemService("alarm");
                if (wasInThePast) {
                    Log.d((String)"NotificationRestoreReceiver", (String)("------------------------ cancel notification: " + (Object)options));
                    alarmManager.cancel(pendingIntent);
                    ((NotificationManager)context.getSystemService("notification")).cancel(options.getInt("id"));
                    continue;
                }
                Log.d((String)"NotificationRestoreReceiver", (String)("------------------------ schedule notification: " + (Object)options));
                alarmManager.set(0, triggerTime, pendingIntent);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

