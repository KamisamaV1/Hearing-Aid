package com.example.voicecraft;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class HearingTestActivity extends AppCompatActivity {
    private RadioButton leftButton, rightButton;
    private Button canHearButton, cantHearButton, stopButton, playButton, resultbutton;
    private TextView dBText, feqText;
    private VideoView videoView;

    private final Integer[] frequencies = {200, 600, 1000, 1400, 1800, 2200, 2600, 3000, 3400, 3800, 4200, 5000, 6000, 7000, 8000}; // Frequencies in Hz
    private final Integer[] decibels = {10, 20, 30, 40, 50, 60, 70, 80, 90};
    private final int duration = 10; //tone duration in seconds
    private final int sampleRate = 44100;// sample rate, 441000 times per second
    private final int numSamples = duration * sampleRate;
    private final byte[] generatedSnd = new byte[2 * numSamples];

    private int leftEarFlag = 1, rightEarFlag = 0;
    private final int[] leftEarLoss = new int[15];
    private final int[] rightEarLoss = new int[15];

    private int frqrow = 0; // index frequency
    private int dbrow = 0; // index volume

    private final Handler handler = new Handler();
    private final Calendar calendar = Calendar.getInstance();
    private final int year = calendar.get(Calendar.YEAR);
    private final int month = calendar.get(Calendar.MONTH) + 1;
    private final int day = calendar.get(Calendar.DAY_OF_MONTH);

    private Intent result;
    private final String currentDate = day + "-" + month + "-" + year;
    private String loggedInUserName;

    final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
            sampleRate, AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
            AudioTrack.MODE_STATIC);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hearingtest);
        initView();
        setListeners();
        initVideoView();
        SharedPreferences sharedPref = getSharedPreferences("user_shared_preferences", MODE_PRIVATE);
        loggedInUserName = sharedPref.getString("loggedInUserName", "");
    }

    private void initView() {
        // Initialize your views here
        leftButton = findViewById(R.id.leftRadioButton);
        rightButton = findViewById(R.id.rightRadioButton);
        canHearButton = findViewById(R.id.yesBtn);
        cantHearButton = findViewById(R.id.noBtn);
        stopButton = findViewById(R.id.stopTest);
        playButton = findViewById(R.id.playButton);
        dBText = findViewById(R.id.dbText);
        feqText = findViewById(R.id.fqText);
        videoView = findViewById(R.id.videoView);
        resultbutton = findViewById(R.id.resultbutton);
    }

    private void setListeners() {
        // Set your listeners here
        stopButton.setOnClickListener(v -> navigateToMainActivity());

        playButton.setOnClickListener(v -> handlePlayButtonClick());

        canHearButton.setOnClickListener(v -> handleCanHearButtonClick());

        cantHearButton.setOnClickListener(v -> handleCantHearButtonClick());

        resultbutton.setOnClickListener(this::showAlertDialogButtonClicked);
    }

    private void handlePlayButtonClick() {
        double sound =calculateAmplitude(decibels[dbrow]);
        genTone(frequencies[frqrow], sound);
        playSound();
    }

    private void initVideoView() {
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.playsound1);
        videoView.setVideoURI(videoUri);
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(HearingTestActivity.this, MainActivity.class);
        intent.putExtra("fragmentToLoad", "homeFragment");
        startActivity(intent);
        finish();
    }

    private void handleCanHearButtonClick() {
        videoView.pause();
        audioTrack.stop();

        if (leftEarFlag == 1) {
            leftEarLoss[frqrow] = decibels[dbrow];
        }
        if (rightEarFlag == 1) {
            rightEarLoss[frqrow] = decibels[dbrow];
        }
        frqrow++;
        dbrow = 0;
        if (frqrow >= frequencies.length) {
            handleEndOfTest();
            return;
        }
        updateText();
    }

    private void handleCantHearButtonClick() {
        videoView.pause();
        audioTrack.stop();
        dbrow++;
        if (frqrow >= frequencies.length) {
            handleEndOfTest();
            return;
        }
        if (frequencies[frqrow] > 9000) {
            if (leftEarFlag == 0) {
                handleEndOfTest();
                return;
            }
            if (rightEarFlag == 0) {
                Toast.makeText(HearingTestActivity.this, "Now the sound channel will be changed to examine the right ear", Toast.LENGTH_SHORT).show();
                leftButton.setChecked(false);
                leftButton.setEnabled(false);
                rightButton.setEnabled(true);
                rightButton.setChecked(true);
                frqrow = 0;
                dbrow = 0;
                leftEarFlag = 0;
                rightEarFlag = 1;
            }
        }
        if (decibels[dbrow] >= 90) {
            if (leftEarFlag == 1) {
                leftEarLoss[frqrow] = 80;
            }
            if (rightEarFlag == 1) {
                rightEarLoss[frqrow] = 80;
            }
            frqrow++;
            dbrow = 0;
        }
        updateText();
    }

    private void handleEndOfTest() {
        if (leftEarFlag == 0) {
            cantHearButton.setEnabled(false);
            canHearButton.setEnabled(false);
            playButton.setVisibility(View.INVISIBLE);
            stopButton.setVisibility(View.INVISIBLE);
            resultbutton.setVisibility(View.VISIBLE);
        }
        if (rightEarFlag == 0) {
            Toast.makeText(HearingTestActivity.this, "Now the sound channel will be changed to examine the right ear", Toast.LENGTH_SHORT).show();
            leftButton.setChecked(false);
            leftButton.setEnabled(false);
            rightButton.setEnabled(true);
            rightButton.setChecked(true);
            frqrow = 0;
            dbrow = 0;
            leftEarFlag = 0;
            rightEarFlag = 1;
        }
        //updateText();
    }

    private void updateText() {
        feqText.setText("Frequency: " + String.valueOf(frequencies[frqrow])+"Hz");
        dBText.setText("Decibel: " + String.valueOf(decibels[dbrow])+"dB");
    }

    public static double calculateAmplitude(int intensity) {
        // Calculate amplitude based on intensity (dB SPL)
        double referencePressure = 20.0e-6; // Reference pressure in Pascals (20 µPa)
        double soundPressure = Math.pow(10, intensity / 20.0) * referencePressure;
        return soundPressure;
    }

    void genTone(int freqOfTone, double volume) {
        for (int i = 0; i < numSamples; i++) {
            double t = (double) i / sampleRate;
            double sineValue = volume * Math.sin(2.0 * Math.PI * freqOfTone * t);
            short shortValue = (short) (sineValue * Short.MAX_VALUE);
            generatedSnd[2 * i] = (byte) (shortValue & 0xFF);
            generatedSnd[2 * i + 1] = (byte) ((shortValue >> 8) & 0xFF);
        }
    }
    void playSound(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Perform your time-consuming task here
                audioTrack.write(generatedSnd, 0, generatedSnd.length);
                audioTrack.setStereoVolume(leftEarFlag,rightEarFlag);
                audioTrack.play();
                videoView.start();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        videoView.pause();
                    }
                }, 10000);
                // Update the UI on the main thread after the task is done
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Update UI components here
                        cantHearButton.setEnabled(true);
                        canHearButton.setEnabled(true);
                    }
                });
            }
        }).start();
    }

    private void showAlertDialogButtonClicked(View view) {
        new Thread(() -> {
            // Perform your time-consuming task here
            for (int i = 0; i < frequencies.length; i++) {
                insertData(currentDate, frequencies[i], leftEarLoss[i], rightEarLoss[i]);
                Log.d("TAG", leftEarLoss[i] + " ");
                Log.d("TAG", rightEarLoss[i] + " ");
            }
            // Update the UI on the main thread after the task is done
            runOnUiThread(() -> {
                // Update UI components here
                showTestCompleteAlertDialog();
            });
        }).start();
    }

    private void showTestCompleteAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HearingTestActivity.this);
        builder.setMessage("Test Complete");
        builder.setPositiveButton("Audiogram chart", (dialog, which) -> {
            result = new Intent(getBaseContext(), GraphActivity.class);
            result.putExtra("Date", currentDate);
            result.putExtra("userName", loggedInUserName);
            startActivity(result);
            finish();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void insertData(String currentDate,int frequency,int leftEarLoss, int rightEarLoss){
        AppDatabase database = AppDatabase.getAppDatabase(getApplicationContext());

        // Create a Calibration object
        Calibration calibration = new Calibration();
            calibration.setCalibrationDate(currentDate);
            calibration.setFrequency(frequency);
            calibration.setLossLeftEar(leftEarLoss);
            calibration.setLossRightEar(rightEarLoss);
            calibration.setUserName(loggedInUserName);

        // Insert the Calibration object into the database
        CalibrationDao calibrationDao = database.calibrationDao();
        calibrationDao.insertCalibration(calibration);
    }
}