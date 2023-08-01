package com.example.menuscreens;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    ImageButton loginbackButton;
    TextView privacypolicy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);
        loginbackButton = findViewById(R.id.loginback_imageBtn);
        loginbackButton.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
        privacypolicy = findViewById(R.id.privacyPolicy);
        privacypolicy.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, PrivacyPolicyActivity.class);
            startActivity(intent);
        });
        Spinner spinner = findViewById(R.id.gender_list);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.genders, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0, true);
        View v = spinner.getSelectedView();
        ((TextView)v).setTextColor(ContextCompat.getColor(this, R.color.prussian_blue));
        ((TextView)v).setTextSize(20);
        ((TextView)v).setTypeface(Typeface.DEFAULT_BOLD);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ((TextView) view).setTextColor(ContextCompat.getColor(this, R.color.prussian_blue));
        ((TextView) view).setTextSize(20);
        ((TextView) view).setTypeface(Typeface.DEFAULT_BOLD);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
