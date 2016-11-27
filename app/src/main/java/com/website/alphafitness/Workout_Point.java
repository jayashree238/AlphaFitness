package com.website.alphafitness;

/**
 * Created by jayashreemadhanraj on 10/20/16.
 */

public class Workout_Point {

    int id;
    int workoutID;
    double latitude;
    double longitude;
    long currentTime;

    public Workout_Point(int workoutID, double latitude, double longitude) {
        currentTime = System.currentTimeMillis();
        this.latitude = latitude;
        this.longitude = longitude;
        this.workoutID = workoutID;
    }

    public void Workout_Point(){

    }

    public int getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public int getWorkoutID() {
        return workoutID;
    }
}

