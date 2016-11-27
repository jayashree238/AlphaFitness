package com.website.alphafitness;

import android.*;
import android.os.*;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;
import static com.github.mikephil.charting.charts.Chart.LOG_TAG;
import static com.google.android.gms.internal.zzsp.LO;

/**
 * Created by jayashreemadhanraj on 10/12/16.
 */

public class LogWorkout extends Service implements LocationListener, SensorEventListener, ConnectionCallbacks, OnConnectionFailedListener {

    private Location myLocation = null;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    //private Workout currentWorkout;
    Workout_Point workoutPoint;
    int workoutID;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    //DataHelper dataHelper = new DataHelper(this.getApplicationContext());
    DataHelper dataHelper;

    private SensorManager mSensorManager;
    private Sensor mStepCounterSensor;

    private Sensor mStepDetectorSensor;
    private int stepCounter = 0;
    private int counterSteps = 0;
    private int stepDetector = 0;

    int updateLatLng = 2;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    /**
     * Called by the system when the service is first created.  Do not call this method directly.
     */
    @Override
    public void onCreate() {
        super.onCreate();

//        Toast.makeText(this, "ServiceStarting3", Toast.LENGTH_SHORT).show();
        buildGoogleApiClient();
        //Step counter and step detector
        mSensorManager = (SensorManager)
                getSystemService(Context.SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetectorSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mGoogleApiClient.connect();
//        Toast.makeText(this, "ServiceStarting2", Toast.LENGTH_SHORT).show();
        dataHelper = new DataHelper(this.getApplicationContext());
        mSensorManager.registerListener(this, mStepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mStepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);

        return super.onStartCommand(intent, flags, startId);
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    private void startLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        updateLatLng = 0;
        stopLocationUpdates();
        mSensorManager.unregisterListener(this, mStepCounterSensor);
        mSensorManager.unregisterListener(this, mStepDetectorSensor);
//        Toast.makeText(this, "shutting service", Toast.LENGTH_LONG).show();
//        Toast.makeText(this, "Final Step Counter Value : " + stepCounter, Toast.LENGTH_LONG).show();
        mGoogleApiClient.disconnect();
    }

    private void createLocationRequest(){
        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


    }

    private void stopLocationUpdates(){
        if(mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        myLocation = location;
        if (location != null)
            updateLatLng++;
            if(updateLatLng % 3 == 0) {

                updateDB();
            }
    }

    private void updateDB() {
        workoutPoint = new Workout_Point(workoutID, myLocation.getLatitude(), myLocation.getLongitude());
        dataHelper.addWorkoutPoint(workoutPoint);

//        Toast.makeText(this, success, Toast.LENGTH_SHORT).show();
//
    }



    @Override
    public void onConnected(Bundle bundle) {
        //currentWorkout = new Workout();
//        Toast.makeText(this, "ServiceStarting1", Toast.LENGTH_SHORT).show();
        workoutID = dataHelper.createWorkout();
        if (myLocation == null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (myLocation != null){
                updateDB();
            }
        }
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        float[] values = event.values;
        int value = -1;



        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            if (counterSteps < 1) {
                value = (int) values[0];
                counterSteps = value;
            }
            if (values.length > 0) {
                value = (int) values[0];
            }
            stepCounter = value - counterSteps;
            Intent intent = new Intent("record_workout_step_count");
            intent.putExtra("Step_Count",stepCounter);
            sendBroadcast(intent);

//           Toast.makeText(this, "Step Counter : " + stepCounter, Toast.LENGTH_SHORT).show();
        } else if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            // For test only. Only allowed value is 1.0 i.e. for step taken
            stepDetector++;
//            Toast.makeText(this, "Step Detector : " + stepDetector, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
