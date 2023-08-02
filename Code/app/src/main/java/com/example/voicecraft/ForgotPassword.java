package com.example.voicecraft;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class ForgotPassword extends AppCompatActivity {
    EditText userName, newPassword;
    Button resetButton;
    AppDatabase appDatabase;
    private UserDao userDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        userName = findViewById(R.id.enterUser);
        newPassword = findViewById(R.id.passReset);
        resetButton = findViewById(R.id.changeButton);
        userDao = AppDatabase.getAppDatabase(this).userDao();
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
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = userName.getText().toString().trim();
                final String newPasswordText = newPassword.getText().toString().trim();
                if (username.isEmpty() || newPasswordText.isEmpty()) {
                    Toast.makeText(ForgotPassword.this, "Enter both Credentials!", Toast.LENGTH_SHORT).show();
                } else {
                    AppDatabase appDatabase = AppDatabase.getAppDatabase(getApplicationContext());
                    final UserDao userDao = appDatabase.userDao();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                User user = userDao.getUser(username);
                                if (user == null) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ForgotPassword.this, "User Not Found", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    userDao.updatePassword(username, newPasswordText);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ForgotPassword.this, "Password Updated Successfully!", Toast.LENGTH_SHORT).show();
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Intent loginRedirect = new Intent(ForgotPassword.this, LoginActivity.class);
                                                    startActivity(loginRedirect);
                                                }
                                            }, 1000);
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ForgotPassword.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).start();
                }
            }
        });
    }
}