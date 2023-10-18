package com.example.voicecraft;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class AudioPlaybackService extends Service {
    private static final String CHANNEL_ID = "AudioPlaybackChannel";
    private static final int NOTIFICATION_ID = 1;
    private final IBinder binder = new LocalBinder();
    private AudioRecord audioRecord;
    private AudioTrack audioTrack;
    private Thread audioThread;
    private NoiseSuppression ns = new NoiseSuppression();
    private boolean isNoiseReductionOn = false;
    private float volume = 0;
    private static final String TAG = "AudioPlaybackService";

    private Context context;

    public AudioPlaybackService() {
        super();
    }

    public AudioPlaybackService(Context context) {
        this.context = context;
    }

    public class LocalBinder extends Binder {
        AudioPlaybackService getService() {
            return AudioPlaybackService.this;
        }
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, AudioPlaybackService.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Audio Playback Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            //Log.d(TAG, intent.getBooleanExtra("isNoiseReductionOn", false) + "onStartCommand: ");
            isNoiseReductionOn = intent.getBooleanExtra("isNoiseReductionOn", false);
            volume = intent.getFloatExtra("volume",100);
        }
        //Log.d(TAG, "onStartCommand: " + isNoiseReductionOn());
        //Log.d(TAG,isNoiseReductionOn + "startAudioPlayback: ");
        if(volume == 0.0){
            volume = 100;
        }
        startAudioPlayback();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAudioPlayback();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Audio Playback Service")
                .setContentText("Recording and Playback in Progress")
                .setSmallIcon(R.drawable.ic_stat_hearing)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
    }

    private void startAudioPlayback() {
        //Log.d(TAG,isNoiseReductionOn + "startAudioPlayback: ");
        int sampleRate = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);
        int bufferSize = AudioRecord.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
        );

        // Initialize audioRecord
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
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

        // Initialize audioTrack
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM,
                AudioTrack.PERFORMANCE_MODE_LOW_LATENCY);

        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            //Log.e(TAG, "AudioRecord initialization failed.");
            // Handle the initialization failure, e.g., show an error message
            return;
        }

        if (audioTrack.getState() != AudioTrack.STATE_INITIALIZED) {
            //Log.e(TAG, "AudioTrack initialization failed.");
            // Handle the initialization failure, e.g., show an error message
            return;
        }

        audioRecord.startRecording();
        audioTrack.play();
        Log.d(TAG, "startAudioPlayback: volume: " +volume+" AudioTrack: "+audioTrack.getState());

        audioTrack.setVolume(volume);

        audioThread = new Thread(() -> {
            int powerOfTwoBufferSize = 1;
            while (powerOfTwoBufferSize < bufferSize) {
                powerOfTwoBufferSize *= 2;
            }
            short[] buffer = new short[powerOfTwoBufferSize];
            while (true) {
                int numSamples = 0;
                if (audioRecord != null) {
                    numSamples = audioRecord.read(buffer, 0, bufferSize);
                }
                // Check if audioTrack is null before synchronization
                if (audioTrack != null) {
                    synchronized (audioTrack) {
                        if (isNoiseReductionOn) {
                            short[] processedBuffer = ns.applyNoiseReduction(buffer, 10000);
                            if (audioTrack != null) {
                                audioTrack.write(processedBuffer, 0, numSamples);
                            }
                        } else {
                            if (audioTrack != null) {
                                audioTrack.write(buffer, 0, numSamples);
                            }
                        }
                    }
                }
            }
        });
        audioThread.start();
    }

    private void stopAudioPlayback() {
        if (audioThread != null) {
            audioThread.interrupt();
            audioThread = null;
        }
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
    }

    public static void startService(Context context, boolean isNoiseReductionOn, float volume) {
        Intent intent = new Intent(context, AudioPlaybackService.class);
        intent.putExtra("isNoiseReductionOn", isNoiseReductionOn);
        intent.putExtra("volume", volume);
        ContextCompat.startForegroundService(context, intent);
    }

    private PendingIntent createNotificationPendingIntent() {
        Intent intent = new Intent(this, MainActivity.class); // Replace YourMainActivity with your actual main activity class
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE); // Use FLAG_IMMUTABLE or FLAG_MUTABLE as needed
        return pendingIntent;
    }

    //public boolean isNoiseReductionOn() {return isNoiseReductionOn;}
}
