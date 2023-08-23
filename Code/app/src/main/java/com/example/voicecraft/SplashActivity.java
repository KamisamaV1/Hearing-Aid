package com.example.voicecraft;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(() -> {
            Intent iHome = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(iHome);
            finish();
        },1500);

    }
}
