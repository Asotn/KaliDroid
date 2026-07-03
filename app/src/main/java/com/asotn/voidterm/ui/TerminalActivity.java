/*
 * VoidTerm - TerminalActivity
 * The main (launcher) screen. Hosts the terminal output view and the
 * command input field, wires them to TerminalSession + CommandProcessor.
 *
 * Developer : Asotn | https://github.com/Asotn | s.pi@outlook.sa
 * License   : GPL-3.0
 */
package com.asotn.voidterm.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.asotn.voidterm.R;
import com.asotn.voidterm.terminal.CommandProcessor;
import com.asotn.voidterm.terminal.TerminalSession;

public class TerminalActivity extends AppCompatActivity {

    private static final int REQUEST_NOTIFICATIONS = 5001;

    private TextView    outputView;
    private ScrollView  scrollView;
    private EditText    inputView;
    private TerminalSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);

        outputView = findViewById(R.id.text_output);
        scrollView = findViewById(R.id.scroll_output);
        inputView  = findViewById(R.id.edit_input);
        ImageButton sendBtn = findViewById(R.id.btn_send);

        requestNotificationPermissionIfNeeded();

        session = new TerminalSession(this);
        session.setOutputCallback(this::appendOutput);

        appendOutput("VoidTerm - Kali Linux Terminal for Android\r\n" +
                "Type voidterm-help for usage.\r\n\r\n");

        session.start();

        sendBtn.setOnClickListener(v -> submitInput());
        inputView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                submitInput();
                return true;
            }
            return false;
        });
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_NOTIFICATIONS);
        }
    }

    private void submitInput() {
        String text = inputView.getText().toString();
        inputView.setText("");
        if (text.isEmpty()) return;

        appendOutput("$ " + text + "\r\n");

        CommandProcessor.CommandResult result = CommandProcessor.process(text, this);
        switch (result.type) {
            case CLEAR:
                outputView.setText("");
                break;
            case EXIT:
                finish();
                break;
            case SETTINGS:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case HELP:
            case ABOUT:
                appendOutput(result.output);
                break;
            case FILE_PERMISSION:
                startActivity(new Intent(this, PackageInstallActivity.class));
                break;
            case PASSTHROUGH:
            default:
                if (result.passthroughCmd != null) {
                    session.sendInput(result.passthroughCmd + "\n");
                }
                break;
        }
    }

    private void appendOutput(String text) {
        if (text == null) return;
        runOnUiThread(() -> {
            outputView.append(text);
            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.terminal_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_ctrl_c) {
            session.sendSignalInterrupt();
            return true;
        } else if (id == R.id.action_clear) {
            outputView.setText("");
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (session != null) session.stop();
    }
}
