package com.example.voicecraft;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SharedPreferences sharedPreferences;

    List<String> dateList = new ArrayList<>();
    String userName;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        if (getApplicationContext() != null) {
            sharedPreferences = getSharedPreferences("user_shared_preferences", MODE_PRIVATE);
        }
        userName = sharedPreferences.getString("loggedInUserName", "");


        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Perform your time-consuming task here
                getData();
                // Update the UI on the main thread after the task is done
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Update UI components here
                    }
                });
            }
        }).start();
        Log.d(TAG,dateList+ "fgh");
        HistoryRecyclerViewAdapter adapter = new HistoryRecyclerViewAdapter(dateList, userName);
        recyclerView.setAdapter(adapter);
    }
    void getData(){
        AppDatabase database = AppDatabase.getAppDatabase(getApplicationContext());
        List<String> dateStrings = database.calibrationDao().getUniqueDates();

        // Create a SimpleDateFormat instance to parse date strings
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");



        // Iterate through the date strings and parse them into Date objects
        for (String dateString : dateStrings) {
            try {
                Date date = dateFormat.parse(dateString);
                dateList.add(String.valueOf(date));
            } catch (ParseException e) {
                e.printStackTrace(); // Handle parsing exceptions if necessary
            }
        }

        // Now, dateList contains Date objects representing your unique dates
    }

}