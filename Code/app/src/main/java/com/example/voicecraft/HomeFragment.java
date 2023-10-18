package com.example.voicecraft;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {
    private RadioGroup radioGroupNoiseSuppression;
    private RadioButton radioButtonOn, radioButtonOff;
    private Button startButton, stopButton;
    private SeekBar gainBar;
    private boolean isNoiseReductionOn = false;
    private boolean isRecording = false;
    private AudioRecord audioRecord;
    private AudioTrack audioTrack;
    private NoiseSuppression ns = new NoiseSuppression();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final String TAG = "Home Fragment";
    float volume = 0;

    private AudioManager audioManager;
    private int maxVolume;
    int newVolume;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        radioGroupNoiseSuppression = view.findViewById(R.id.radioGroupNoiseSuppression);
        radioButtonOn = view.findViewById(R.id.radioButtonOn);
        radioButtonOff = view.findViewById(R.id.radioButtonOff);
        startButton = view.findViewById(R.id.buttonStart);
        stopButton = view.findViewById(R.id.buttonStop);
        gainBar = view.findViewById(R.id.seekBarGain);

        audioManager = (AudioManager) requireActivity().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            Log.d(TAG, "onCreateView: maxVolume "+maxVolume);
        } else {
            // Handle the case where audioManager is null
            Log.e(TAG, "AudioManager is null.");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            gainBar.setProgress(gainBar.getMax()/2);
        }

        // Set click listeners
        setClickListeners();
        return view;
    }

    private void setClickListeners() {
        startButton.setOnClickListener(view -> {
            if (!isRecording) {
                // Start recording and playback in a foreground service
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(),
                            new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
                    return;
                }
                startForegroundService();
                isRecording = true;
            }
            radioButtonOn.setEnabled(false);
            radioButtonOff.setEnabled(false);
            gainBar.setEnabled(false);
        });

        stopButton.setOnClickListener(view -> {
            // Stop the foreground service and recording
            if (isRecording) {
                requireActivity().stopService(AudioPlaybackService.getIntent(requireContext()));
                isRecording = false;
                 if(audioTrack != null) {
                    audioTrack.stop();
                    audioTrack.release();
                    audioTrack = null;
                }
                if (audioRecord != null) {
                    audioRecord.stop();
                    audioRecord.release();
                    audioRecord = null;
                }
            }
            radioButtonOn.setEnabled(true);
            radioButtonOff.setEnabled(true);
            gainBar.setEnabled(true);
        });

        radioButtonOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isNoiseReductionOn = true;
            }
        });
        radioButtonOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isNoiseReductionOn = false;
            }
        });

        gainBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Adjust audio gain based on seek bar progress
                newVolume = 100 + (int)(((float)progress / seekBar.getMax()) * 200);

                // Ensure that the volume level stays within the desired range
                if (newVolume < 100) {
                    newVolume = 100;
                } else if (newVolume > 300) {
                    newVolume = 300;
                }
                Log.d(TAG, "onProgressChanged: newVolume "+ newVolume);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void startForegroundService() {
        Log.d(TAG, "startForegroundService: " + isNoiseReductionOn);
        AudioPlaybackService.startService(requireContext(), isNoiseReductionOn, newVolume);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start recording
                startForegroundService();
                isRecording = true;
            }
        }
    }
}
