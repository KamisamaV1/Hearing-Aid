package com.example.voicecraft;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class LoginActivity extends AppCompatActivity {
    TextView signupRedirect, forgotPassword;
    Button loginButton;
    EditText uName, pWord;
    private String username;
    AppDatabase appDatabase;

    public String getUsername() {
        return username;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (isLoggedIn()) {
            // Automatically navigate to the main activity
            startActivity(new Intent(this, MainActivity.class));
            finish(); // Close the login activity
        }

        signupRedirect = findViewById(R.id.signupText);
        loginButton = findViewById(R.id.loginButton);
        signupRedirect.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        loginButton = findViewById(R.id.loginButton);
        forgotPassword = findViewById(R.id.forgotPassword);
        uName = findViewById(R.id.userName);
        pWord = findViewById(R.id.passWord);
        username = uName.getText().toString().trim();
        forgotPassword.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
            startActivity(intent);
        });
        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "AppDB")
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@androidx.annotation.NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                    }
                    @Override
                    public void onOpen(@androidx.annotation.NonNull SupportSQLiteDatabase db) {
                        super.onOpen(db);
                    }
                })
                .build();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userName = uName.getText().toString().trim();
                final String passWord = pWord.getText().toString().trim();
                if (userName.isEmpty() || passWord.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Enter both Credentials!", Toast.LENGTH_SHORT).show();
                } else {
                    AppDatabase appDatabase = AppDatabase.getAppDatabase(getApplicationContext());
                    final UserDao userDao = appDatabase.userDao();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                User user = userDao.loginUser(userName, passWord);
                                if (user == null) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(LoginActivity.this, "Invalid Credentials!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("fragmentToLoad", "homeFragment");
                                    startActivity(intent);
                                    SharedPreferences sharedPref = getSharedPreferences("user_shared_preferences", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString("loggedInUserName", userName);
                                    editor.apply();
                                    finish();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this, "Error occurred during login!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).start();
                }
            }
        });
    }

    private boolean isLoggedIn() {
        // Check if the user's authentication token or session data exists in SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("user_shared_preferences", Context.MODE_PRIVATE);
        return sharedPref.contains("loggedInUserName");
    }
}

