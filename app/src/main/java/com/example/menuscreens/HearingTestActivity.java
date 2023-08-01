package com.example.menuscreens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HearingTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layered);
        Button stopTest = findViewById(R.id.stopTest);
        stopTest.setOnClickListener(v -> {
            Intent intent = new Intent(this, TabActivity.class);
            startActivity(intent);
        });
    }
}