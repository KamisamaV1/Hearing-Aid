package com.example.menuscreens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

public class PrivacyPolicyActivity extends AppCompatActivity {
    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_policy);
        backButton = findViewById(R.id.back_imageBtn);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(PrivacyPolicyActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}