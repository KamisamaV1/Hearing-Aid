package com.example.voicecraft;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

public class HearingTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hearingtest);
        ImageView headphoneImage = findViewById(R.id.headphoneImg);
        Button stopTest = findViewById(R.id.stopTest);
        RadioGroup radioSelectEar=findViewById(R.id.radioGroupEar);
        radioSelectEar.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.leftRadioButton) {
                headphoneImage.setImageResource(R.drawable.headphoneleft);
            } else if (checkedId == R.id.rightRadioButton) {
                headphoneImage.setImageResource(R.drawable.headphoneright);
            }
        });
        stopTest.setOnClickListener(v -> {
            Intent intent = new Intent(HearingTestActivity.this, MainActivity.class);
            intent.putExtra("fragmentToLoad", "homeFragment");
            startActivity(intent);
            finish();
        });
    }
}