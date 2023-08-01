package com.example.menuscreens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    TextView signupRedirect;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        signupRedirect = findViewById(R.id.signupText);
        loginButton = findViewById(R.id.loginButton);
        signupRedirect.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        loginButton.setOnClickListener(view ->{
            Intent intent = new Intent(LoginActivity.this, TabActivity.class);
            startActivity(intent);
        });

    }
}
