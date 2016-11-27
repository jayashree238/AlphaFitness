package com.website.alphafitness;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by jayashreemadhanraj on 10/20/16.
 */

public class Workout {

    int id;
    long timeStamp;
    float distance;
    long duration;

    public Workout() {
        timeStamp = System.currentTimeMillis();
    }

    public int getId() {
        return id;
    }

    public long getTimeStamp(){
        return timeStamp;
    }

}
