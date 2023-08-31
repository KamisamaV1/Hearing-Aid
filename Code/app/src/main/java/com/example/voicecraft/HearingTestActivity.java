package com.example.voicecraft;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;

public class HearingTestActivity extends AppCompatActivity {

    RadioButton leftButton, rightButton;
    Button canHearButton, cantHearButton, stopButton, playButton;
    TextView dBText, feqText;

    VideoView videoView;
    final Integer [] frequencies = {200, 600, 1000, 1400, 1800, 2200, 2600, 3000, 3400, 3800, 4200, 5000, 6000, 7000, 8000}; // Frequencies in Hz
    final Integer [] decibels = {10, 20, 30, 40, 50, 60, 70, 80, 90};

    //final Double [] volumes = {1.0/*0dB*/, 3.16227766/*10dB*/, 10.0/*20dB*/, 31.6227766/*30dB*/, 100.0/*40dB*/, 316.227766/*50dB*/,
           // 1000.0/*60dB*/, 3162.27766/*70dB*/, 10000.0/*80dB*/, 31622.7766/*90dB*/};

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

    String currentDate = year + "-" + month + "-" + day;

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
                        playButton.setText("Result");
                        playButton.setVisibility(View.VISIBLE);

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
                        playButton.setText("Result");
                        playButton.setVisibility(View.VISIBLE);
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

    }

    private void updateText(){
        feqText.setText(String.valueOf(frequencies[frqrow]));
        dBText.setText(String.valueOf(decibels[dbrow]));
    }

    public static double calculateAmplitude(int intensity) {
        // Calculate amplitude based on intensity (dB SPL)
        double referencePressure = 20.0e-6; // Reference pressure in Pascals (20 µPa)
        double soundPressure = Math.pow(10, intensity / 20.0) * referencePressure;
        return soundPressure;
    }

    void genTone(int freqOfTone, double volume) {
        // fill out the array
        /*for (int i = 0; i < numSamples; ++i) {
            samples[i] =  (Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone)));
        }
        int idx = 0;
        for (final double dVal : samples) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * volume));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }*/
        for (int i = 0; i < numSamples; i++) {
            double t = (double) i / sampleRate;
            double sineValue = volume * Math.sin(2.0 * Math.PI * freqOfTone * t);
            short shortValue = (short) (sineValue * Short.MAX_VALUE);
            generatedSnd[2 * i] = (byte) (shortValue & 0xFF);
            generatedSnd[2 * i + 1] = (byte) ((shortValue >> 8) & 0xFF);
        }
    }
    void playSound(){
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

        /*int hearLossLeft;
        int hearLossRight;
        int hearLoss = 0;
        hearLossLeft = (leftEarLoss[3]+leftEar[4]+leftEar[6])/3;
        hearLossRight = (rightEar[3]+rightEar[4]+rightEar[6])/3;


        if (hearLossLeft>hearLossRight){
            hearLoss = hearLossRight;
        }
        else{
            hearLoss = hearLossLeft;
        }*/

        Calibration hearingLossEntry = new Calibration();
        hearingLossEntry.setLossLeftEar(leftEarLoss);
        hearingLossEntry.setLossRightEar(rightEarLoss);
        hearingLossEntry.setCalibrationDate(Integer.parseInt(currentDate));

        AppDatabase appDatabase = AppDatabase.getAppDatabase(this);
        CalibrationDao hearingLossDao = appDatabase.calibrationDao();
        hearingLossDao.insertCalibration(hearingLossEntry);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Test Complete");
        // add a button
        builder.setPositiveButton("Audiogram chart", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*result = new Intent(getBaseContext(), GraphActivity.class);
                result.putExtra("leftEar", leftEarLoss);
                result.putExtra("rightEar", rightEarLoss);
                startActivity(result);*/
                finish();
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}