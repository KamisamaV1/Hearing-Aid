package com.example.voicecraft;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SharedPreferences sharedPreferences;

    //private AppDatabase appDatabase;
    //private User userList = getSharedPreferences();
    //SharedPreferences sharedPreferences = getSharedPreferences("user_shared_preferences", MODE_PRIVATE);
   // private ArrayList<User>userList = new ArrayList<>();
   // private ArrayList<Integer>hearingTestDateList = new ArrayList<>();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        if (getApplicationContext() != null) {
            sharedPreferences = getSharedPreferences("user_shared_preferences", MODE_PRIVATE);
        }

        User user = new User();
        user.setUserName(sharedPreferences.getString("user_name", null));

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));
        HistoryRecyclerViewAdapter adapter = new HistoryRecyclerViewAdapter(user); // userList should be the data you want to display
        recyclerView.setAdapter(adapter);


    }
}