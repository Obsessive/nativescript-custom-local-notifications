/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  android.app.Notification
 *  android.app.NotificationManager
 *  android.content.BroadcastReceiver
 *  android.content.Context
 *  android.content.Intent
 *  android.net.Uri
 *  android.os.Parcelable
 */
package com.telerik.localnotifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import static android.R.attr.data;

public class NotificationPublisher
extends BroadcastReceiver {
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";
    public static String SOUND = "sound";

    public void onReceive(Context context, Intent intent) {
        Notification notification = (Notification)intent.getParcelableExtra(NOTIFICATION);
        Uri d_sound = Uri.parse((String)("android.resource://" + context.getPackageName() + "/raw/" + notification.sound));

       // Toast.makeText(context,notification.toString() , Toast.LENGTH_LONG).show();
       // Toast.makeText(context,d_sound.toString() , Toast.LENGTH_LONG).show();
         notification.sound = d_sound;
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        ((NotificationManager)context.getSystemService("notification")).notify(id, notification);
    }
}

