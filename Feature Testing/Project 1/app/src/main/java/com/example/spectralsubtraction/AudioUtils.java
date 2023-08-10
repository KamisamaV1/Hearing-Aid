package com.example.spectralsubtraction;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import java.io.IOException;

public class AudioUtils {

    public static int getAudioSampleRate(Context context, Uri audioUri) throws IOException {
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        try {
            metadataRetriever.setDataSource(context, audioUri);
            String sampleRateString = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_SAMPLERATE);
            return Integer.parseInt(sampleRateString);
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Error occurred while extracting sample rate
        } finally {
            metadataRetriever.release();
        }
    }
}