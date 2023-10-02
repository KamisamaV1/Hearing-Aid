package com.example.voicecraft;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends AppCompatActivity {
    Button backButton;
    private LineChart mChart;
    String date;
    int[] leftEar = new int[15];
    int[] rightEar = new int[15];
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        backButton = findViewById(R.id.backbutton);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        mChart = findViewById(R.id.linechart);

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setDrawGridLines(true);

        YAxis yAxisLeft = mChart.getAxisLeft();
        yAxisLeft.setDrawGridLines(false);

        Bundle extras = getIntent().getExtras();
        date = extras.getString("Date");

        // Show a loading dialog while fetching data
        progressDialog.show();

        // Execute the background task to fetch data
        new YourTask().execute();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(GraphActivity.this, MainActivity.class);
                startActivity(it);
            }
        });
    }

    // AsyncTask to perform the background task
    private class YourTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // Perform your background task here (fetching data from the database)
            getData();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Update the graph data
            ArrayList<Entry> updatedYValues1 = new ArrayList<>();
            ArrayList<Entry> updatedYValues2 = new ArrayList<>();

            // Populate updated data (you can adjust this as needed)
            updatedYValues1.add(new Entry(400, ((leftEar[0] + leftEar[1]) / 2)));
            updatedYValues1.add(new Entry(1000, leftEar[2]));
            updatedYValues1.add(new Entry(2000, ((leftEar[3] + leftEar[4] + leftEar[5]) / 3)));
            updatedYValues1.add(new Entry(4000, ((leftEar[6] + leftEar[7] + leftEar[8] + leftEar[9] + leftEar[10]) / 5)));
            updatedYValues1.add(new Entry(6000, ((leftEar[11] + leftEar[12]) / 2)));
            updatedYValues1.add(new Entry(8000, ((leftEar[13] + leftEar[14]) / 2)));

            updatedYValues2.add(new Entry(400, ((rightEar[0] + rightEar[1]) / 2)));
            updatedYValues2.add(new Entry(1000, rightEar[2]));
            updatedYValues2.add(new Entry(2000, ((rightEar[3] + rightEar[4] + rightEar[5]) / 3)));
            updatedYValues2.add(new Entry(4000, ((rightEar[6] + rightEar[7] + rightEar[8] + rightEar[9] + rightEar[10]) / 5)));
            updatedYValues2.add(new Entry(6000, ((rightEar[11] + rightEar[12]) / 2)));
            updatedYValues2.add(new Entry(8000, ((rightEar[13] + rightEar[14]) / 2)));

            // Update the LineDataSet
            LineDataSet set1 = new LineDataSet(updatedYValues1, "Left ear");
            LineDataSet set2 = new LineDataSet(updatedYValues2, "Right ear");
            set1.setFillAlpha(85);
            set2.setFillAlpha(85);
            set1.setColor(Color.BLUE, 100);
            set2.setColor(Color.RED, 100);

            // Update the chart data
            ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
            iLineDataSets.add(set1);
            iLineDataSets.add(set2);
            LineData data = new LineData(iLineDataSets);
            mChart.setData(data);
            mChart.getAxisLeft().setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            mChart.getAxisLeft().setInverted(true);

            // Define the limit lines for each colored section
            YAxis yAxis = mChart.getAxisLeft();
            LimitLine mildHearingLoss = new LimitLine(40f, "Mild Hearing Loss");
            mildHearingLoss.setLineColor(Color.GREEN);
            mildHearingLoss.setLineWidth(2f);
            yAxis.addLimitLine(mildHearingLoss);

            LimitLine moderateHearingLoss = new LimitLine(60f, "Moderate Hearing Loss");
            moderateHearingLoss.setLineColor(Color.YELLOW);
            moderateHearingLoss.setLineWidth(2f);
            yAxis.addLimitLine(moderateHearingLoss);

            LimitLine severeHearingLoss = new LimitLine(80f, "Severe Hearing Loss");
            severeHearingLoss.setLineColor(Color.RED);
            severeHearingLoss.setLineWidth(2f);
            yAxis.addLimitLine(severeHearingLoss);

            // Notify the chart that the data has changed
            mChart.notifyDataSetChanged();
            mChart.invalidate(); // Refresh the chart

            // Hide the loading dialog when the task is complete
            progressDialog.dismiss();
        }
    }

    private void getData() {
        AppDatabase database = AppDatabase.getAppDatabase(getApplicationContext());
        List<Calibration> calibration = database.calibrationDao().getAllCalibrations(date);
        for (int i = 0; i < leftEar.length; i++) {
            leftEar[i] = calibration.get(i).getLossLeftEar();
            Log.d("TAG", leftEar[i] + " ");
            rightEar[i] = calibration.get(i).getLossRightEar();
            Log.d("TAG", rightEar[i] + " ");
        }
    }
}