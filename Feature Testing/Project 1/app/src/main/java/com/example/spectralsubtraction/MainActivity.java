package com.example.spectralsubtraction;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity {
    private static final int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 1;
    private static final int PICK_AUDIO_REQUEST_CODE = 2;

    private Uri selectedAudioUri;
    private MediaPlayer mediaPlayer;
    private Button uploadButton;
    private EditText thresholdEditText;
    private Button playButton;
    private Button stopButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadButton = findViewById(R.id.uploadButton);
        thresholdEditText = findViewById(R.id.thresholdEditText);
        playButton = findViewById(R.id.playButton);
        stopButton = findViewById(R.id.stopButton);

        uploadButton.setOnClickListener(view -> {
            // Request storage permission to upload the audio file
            Log.d("MainActivity", "Upload button clicked. Requesting storage permission...");
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MainActivity.this,"You have already given the permission",Toast.LENGTH_SHORT).show();
            }
            else{
                requestStoragePermission();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Process and play the audio using the spectral subtraction algorithm
                double threshold = Double.parseDouble(thresholdEditText.getText().toString());
                // Implement the audio processing logic here using the spectralSubtraction method

                // Example: Play the processed audio
                if (selectedAudioUri != null) {
                    try {
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(getApplicationContext(), selectedAudioUri);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Stop audio playback
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            }
        });
    }

    private void requestStoragePermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission")
                    .setMessage("Requesting permission for read the audio file from storage")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();
        }else {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION_CODE);
        }
    }

    private void selectAudioFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, PICK_AUDIO_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // After handling permission result, check if it's for audio file selection
        if (requestCode == PICK_AUDIO_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with audio file selection
                selectAudioFile();
            } else {
                // Permission denied, handle this case appropriately (e.g., show a message to the user)
                Toast.makeText(this, "Permission denied. Cannot upload audio file.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_AUDIO_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Toast.makeText(this, "Audio file selected.", Toast.LENGTH_SHORT).show();
            selectedAudioUri = data.getData();
            // Proceed with processing the selected audio file and apply the spectral subtraction algorithm
            double threshold = Double.parseDouble(thresholdEditText.getText().toString());

             try {
                // Read the audio file into a byte array
                InputStream inputStream = getContentResolver().openInputStream(selectedAudioUri);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }
                byte[] audioData = byteArrayOutputStream.toByteArray();

                // Convert the byte array to a double array for processing
                double[] audioSamples = new double[audioData.length];
                for (int i = 0; i < audioData.length; i++) {
                    audioSamples[i] = audioData[i];
                }
                AudioUtils audioUtils = new AudioUtils();
                // Apply spectral subtraction with the given threshold
                double desiredChunkDurationInSeconds;
                int frameSize = 0;
                int overlap = 0;
                int sampleRate = AudioUtils.getAudioSampleRate(this, selectedAudioUri); // Replace with the actual sample rate of the audio
                if (sampleRate != -1) {
                    // Calculate the desired chunk duration (e.g., 0.1 seconds)
                    desiredChunkDurationInSeconds = 0.1;
                    frameSize = (int) (desiredChunkDurationInSeconds * sampleRate);
                    // Calculate the overlap based on your desired overlap ratio (e.g., 50%)
                    overlap = frameSize / 2;
                    double audioDurationInSeconds = audioData.length / (double) sampleRate;
                    double frameDurationInSeconds = frameSize / (double) sampleRate;
                    int framesCount = (int) Math.ceil((audioDurationInSeconds - frameDurationInSeconds) / (frameDurationInSeconds - overlap / (double) sampleRate)) + 1;

                    // Perform STFT (Short-time Fourier Transform) on audioSamples
                    FastFourierTransformer fftTransformer = new FastFourierTransformer(DftNormalization.STANDARD);
                    Complex[][] stftMatrix = new Complex[framesCount][frameSize];

                    int startSample = 0;
                    for (int i = 0; i < framesCount; i++) {
                        double[] frame = new double[frameSize];
                        for (int j = 0; j < frameSize; j++) {
                            frame[j] = audioSamples[startSample + j];
                        }
                        Complex[] complexFrame = fftTransformer.transform(frame, TransformType.FORWARD);
                        stftMatrix[i] = complexFrame;
                        startSample += frameSize - overlap;
                    }

                    // Apply spectral subtraction with the given threshold
                    stftMatrix = spectralSubtraction(stftMatrix, frameSize, framesCount, overlap, threshold);

                    // Inverse STFT (ISTFT) to obtain processed audio samples
                    int outputSamplesLength = framesCount * (frameSize - overlap) + overlap;
                    double[] outputSamples = new double[outputSamplesLength];

                    int outputIndex = 0;
                    for (int i = 0; i < framesCount; i++) {
                        Complex[] frameReconstructed = fftTransformer.transform(stftMatrix[i], TransformType.INVERSE);
                        for (int j = 0; j < frameSize; j++) {
                            outputSamples[outputIndex + j] += frameReconstructed[j].getReal();
                        }
                        outputIndex += frameSize - overlap;
                    }

                    // Convert the processed double array back to a byte array for audio playback
                    byte[] processedAudioData = new byte[outputSamplesLength];
                    for (int i = 0; i < outputSamplesLength; i++) {
                        processedAudioData[i] = (byte) outputSamples[i];
                    }

                    // Save the processed audio data to a temporary file and play it using MediaPlayer
                    File tempFile = File.createTempFile("processed_audio", ".wav", getCacheDir());
                    FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
                    fileOutputStream.write(processedAudioData);
                    fileOutputStream.close();

                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(getApplicationContext(), Uri.fromFile(tempFile));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static Complex[][] spectralSubtraction(Complex[][] stftMatrix, int frameSize, int framesCount, int overlap, double threshold) {
        // Estimate the noise spectrum (average of magnitude spectra in silence or low activity regions)
        Complex[] noiseSpectrum = new Complex[frameSize];
        for (int i = 0; i < frameSize; i++) {
            double magnitudeSum = 0.0;
            int framesForNoiseEstimation = 0;

            // Detect silence or low activity regions (you can modify this part based on your needs)
            for (int j = 0; j < framesCount; j++) {
                double frameMagnitude = stftMatrix[j][i].abs();
                if (frameMagnitude < threshold) {
                    magnitudeSum += frameMagnitude;
                    framesForNoiseEstimation++;
                }
            }

            if (framesForNoiseEstimation > 0) {
                double averageMagnitude = magnitudeSum / framesForNoiseEstimation;
                noiseSpectrum[i] = Complex.valueOf(averageMagnitude, 0);
            } else {
                noiseSpectrum[i] = Complex.ZERO;
            }
        }

        // Subtract noise spectrum from the magnitude spectra of each frame
        for (int i = 0; i < framesCount; i++) {
            for (int j = 0; j < frameSize; j++) {
                Complex originalMagnitude = stftMatrix[i][j];
                Complex noiseMagnitude = noiseSpectrum[j];
                // Spectral subtraction
                double correctedMagnitude = Math.max(originalMagnitude.abs() - noiseMagnitude.abs(), 0);
                stftMatrix[i][j] = Complex.valueOf(correctedMagnitude, 0);
            }
        }

        return stftMatrix;
    }
}