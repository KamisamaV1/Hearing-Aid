# Hearing-Aid
This project aims to provide affordable hearing aids to the masses by utilizing an Android device.

The RTNR stands for Real-Time Noise Reducer The Audio Recording and Noise Suppression Library created in JAVA The reference of the actual GitHub Repository - https://github.com/JaeBinCHA7/RTNR

The errors occurred in the testing of the library
1) -The tensorflow library error- java.lang.UnsatisfiedLinkError: Failed to load native TensorFlow Lite methods. Check that the correct native libraries are present, and, if using a custom native library, have been properly loaded via System.loadLibrary():  Solutions a) -The error was resolved by replacing (implementation 'org.tensorflow:tensorflow-lite:0.1'). in the gradle.build(app)
          b) -The error can also be resolved by creating the signed apk of the project.
2) -The size of the apk was 440MB after editing and removing non-required code from the library.
Solution  a) -Regarding the size we can remove the unused resources or we can keep "shrinkResources true" in build.gralde file.

3) -E/AndroidRuntime: FATAL EXCEPTION: Thread-2
    Process: com.example.speech_enhancement_rt_on_mobile, PID: 25327
    java.lang.NullPointerException: Attempt to invoke virtual method 'int java.io.InputStream.read(byte[], int, int)' on a null object reference
        at java.io.DataInputStream.read(DataInputStream.java:149)
        at com.example.speech_enhancement_rt_on_mobile.bottomnavi.AudioFragment$2.run(AudioFragment.java:323)
Solution  a) -After providing the All file permissions user will be able to test the "play original" i.e recording permission. If it is installed need to provide the permission manually by going into app info permissions in that files and media Next = "Allow management of all files"
