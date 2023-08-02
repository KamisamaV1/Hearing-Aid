package com.example.voicecraft;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

public class HearingTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hearingtest);
        Button stopTest = findViewById(R.id.stopTest);
        Button newLeftBtn = findViewById(R.id.newleftBtn);
        Button newRightBtn = findViewById(R.id.newrightBtn);
        ImageView headphoneImg = findViewById(R.id.headphoneImg);
        newLeftBtn.setOnClickListener(v -> {
            newLeftBtn.setBackgroundTintList(AppCompatResources.getColorStateList(newLeftBtn.getContext(), R.color.neon_blue));
            headphoneImg.setImageResource(R.drawable.headphoneleft);
            newRightBtn.setBackgroundTintList(AppCompatResources.getColorStateList(newLeftBtn.getContext(), R.color.gy_pewter));
        });
        newRightBtn.setOnClickListener(v -> {
            newRightBtn.setBackgroundTintList(AppCompatResources.getColorStateList(newLeftBtn.getContext(), R.color.neon_blue));
            headphoneImg.setImageResource(R.drawable.headphoneright);
            newLeftBtn.setBackgroundTintList(AppCompatResources.getColorStateList(newLeftBtn.getContext(), R.color.gy_pewter));
        });
        stopTest.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("fragmentToLoad", "homeFragment");
            startActivity(intent);
        });
    }
}