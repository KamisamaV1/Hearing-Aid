package com.example.voicecraft;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SharedPreferences sharedPreferences;

    List<String> date;
    String userName;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        userName = sharedPreferences.getString("loggedInUserName", "");

        if (getApplicationContext() != null) {
            sharedPreferences = getSharedPreferences("user_shared_preferences", MODE_PRIVATE);
        }

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));
        HistoryRecyclerViewAdapter adapter = new HistoryRecyclerViewAdapter(date, userName); // userList should be the data you want to display
        recyclerView.setAdapter(adapter);
    }
    void getData(){
        AppDatabase database = AppDatabase.getAppDatabase(getApplicationContext());
        date = database.calibrationDao().getUniqueDates();
    }

}