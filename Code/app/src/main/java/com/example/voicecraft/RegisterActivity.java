package com.example.voicecraft;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText fName, eMail, uName, pWord, aDS;
    Spinner gD;
    Button registerButton;
    AppDatabase appDatabase;
    TextView privacyPolicy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fName = findViewById(R.id.fullName);
        uName = findViewById(R.id.userName);
        eMail = findViewById(R.id.Email);
        pWord = findViewById(R.id.passWord);
        gD = findViewById(R.id.Gender);
        aDS = findViewById(R.id.Address);
        registerButton = findViewById(R.id.registerButton);

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
        setupGenderSpinner();
        setupRegisterButton();
        privacyPolicy = findViewById(R.id.privacyPolicy);
        privacyPolicy.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, PrivacyPolicyActivity.class);
            startActivity(intent);
        });

    }

    private void setupGenderSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.genders, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gD.setAdapter(adapter);

        gD.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    ((TextView) view).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                } else {
                    ((TextView) view).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.prussian_blue));
                }
                ((TextView) view).setTextSize(20);
                ((TextView) view).setTypeface(Typeface.DEFAULT_BOLD);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ((TextView) parent.getSelectedView()).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.prussian_blue));
                ((TextView) parent.getSelectedView()).setTextSize(20);
                ((TextView) parent.getSelectedView()).setTypeface(Typeface.DEFAULT_BOLD);
            }
        });
    }

    private void setupRegisterButton() {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = new User();
                user.setFullName(fName.getText().toString());
                user.setEmail(eMail.getText().toString().trim());
                user.setPassWord(pWord.getText().toString().trim());
                user.setUserName(uName.getText().toString().trim());
                user.setAddress(aDS.getText().toString());
                user.setGender(gD.getSelectedItem().toString());

                if (validateUser(user)) {
                    UserDao userDao = appDatabase.userDao();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            userDao.registerUser(user);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RegisterActivity.this, "User Registered Successfully!", Toast.LENGTH_SHORT).show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent loginRedirect = new Intent(RegisterActivity.this, LoginActivity.class);
                                            startActivity(loginRedirect);
                                        }
                                    }, 100);
                                }
                            });
                        }
                    }).start();
                } else {
                    Toast.makeText(RegisterActivity.this, "Enter all Details!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //Code For User Already Exists...
    private Boolean validateUser(User user) {
        if (user.getFullName().isEmpty() || user.getEmail().isEmpty() || user.getPassWord().isEmpty() || user.getUserName().isEmpty() || user.getAddress().isEmpty() || user.getGender().isEmpty()) {
            return false;
        }
        return true;
    }
}
