package com.example.voicecraft;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.time.Year;
import java.util.Calendar;
import java.util.Date;


public class HearingTestActivity extends AppCompatActivity {
    RadioButton leftButton, rightButton;
    Button canHearButton, cantHearButton, stopButton, playButton, resultbutton;
    TextView dBText, feqText;
    VideoView videoView;
    final Integer [] frequencies = {200, 600, 1000, 1400, 1800, 2200, 2600, 3000, 3400, 3800, 4200, 5000, 6000, 7000, 8000}; // Frequencies in Hz
    final Integer [] decibels = {10, 20, 30, 40, 50, 60, 70, 80, 90};
    final int duration = 1; //tone duration in seconds
    final int sampleRate = 44100;// sample rate, 441000 times per seconds
    final int numSamples = duration * sampleRate;
    private final double[] samples = new double[numSamples];
    final byte[] generatedSnd = new byte[2 * numSamples];
    int leftEarFlag = 1, rightEarFlag=0;
    int[] leftEarLoss = new int[15];
    int[] rightEarLoss = new int[15];
    int frqrow = 0;//index i frequency
    int dbrow = 0;//index j volume
    private Handler handler = new Handler();
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH) + 1;
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    LoginActivity lg = new LoginActivity();
    Intent result;
    String currentDate = day + "-" + month + "-" + year;
    String loggedInUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hearingtest);
        ImageView headphoneImage = findViewById(R.id.headphoneImg);
        Button stopTest = findViewById(R.id.stopTest);
        RadioGroup radioSelectEar=findViewById(R.id.radioGroupEar);
        radioSelectEar.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.leftRadioButton) {
                headphoneImage.setImageResource(R.drawable.logobg_shape);
            } else if (checkedId == R.id.rightRadioButton) {
                headphoneImage.setImageResource(R.drawable.headphoneright);
            }
        });
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

        SharedPreferences sharedPref = getSharedPreferences("user_shared_preferences", Context.MODE_PRIVATE);
        loggedInUserName = sharedPref.getString("UserName", "");

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.playsound);
        videoView.setVideoURI(videoUri);

        stopTest.setOnClickListener(v -> {
            Intent intent = new Intent(HearingTestActivity.this, MainActivity.class);
            intent.putExtra("fragmentToLoad", "homeFragment");
            startActivity(intent);
            finish();
        });

        updateText();

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canHearButton.setEnabled(false);
                cantHearButton.setEnabled(false);
                double sound =calculateAmplitude(decibels[dbrow]);
                genTone(frequencies[frqrow], sound);
                playSound();
                canHearButton.setEnabled(true);
                cantHearButton.setEnabled(true);
                //playButton.setEnabled(true);
            }
        });
        canHearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(leftEarFlag  == 1){
                    leftEarLoss[frqrow] = decibels[dbrow];
                }
                if(rightEarFlag == 1){
                    rightEarLoss[frqrow] = decibels[dbrow];
                }
                frqrow++;
                dbrow =0;
                if(frequencies[frqrow] >= 8000 ){
                    if(leftEarFlag == 0){
                        cantHearButton.setEnabled(false);
                        canHearButton.setEnabled(false);
                        playButton.setVisibility(View.INVISIBLE);
                        stopTest.setVisibility(View.INVISIBLE);
                        resultbutton.setVisibility(View.VISIBLE);
                    }
                    Toast.makeText(HearingTestActivity.this, "Maximum frequency value", Toast.LENGTH_SHORT).show();
                    Toast.makeText(HearingTestActivity.this,"Now the sound channel will be changed to examine the right ear", Toast.LENGTH_SHORT).show();
                    leftButton.setChecked(false);
                    leftButton.setEnabled(false);
                    rightButton.setEnabled(true);
                    rightButton.setChecked(true);
                    frqrow =0;
                    dbrow =0;
                    leftEarFlag=0;
                    rightEarFlag=1;
                }
                updateText();
            }
        });

        cantHearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dbrow++;
                if(frequencies[frqrow] > 8000 ){
                    if(leftEarFlag == 0) {
                        cantHearButton.setEnabled(false);
                        canHearButton.setEnabled(false);
                        playButton.setVisibility(View.INVISIBLE);
                        stopTest.setVisibility(View.INVISIBLE);
                        resultbutton.setVisibility(View.VISIBLE);
                    }
                    if(rightEarFlag == 0){
                        Toast.makeText(HearingTestActivity.this,"Now the sound channel will be changed to examine the right ear", Toast.LENGTH_SHORT).show();
                        leftButton.setChecked(false);
                        leftButton.setEnabled(false);
                        rightButton.setEnabled(true);
                        rightButton.setChecked(true);
                        frqrow =0; dbrow =0;
                        leftEarFlag=0;
                        rightEarFlag=1;
                    }
                }
                if(decibels[dbrow] >= 90){
                    if(leftEarFlag  == 1){
                        leftEarLoss[frqrow] = 80;
                    }
                    if(rightEarFlag == 1){
                        rightEarLoss[frqrow] = 80;
                    }

                    frqrow++;
                    Toast.makeText(HearingTestActivity.this,"Maximum sound level", Toast.LENGTH_SHORT).show();
                    dbrow =0;
                }
                updateText();
            }
        });

        resultbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialogButtonClicked(view);
            }
        });

    }

    private void updateText(){
        feqText.setText("Frequency:"+String.valueOf(frequencies[frqrow]));
        dBText.setText("Decibel:"+String.valueOf(decibels[dbrow]));
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
                final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                        sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                        AudioTrack.MODE_STATIC);
                audioTrack.write(generatedSnd, 0, generatedSnd.length);
                audioTrack.setStereoVolume(leftEarFlag,rightEarFlag);
                audioTrack.play();
                videoView.start();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        videoView.pause();
                    }
                }, 1000);
                // Update the UI on the main thread after the task is done
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Update UI components here
                    }
                });
            }
        }).start();

        final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        audioTrack.setStereoVolume(leftEarFlag,rightEarFlag);
        audioTrack.play();
        videoView.start();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                videoView.pause();
            }
        }, 1000);
    }

    public void showAlertDialogButtonClicked(View view) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Perform your time-consuming task here
                for (int i = 0; i<frequencies.length;i++){
                insertData(currentDate, frequencies[i], leftEarLoss[i], rightEarLoss[i]);
                }
                // Update the UI on the main thread after the task is done
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Update UI components here
                        AlertDialog.Builder builder = new AlertDialog.Builder(HearingTestActivity.this);

                        builder.setMessage("Test Complete");
                        // add a button
                        builder.setPositiveButton("Audiogram chart", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result = new Intent(getBaseContext(), GraphActivity.class);
                                //result.putExtra("leftEar", leftEarLoss);
                                //result.putExtra("rightEar", rightEarLoss);
                                result.putExtra("Date", currentDate);
                                result.putExtra("userName", loggedInUserName);
                                startActivity(result);
                                finish();
                            }
                        });
                        // create and show the alert dialog
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
            }
        }).start();

    }

    private void insertData(String currentDate,int frequency,int leftEarLoss, int rightEarLoss  ){
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