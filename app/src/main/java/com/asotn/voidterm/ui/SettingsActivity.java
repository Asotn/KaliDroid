/*
 * VoidTerm - SettingsActivity
 * Simple settings screen: Kali mirror URL, terminal font size, keep-screen-on.
 *
 * Developer : Asotn | https://github.com/Asotn | s.pi@outlook.sa
 * License   : GPL-3.0
 */
package com.asotn.voidterm.ui;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.asotn.voidterm.R;
import com.asotn.voidterm.utils.AppPreferences;

public class SettingsActivity extends AppCompatActivity {

    private EditText mirrorField;
    private SeekBar  fontSizeSeek;
    private CheckBox keepScreenOnCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mirrorField       = findViewById(R.id.edit_mirror);
        fontSizeSeek      = findViewById(R.id.seek_font_size);
        keepScreenOnCheck = findViewById(R.id.check_keep_screen_on);

        AppPreferences prefs = AppPreferences.get(this);
        mirrorField.setText(prefs.getKaliMirror());
        fontSizeSeek.setProgress(prefs.getFontSize());
        keepScreenOnCheck.setChecked(prefs.getKeepScreenOn());

        findViewById(R.id.btn_save).setOnClickListener(v -> {
            String mirror = mirrorField.getText().toString().trim();
            if (!mirror.isEmpty()) {
                prefs.setKaliMirror(mirror);
            }
            prefs.setFontSize(Math.max(10, fontSizeSeek.getProgress()));
            prefs.setKeepScreenOn(keepScreenOnCheck.isChecked());
            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
