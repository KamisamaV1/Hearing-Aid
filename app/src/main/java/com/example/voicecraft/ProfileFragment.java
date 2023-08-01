package com.example.voicecraft;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class ProfileFragment extends Fragment {
    private AppDatabase appDatabase;
    private Button logoutButton;
    private String username;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        logoutButton = rootView.findViewById(R.id.logout_Button);

        SharedPreferences sharedPref = requireActivity().getSharedPreferences("user_shared_preferences", requireContext().MODE_PRIVATE);
        username = sharedPref.getString("loggedInUserName", "");

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });
        appDatabase = Room.databaseBuilder(requireContext(), AppDatabase.class, "AppDB")
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                    }
                    @Override
                    public void onOpen(@NonNull SupportSQLiteDatabase db) {
                        super.onOpen(db);
                    }
                })
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                User user = appDatabase.userDao().getUser(username);
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (user != null) {
                            TextView fullNameTextView = rootView.findViewById(R.id.textview_Name);
                            fullNameTextView.setText(user.getFullName());

                            TextView emailTextView = rootView.findViewById(R.id.textview_Email);
                            emailTextView.setText(user.getEmail());

                            TextView usernameTextView = rootView.findViewById(R.id.textview_User);
                            usernameTextView.setText(user.getUserName());

                            TextView genderTextView = rootView.findViewById(R.id.textview_Gender);
                            genderTextView.setText(user.getGender());

                            TextView addressTextView = rootView.findViewById(R.id.textview_Address);
                            addressTextView.setText(user.getAddress());
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog);
                            builder.setTitle(Html.fromHtml("<font color='#1C48D8'>Not Found!</font>"));
                            builder.setMessage(Html.fromHtml("<font color='#000000'>Sorry, the user details are not available.</font>"));
                            builder.setPositiveButton(Html.fromHtml("<font color='#1C48D8'>OK</font>"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // You can add any action you want when the user clicks "OK"
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    }
                });
            }
        }).start();

        return rootView;
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog);
        builder.setTitle(Html.fromHtml("<font color='#1C48D8'>Confirm Log Out</font>"));
        builder.setMessage("Are you sure you want to Log Out?");
        builder.setPositiveButton(Html.fromHtml("<font color='#1C48D8'>Yes</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logoutUser();
            }
        });
        builder.setNegativeButton(Html.fromHtml("<font color='#1C48D8'>No</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void logoutUser() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
