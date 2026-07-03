/*
 * VoidTerm - AboutActivity
 * Shows developer/build info. (This file was previously empty, which by
 * itself would fail compilation since the manifest references the class.)
 *
 * Developer : Asotn | https://github.com/Asotn | s.pi@outlook.sa
 * License   : GPL-3.0
 */
package com.asotn.voidterm.ui;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.asotn.voidterm.BuildConfig;
import com.asotn.voidterm.R;
import com.asotn.voidterm.utils.EnvironmentManager;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView body = findViewById(R.id.text_about_body);
        body.setText(
                "Kali Linux Terminal for Android\n\n" +
                "Developer  : Asotn\n" +
                "GitHub     : https://github.com/Asotn\n" +
                "Email      : s.pi@outlook.sa\n" +
                "License    : GPL-3.0\n" +
                "Version    : " + BuildConfig.VERSION_NAME + "\n\n" +
                "Running on : " + Build.MANUFACTURER + " " + Build.MODEL + "\n" +
                "Android    : " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")\n" +
                "ABI        : " + EnvironmentManager.getPrimaryAbi() + "\n" +
                "Kali arch  : " + EnvironmentManager.getKaliArch() + "\n\n" +
                "A real Kali Linux terminal for Android.\n" +
                "Powered by proot + bash + apt."
        );
    }
}
