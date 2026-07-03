/*
 * VoidTerm - PackageInstallActivity
 * Transparent activity used for two things:
 *  1. The "./files -0 & permission" storage-permission trick from the README
 *  2. Handling APK install intents triggered from the terminal (sideloading)
 * It shows no UI of its own — it opens the relevant system dialog and
 * immediately finishes.
 *
 * Developer : Asotn | https://github.com/Asotn | s.pi@outlook.sa
 * License   : GPL-3.0
 */
package com.asotn.voidterm.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PackageInstallActivity extends AppCompatActivity {

    private static final String TAG = "VoidTerm-PkgInstall";
    private static final int REQUEST_STORAGE = 6001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestStoragePermission();
    }

    private void requestStoragePermission() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Android 11+: MANAGE_EXTERNAL_STORAGE needs the special settings screen
                if (!Environment_isManageAllFilesGranted()) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        REQUEST_STORAGE);
            }
        } catch (Exception e) {
            Log.w(TAG, "requestStoragePermission failed: " + e.getMessage());
        } finally {
            finish();
        }
    }

    private boolean Environment_isManageAllFilesGranted() {
        try {
            return android.os.Environment.isExternalStorageManager();
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        finish();
    }
}
