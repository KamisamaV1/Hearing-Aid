package com.example.voicecraft;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private RadioGroup radioGroupNoiseSuppression;
    private RadioButton radioButtonOn, radioButtonOff;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        radioGroupNoiseSuppression = view.findViewById(R.id.radioGroupNoiseSuppression);
        radioButtonOn = view.findViewById(R.id.radioButtonOn);
        radioButtonOff = view.findViewById(R.id.radioButtonOff);

        return view;
    }
}