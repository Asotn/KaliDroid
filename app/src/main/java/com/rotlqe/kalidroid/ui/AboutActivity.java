/*
 * KaliDroid - AboutActivity
 * Developer : Rotlqe | https://github.com/Rotlqe | s.pi@outlook.sa
 * License   : GPL-3.0
 */

package com.rotlqe.kalidroid.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.rotlqe.kalidroid.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("About KaliDroid");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Button btnGithub = findViewById(R.id.btn_github);
        if (btnGithub != null) {
            btnGithub.setOnClickListener(v -> {
                startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/Rotlqe")));
            });
        }

        Button btnEmail = findViewById(R.id.btn_email);
        if (btnEmail != null) {
            btnEmail.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:s.pi@outlook.sa"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "KaliDroid Feedback");
                startActivity(Intent.createChooser(intent, "Send Email"));
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
