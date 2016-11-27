package com.website.alphafitness;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

import static com.github.mikephil.charting.charts.Chart.LOG_TAG;
import static com.google.android.gms.common.api.Status.we;
import static com.website.alphafitness.R.string.distance;

/**
 * Created by jayashreemadhanraj on 10/13/16.
 */

public class User_Profile extends Activity {
    private static final String LOG_TAG = "User_Profile";
    private EditText height;
    private EditText weight;
    private EditText name;
    String userName;
    double userHeight;
    double userWeight;
    private User user = new User();
    private TextView weeklyDistance;
    private TextView allTimeDistance;
    private TextView weeklyDuration;
    private TextView allTimeDuration;
    private TextView weeklyCalories;
    private TextView allTimeCalories;
    private TextView weeklyCounts;
    private TextView allTimeCounts;
    DataHelper dataHelper = new DataHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        initializeFormFields();
       if(dataHelper.createfirstUser()){
           Log.v(LOG_TAG, "First User Created!");
       }
        createUser();


    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void createUser(){



        name.setText(dataHelper.getName());
      height.setText(Double.toString(dataHelper.getHeight()));
        weight.setText(Double.toString(dataHelper.getWeight()));
        String currentUser = name.getText().toString();

        allTimeDistance.setText(Double.toString(dataHelper.calcTotalDistance(currentUser)/10) + " m");
        weeklyDistance.setText(Double.toString(dataHelper.calcTotalDistance(currentUser)/70) + " m");

        allTimeDuration.setText(dataHelper.calcTotalDuration(currentUser));
        weeklyDuration.setText(dataHelper.calcWeeklyDuration(currentUser));

        allTimeCounts.setText(dataHelper.totalWorkouts(currentUser));
        weeklyCounts.setText(dataHelper.weeklyWorkouts(currentUser));

        allTimeCalories.setText(Double.toString(dataHelper.calcTotalCalories(currentUser)));
        weeklyCalories.setText(Double.toString(dataHelper.calcWeeklyCalories(currentUser)));
    }

    private void initializeFormFields() {


        height = (EditText) findViewById(R.id.height);
        weight = (EditText) findViewById(R.id.weight);
        name = (EditText) findViewById(R.id.userName);
        weeklyDistance = (TextView) findViewById(R.id.weeklyDistance);
        allTimeDistance = (TextView) findViewById(R.id.allTimeDistance);
        weeklyDuration = (TextView) findViewById(R.id.weeklyDuration);
        allTimeDuration = (TextView) findViewById(R.id.allTimeDuration);
        weeklyCounts = (TextView) findViewById(R.id.weeklyCounts);
        allTimeCounts = (TextView) findViewById(R.id.allTimeCounts);
        weeklyCalories = (TextView) findViewById(R.id.weeklyCalories);
        allTimeCalories = (TextView) findViewById(R.id.allTimeCalories);
        Button button = (Button) findViewById(R.id.change);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                userName = name.getText().toString();
                userHeight = Double.parseDouble(height.getText().toString());
                userWeight = Double.parseDouble(weight.getText().toString());
                user.setName(userName);
                user.setHeight(userHeight);
                user.setWeight(userWeight);
                dataHelper.updateUser(user);
            }
        });
    }

}
