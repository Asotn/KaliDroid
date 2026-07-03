/*
 * VoidTerm - Application Class
 * Developer : Asotn | https://github.com/Asotn | s.pi@outlook.sa
 * License   : GPL-3.0
 */
package com.asotn.voidterm;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import com.asotn.voidterm.utils.EnvironmentManager;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VoidTermApp extends Application {

    private static final String TAG = "VoidTerm-App";

    public static final String CHANNEL_ID_DOWNLOAD  = "voidterm_download";
    public static final String CHANNEL_ID_BOOTSTRAP = "voidterm_bootstrap";
    public static final String CHANNEL_ID_SESSION   = "voidterm_session";

    private static VoidTermApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        installCrashHandler();

        // Wrap everything — the Application must never crash
        try {
            createNotificationChannels();
        } catch (Throwable t) {
            Log.e(TAG, "Notification channel setup failed: " + t.getMessage());
        }

        try {
            EnvironmentManager.init(this);
        } catch (Throwable t) {
            Log.e(TAG, "EnvironmentManager init failed: " + t.getMessage());
        }
    }

    /**
     * Writes any uncaught exception to a plain text file under the app's
     * external files dir. No permissions required (scoped storage).
     * Readable from Termux at:
     *   ~/storage/shared/Android/data/com.asotn.voidterm/files/crash_log.txt
     */
    private void installCrashHandler() {
        final Thread.UncaughtExceptionHandler defaultHandler =
                Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            try {
                File dir = getExternalFilesDir(null);
                if (dir == null) dir = getFilesDir();
                File logFile = new File(dir, "crash_log.txt");

                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                pw.println("=== VoidTerm crash: " +
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()) + " ===");
                pw.println("Device: " + Build.MANUFACTURER + " " + Build.MODEL);
                pw.println("Android: " + Build.VERSION.RELEASE + " (SDK " + Build.VERSION.SDK_INT + ")");
                pw.println("ABI: " + String.join(",", Build.SUPPORTED_ABIS));
                pw.println("Thread: " + thread.getName());
                pw.println();
                throwable.printStackTrace(pw);
                pw.println();

                try (FileWriter fw = new FileWriter(logFile, true)) {
                    fw.write(sw.toString());
                }
            } catch (Throwable ignored) {
                // never let the crash handler itself crash
            }

            if (defaultHandler != null) {
                defaultHandler.uncaughtException(thread, throwable);
            } else {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });
    }

    public static VoidTermApp getInstance() { return instance; }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        NotificationManager nm = getSystemService(NotificationManager.class);
        if (nm == null) return;

        NotificationChannel dl = new NotificationChannel(
            CHANNEL_ID_DOWNLOAD, "Package Downloads", NotificationManager.IMPORTANCE_LOW);
        dl.setDescription("Download progress for Kali packages");
        dl.setShowBadge(false);
        nm.createNotificationChannel(dl);

        NotificationChannel bs = new NotificationChannel(
            CHANNEL_ID_BOOTSTRAP, "Bootstrap Setup", NotificationManager.IMPORTANCE_DEFAULT);
        bs.setDescription("First-time Kali environment setup");
        nm.createNotificationChannel(bs);

        NotificationChannel sess = new NotificationChannel(
            CHANNEL_ID_SESSION, "Terminal Session", NotificationManager.IMPORTANCE_MIN);
        sess.setDescription("Active terminal session");
        sess.setShowBadge(false);
        nm.createNotificationChannel(sess);
    }
}
