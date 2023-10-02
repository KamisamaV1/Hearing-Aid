package com.example.voicecraft;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.Manifest;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {
    private RadioGroup radioGroupNoiseSuppression;
    private RadioButton radioButtonOn, radioButtonOff;
    private Button startButton, stopButton;
    private SeekBar gainBar;
    private Context context;
    private boolean isNoiseReductionOn = false;
    private boolean isRecording = false;
    private static final int RADIO_BUTTON_ON = 1;
    private static final int RADIO_BUTTON_OFF = 2;
    private AudioRecord audioRecord;
    private AudioTrack audioTrack;
    private Thread thread;
    NoiseSuppression ns = new NoiseSuppression();

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        radioGroupNoiseSuppression = view.findViewById(R.id.radioGroupNoiseSuppression);
        radioButtonOn = view.findViewById(R.id.radioButtonOn);
        radioButtonOff = view.findViewById(R.id.radioButtonOff);
        startButton = view.findViewById(R.id.buttonStart);
        stopButton = view.findViewById(R.id.buttonStop);
        gainBar = view.findViewById(R.id.seekBarGain);
        context = requireContext(); // Use requireContext() to get the context

        // Set click listeners
        setClickListeners();

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                startAudioPlayback();
            }
        });
        return view;
    }

    private void setClickListeners() {
        startButton.setOnClickListener(view -> {
            if (!isRecording) {
                // Start a new thread for audio processing
                isRecording = true;
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startAudioPlayback();
                    }
                });
                thread.start();
            }
        });

        stopButton.setOnClickListener(view -> {
            // Stop the audio processing
            isRecording = false;
            if (audioTrack != null) {
                audioTrack.stop();
                audioTrack.release();
                audioTrack = null;
            }
            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
            }
            // Interrupt the thread to ensure it stops
            if (thread != null) {
                thread.interrupt();
            }
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
                int gain = progress - (seekBar.getMax() / 2); // Calculate gain adjustment
                setAudioGain(gain);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not used
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not used
            }
        });
    }

    private void startAudioPlayback() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, 0);
            return;
        }
        int sampleRate = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);
        int bufferSize = AudioRecord.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
        );
        // Create audio buffers
        int powerOfTwoBufferSize = 1;
        while (powerOfTwoBufferSize < bufferSize) {
            powerOfTwoBufferSize *= 2;
        }
        short[] buffer = new short[powerOfTwoBufferSize];

        // Create AudioRecord and AudioTrack objects
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_PERFORMANCE,
                sampleRate,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);

        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM,
                AudioTrack.PERFORMANCE_MODE_LOW_LATENCY);

        // Start recording and playback
        audioRecord.startRecording();
        audioTrack.play();

        // Continuously read audio data from the AudioRecord object and process it
        while (isRecording) {
            int numSamples = audioRecord.read(buffer, 0, bufferSize);
            if (isNoiseReductionOn == true) {
                short[] processedBuffer = ns.applyNoiseReduction(buffer, 10000);
                audioTrack.write(processedBuffer, 0, numSamples);
            } else if (isRecording == false) {
                audioTrack.write(buffer, 0, numSamples);
            } else {
                audioTrack.write(buffer, 0, numSamples);

            }

        }
    }

    private void setAudioGain(int gain) {
        // Adjust audio gain (volume) based on the gain value and maxGain
        float volume = (float) Math.pow(10, (float) gain / 20.0f);
        audioTrack.setVolume(volume);
    }
}
