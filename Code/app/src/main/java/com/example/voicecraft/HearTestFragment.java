package com.example.voicecraft;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class HearTestFragment extends Fragment {

    public HearTestFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("user_shared_preferences", requireContext().MODE_PRIVATE);
        String userName = sharedPref.getString("loggedInUserName", "");
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("UserName", userName);
        editor.apply();
        return inflater.inflate(R.layout.fragment_hear_test, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button startButton = view.findViewById(R.id.start_test_button);
        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), InstructionsPage.class);
            startActivity(intent);

        });
    }
}
