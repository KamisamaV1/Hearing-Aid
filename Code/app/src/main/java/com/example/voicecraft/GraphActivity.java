package com.example.voicecraft;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends AppCompatActivity {
    Button backButton;
    private LineChart mChart;
    String userName, date;
    int[] leftEar= new int[15];
    int[] rightEar= new int[15];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        backButton = findViewById(R.id.backbutton);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        mChart = findViewById(R.id.linechart);

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);

        ArrayList<Entry> yValues1 = new ArrayList<>();
        ArrayList<Entry> yValues2 = new ArrayList<>();

        Bundle extras = new Bundle();
        extras = getIntent().getExtras();
        date = extras.getString("Date");
        userName = extras.getString("userName");

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Perform your time-consuming task here
                getData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Update UI components here
                        yValues1.add(new Entry(200, leftEar[0]));
                        yValues1.add(new Entry(600, leftEar[1]));
                        yValues1.add(new Entry(1000, leftEar[2]));
                        yValues1.add(new Entry(1400, leftEar[3]));
                        yValues1.add(new Entry(1800, leftEar[4]));
                        yValues1.add(new Entry(2200, leftEar[5]));
                        yValues1.add(new Entry(2600, leftEar[6]));
                        yValues1.add(new Entry(3000, leftEar[7]));
                        yValues1.add(new Entry(3400, leftEar[8]));
                        yValues1.add(new Entry(3800, leftEar[9]));
                        yValues1.add(new Entry(4200, leftEar[10]));
                        yValues1.add(new Entry(5000, leftEar[11]));
                        yValues1.add(new Entry(6000, leftEar[12]));
                        yValues1.add(new Entry(7000, leftEar[13]));
                        yValues1.add(new Entry(8000, leftEar[14]));

                        yValues2.add(new Entry(200, rightEar[0]));
                        yValues2.add(new Entry(600, rightEar[1]));
                        yValues2.add(new Entry(1000, rightEar[2]));
                        yValues2.add(new Entry(1400, rightEar[3]));
                        yValues2.add(new Entry(1800, rightEar[4]));
                        yValues2.add(new Entry(2200, rightEar[5]));
                        yValues2.add(new Entry(2600, rightEar[6]));
                        yValues2.add(new Entry(3000, rightEar[7]));
                        yValues2.add(new Entry(3400, rightEar[8]));
                        yValues2.add(new Entry(3800, rightEar[9]));
                        yValues2.add(new Entry(4200, rightEar[10]));
                        yValues2.add(new Entry(5000, rightEar[11]));
                        yValues2.add(new Entry(6000, rightEar[12]));
                        yValues2.add(new Entry(7000, rightEar[13]));
                        yValues2.add(new Entry(8000, rightEar[14]));

                        LineDataSet set1 = new LineDataSet(yValues1, "Left ear");
                        LineDataSet set2 = new LineDataSet(yValues2, "Right ear");
                        set1.setFillAlpha(85);
                        set2.setFillAlpha(85);
                        set1.setColor(Color.BLUE, 100);
                        set2.setColor(Color.RED, 100);
                        //set1.setLineWidth(100);

                        ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
                        iLineDataSets.add(set1);
                        iLineDataSets.add(set2);
                        LineData data = new LineData(iLineDataSets);
                        mChart.setData(data);
                        mChart.getAxisLeft().setInverted(true);

                        mChart.setTransitionName("test");
                    }
                });
            }
        }).start();

        Log.d("TAG",leftEar[0] + " " +leftEar[1] + " " +leftEar[2] + " " + leftEar[3] + " " +leftEar[4] + " "  +leftEar[5] + " ");
        Log.d("TAG",rightEar[0] + " " +rightEar[1] + " " +rightEar[2] + " " + rightEar[3] + " " +rightEar[4] + " "  +rightEar[5] + " ");


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(GraphActivity.this, MainActivity.class);
                startActivity(it);
            }
        });
    }

    private void getData(){
        AppDatabase database = AppDatabase.getAppDatabase(getApplicationContext());
        List<Calibration> calibration = database.calibrationDao().getAllCalibrations(userName, date);
        for (int i = 0; i < 15; i++) {
            leftEar[i] = calibration.get(i).getLossLeftEar();
            rightEar[i] = calibration.get(i).getLossRightEar();
        }
        Log.d("TAG",leftEar[0] + " " +leftEar[1] + " " +leftEar[2] + " " + leftEar[3] + " " +leftEar[4] + " "  +leftEar[5] + " ");
        Log.d("TAG",rightEar[0] + " " +rightEar[1] + " " +rightEar[2] + " " + rightEar[3] + " " +rightEar[4] + " "  +rightEar[5] + " ");
    }
}