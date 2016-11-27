package com.website.alphafitness;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.vision.text.Text;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;
import static android.R.attr.entries;
import static com.website.alphafitness.R.id.chart;
import static com.website.alphafitness.R.id.weight;
import static com.website.alphafitness.R.string.distance;

/**
 * Created by jayashreemadhanraj on 9/30/16.
 */
public class Landscape_Fragment_RecordWorkout extends Fragment {
    TextView avgMinKm;
    TextView minMinKm;
    TextView maxMinKm;
    DataHelper db;
    int time;
    int time2;
    List<Double> calories = new ArrayList<>();
    List<Integer> steps = new ArrayList<>();

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_landscape_layout,container,false);

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new DataHelper((getActivity()));
        avgMinKm = (TextView) getView().findViewById(R.id.avgMinKm);
        minMinKm = (TextView) getView().findViewById(R.id.minMinKm);
        maxMinKm = (TextView) getView().findViewById(R.id.maxMinKm);

        avgMinKm.setText(db.calcAverage());
        minMinKm.setText(db.calcMinimum());
        maxMinKm.setText(db.calcMaximum());

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity a;

        if (context instanceof Activity){
            a=(Activity) context;
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LineChart chart = (LineChart) getActivity().findViewById(R.id.chart);
         calories = db.readCalorieForChart();
        steps = db.readDistanceForChart();

        List<Entry> calorieEntries = new ArrayList<Entry>();
        List<Entry> distanceEntries = new ArrayList<Entry>();
        calorieEntries.add(new Entry(0, (float) 0.0));
        distanceEntries.add(new Entry(0, (float) 0.0));
        for(double calorie : calories){
            calorieEntries.add(new Entry(time, (float) calorie));
            time += 100;
        }
        LineDataSet dataSet = new LineDataSet(calorieEntries, "Calories");
        dataSet.setColor(Color.BLUE);

        for(double d : steps){
            distanceEntries.add(new Entry(time2, (float) d/10));
            time2 += 100;
        }
        LineDataSet dataSet1 = new LineDataSet(distanceEntries, "Steps");
        dataSet.setDrawValues(false);
        dataSet1.setDrawValues(false);
        dataSet1.setColor(Color.RED);
        LineData data = new LineData(dataSet,dataSet1);
        chart.setData(data);
    }


}
