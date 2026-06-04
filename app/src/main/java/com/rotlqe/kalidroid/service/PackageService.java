/*
 * KaliDroid - PackageService
 * Foreground service that handles apt downloads in the background.
 * Shows download progress in the notification bar even when app is minimized.
 *
 * Developer : Rotlqe | https://github.com/Rotlqe | s.pi@outlook.sa
 * License   : GPL-3.0
 */

package com.rotlqe.kalidroid.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.rotlqe.kalidroid.KaliDroidApp;
import com.rotlqe.kalidroid.R;
import com.rotlqe.kalidroid.ui.TerminalActivity;

public class PackageService extends Service {

    private static final String TAG     = "KaliDroid-PkgSvc";
    private static final int    NOTIF_ID = 2001;

    public static final String ACTION_START    = "com.rotlqe.kalidroid.PKG_START";
    public static final String ACTION_STOP     = "com.rotlqe.kalidroid.PKG_STOP";
    public static final String ACTION_PROGRESS = "com.rotlqe.kalidroid.PKG_PROGRESS";

    public static final String EXTRA_PACKAGE  = "package_name";
    public static final String EXTRA_PROGRESS = "progress";
    public static final String EXTRA_STATUS   = "status";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_NOT_STICKY;

        String action = intent.getAction();
        if (action == null) return START_NOT_STICKY;

        switch (action) {
            case ACTION_START:
                String pkgName = intent.getStringExtra(EXTRA_PACKAGE);
                startForeground(NOTIF_ID, buildNotification(pkgName, 0));
                Log.i(TAG, "Package service started for: " + pkgName);
                break;

            case ACTION_PROGRESS:
                int progress = intent.getIntExtra(EXTRA_PROGRESS, 0);
                String pkg   = intent.getStringExtra(EXTRA_PACKAGE);
                updateNotification(pkg, progress);
                break;

            case ACTION_STOP:
                stopForeground(true);
                stopSelf();
                Log.i(TAG, "Package service stopped");
                break;
        }

        return START_NOT_STICKY;
    }

    private void updateNotification(String packageName, int progress) {
        Notification notif = buildNotification(packageName, progress);
        androidx.core.app.NotificationManagerCompat.from(this)
            .notify(NOTIF_ID, notif);
    }

    private Notification buildNotification(String packageName, int progress) {
        Intent intent = new Intent(this, TerminalActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String title = (packageName != null && !packageName.isEmpty())
            ? "Installing: " + packageName
            : "Package operation in progress";

        NotificationCompat.Builder builder =
            new NotificationCompat.Builder(this, KaliDroidApp.CHANNEL_ID_DOWNLOAD)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("KaliDroid")
                .setContentText(title)
                .setOnlyAlertOnce(true)
                .setContentIntent(pi)
                .setOngoing(progress < 100);

        if (progress > 0) {
            builder.setProgress(100, progress, false);
            builder.setSubText(progress + "%");
        } else {
            builder.setProgress(0, 0, true); // indeterminate
        }

        return builder.build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
