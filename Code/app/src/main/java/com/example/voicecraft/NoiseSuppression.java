package com.example.voicecraft;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class NoiseSuppression {

    public short[] applyNoiseReduction(short[] buffer, double threshold) {
        /* Convert the buffer to complex values */
        double[] samples = new double[buffer.length];
        for (int i = 0; i < buffer.length; i++) {
            samples[i] = buffer[i];
        }

        Complex[] complexSamples = new Complex[samples.length];

        for (int i = 0; i < samples.length; i++) {
            complexSamples[i] = new Complex(samples[i], 0);
        }

        // Perform Fourier transform
        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] transformedSamples = fft.transform(complexSamples, TransformType.FORWARD);

        // Apply spectral subtraction
        for (int i = 0; i < transformedSamples.length; i++) {
            double magnitude = transformedSamples[i].abs();
            double phase = transformedSamples[i].getArgument();
            double newMagnitude = Math.max(magnitude - threshold, 0);
            transformedSamples[i] = ComplexUtils.polar2Complex(newMagnitude, phase);
        }

        // Perform inverse Fourier transform
        Complex[] inverseTransformedSamples = fft.transform(transformedSamples, TransformType.INVERSE);

        // Convert the complex samples back to short values
        short[] outputBuffer = new short[inverseTransformedSamples.length];
        for (int i = 0; i < inverseTransformedSamples.length; i++) {
            outputBuffer[i] = (short) inverseTransformedSamples[i].getReal();
        }

        return outputBuffer;
    }
}