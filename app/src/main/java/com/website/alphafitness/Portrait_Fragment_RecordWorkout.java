package com.website.alphafitness;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.google.android.gms.common.api.GoogleApiClient;
import android.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.attr.data;
import static android.R.attr.name;
import static android.R.id.list;
import static android.content.Context.DOWNLOAD_SERVICE;
import static android.content.Context.LOCATION_SERVICE;
import static com.website.alphafitness.R.id.weight;
import static com.website.alphafitness.R.string.distance;
import static com.website.alphafitness.R.string.duration;

/**
 * Created by jayashreemadhanraj on 9/30/16.
 */

public class Portrait_Fragment_RecordWorkout extends Fragment implements LocationListener, Serializable, GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, CompoundButton.OnCheckedChangeListener{
    private static final String LOG_TAG = "RecordWorkout";
    private Chronometer duration;
    private int durationSetUp = 0;
    private GoogleMap mMap;
    private static final int PERMISSION_LOCATION_REQUEST_CODE = 1;
    LocationManager locationManager;
    DataHelper dataHelper;
    private TextView textDistance;
    private Polyline line = null;
    double distance;
    int stepCounter;
    double weight;
    double calories;
    double stoppedMilliseconds = 0.0;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra("Step_Count")) {
                stepCounter = intent.getIntExtra("Step_Count", 0);
                 weight = dataHelper.getLastUserWeight();
                calories = ((weight * 28)/100000) * stepCounter;
                distance = (28.2 * stepCounter)/100;
                String workoutDuration = duration.getText().toString();
                String array[] = workoutDuration.split(":");

                if (array.length == 2) {
                    stoppedMilliseconds = Double.parseDouble(Integer.parseInt(array[0]) * 60 * 1000 + "." +Integer.parseInt(array[1]) * 1000);
                } else if (array.length == 3) {
                    stoppedMilliseconds = Double.parseDouble(Integer.parseInt(array[0]) * 60 * 60 * 1000
                            +"." +  Integer.parseInt(array[1]) * 60 * 1000 +"."
                            + Integer.parseInt(array[2]) * 1000);
                }

                Log.v(LOG_TAG, "StoppedMilliseconds: " + stoppedMilliseconds);


                //Update Current User's Data.
                if(dataHelper.addToUserTableLastUser(distance, calories, stoppedMilliseconds)){
                    Log.v(LOG_TAG, "Update User Table with New Distance: " + distance + " calories: " + calories + " stoppedMilliseconds: " + stoppedMilliseconds);
                }

                textDistance.setText(Double.toString(distance));
                if(dataHelper.getLastWorkoutPath().size() > 0) {
                    createPolyLine();
                }
            }

        }
    };


    class SayHello extends TimerTask {
        public void run() {
            callFunction();
        }

        public void callFunction(){
            dataHelper.addToChart(stepCounter, calories, stoppedMilliseconds);
            Log.v(LOG_TAG, "added to chart");
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("record_workout_step_count");
        getActivity().registerReceiver(receiver,intentFilter);
    }


    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(durationSetUp >0) {
            outState.putLong("duration", duration.getBase());
            outState.putInt("durationSetUp", durationSetUp);
            outState.putDouble("distanceFragment", Double.parseDouble(textDistance.getText().toString()));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showPermissionDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView =  inflater.inflate(R.layout.fragment_portait_layout,container,false);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dataHelper = new DataHelper(getContext());
        duration = (Chronometer) getActivity().findViewById(R.id.duration_chrono);
        textDistance = (TextView) getActivity().findViewById(R.id.distance_chrono);
        if(savedInstanceState != null && savedInstanceState.getInt("durationSetUp") > 0) {
            duration.setBase(savedInstanceState.getLong("duration"));
            durationSetUp = savedInstanceState.getInt("durationSetUp");
            textDistance.setText(Double.toString(savedInstanceState.getDouble("distanceFragment")));

        }
        ToggleButton startStop = (ToggleButton) getActivity().findViewById(R.id.toggleButton);

        startStop.setOnCheckedChangeListener(this);
//        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
        showPermissionDialog();
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            duration.start();
            if(durationSetUp<1) {

                startWorkoutService();
                Log.v(LOG_TAG, "Toggle is checked" + buttonView.getText());
                duration.setBase(SystemClock.elapsedRealtime());
                durationSetUp += 1;
                textDistance.setText("0.0");
                mMap.clear();
                SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
                //Add stepCounter Values and Calories only Every 5 minutes
                Timer timer = new Timer();
                timer.schedule(new SayHello(), 0, 300000);

            }

        }
        else{
            getActivity().stopService(new Intent(getActivity(), LogWorkout.class));
            Log.v(LOG_TAG, "Toggle is unchecked" + buttonView.getText());
            duration.stop();
            durationSetUp = 0;

            String workoutDuration = duration.getText().toString();

           long workoutDuration2 = SystemClock.elapsedRealtime() - duration.getBase(); //Get duration of workout
            if(stepCounter > 0) {
//                distance = (weight * 28) / 100000;
//                distance = distance * stepCounter;
                distance = 28.2 * stepCounter /100;
            }
            dataHelper.updateLastWorkout(workoutDuration2, workoutDuration, distance);
            dataHelper.updateUserTable(workoutDuration, distance, calories);
            createPolyLine();  //Draw line in map for path
            dataHelper.emptyChart();
        }
    }

    private void startWorkoutService() {
        getActivity().startService(new Intent(getActivity(), LogWorkout.class));
    }



    private void createPolyLine() {
        ArrayList<LatLng> path = dataHelper.getLastWorkoutPath();
//        line = mMap.addPolyline(new PolylineOptions().addAll(path).width(5).color(Color.RED));
        LatLng[] path2 = path.toArray(new LatLng[path.size()]);
        line = mMap.addPolyline(new PolylineOptions().add(path2).width(5).color(Color.RED));
        if(path2.length != 0){
            LatLngBounds.Builder builder = LatLngBounds.builder();
            for (LatLng point:path2) {
                builder.include(point);
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String locationProvider = locationManager.getBestProvider(criteria, true);
        // LatLng loc =  new LatLng(location.getLatitude(), location.getLongitude());
        Location location = locationManager.getLastKnownLocation(locationProvider);
        LatLng loc = new LatLng(-31, 152);
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.v(LOG_TAG, "Permission is present");
            googleMap.setMyLocationEnabled(true);

            if(location != null){
                loc = new LatLng(location.getLatitude(), location.getLongitude());
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
            googleMap.addMarker(new MarkerOptions().title("You Are Here").snippet("Current Location").position(loc));

        }

    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void showPermissionDialog() {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_LOCATION_REQUEST_CODE);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
