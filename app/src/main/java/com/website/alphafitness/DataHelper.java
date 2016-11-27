package com.website.alphafitness;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jayashreemadhanraj on 10/13/16.
 */

public class DataHelper extends SQLiteOpenHelper{
    private static final String LOG_TAG = DataHelper.class.getName();
    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "alphaFitnessDatabase";

    //Table Names
    private static final String TABLE_USER = "user";
    private static final String TABLE_WORKOUT_POINT = "workoutPoint";
    private static final String TABLE_WORKOUT = "workout";
    private static final String TABLE_CHART = "chart";

    //Table Creation Syntax
    private static final String TABLE_USER_CREATE =
            "CREATE TABLE " + TABLE_USER + " (_ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, WEIGHT REAL, HEIGHT REAL, DISTANCE REAL, DURATION REAL, CALORIES REAL);";
    private static final String TABLE_WORKOUT_CREATE =
            "CREATE TABLE " + TABLE_WORKOUT + " (_ID INTEGER PRIMARY KEY AUTOINCREMENT, TIMESTAMP INTEGER, DISTANCE INTEGER, DURATION REAL);";
    private static final String TABLE_WORKOUT_POINT_CREATE =
            "CREATE TABLE " + TABLE_WORKOUT_POINT + " (_ID INTEGER PRIMARY KEY, WORKOUT_ID INTEGER, LATITUDE REAL, LONGITUDE REAL, CURRENTTIME INTEGER, FOREIGN KEY(WORKOUT_ID) REFERENCES " + TABLE_WORKOUT +"(ID));";
    private static final String TABLE_CHART_CREATE =
            "CREATE TABLE " + TABLE_CHART + " (_ID INTEGER PRIMARY KEY, DISTANCE INTEGER, CALORIES REAL, DURATION REAL);";

    DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.i(LOG_TAG, "DataHelper constructor");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_USER_CREATE);
        db.execSQL(TABLE_WORKOUT_POINT_CREATE);
        db.execSQL(TABLE_WORKOUT_CREATE);
        db.execSQL(TABLE_CHART_CREATE);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public Boolean createfirstUser() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USER, null);
        c.moveToFirst();
        if (c.getCount() == 0){
            String update = "INSERT INTO " + TABLE_USER + " (NAME, WEIGHT, HEIGHT, DISTANCE, DURATION, CALORIES) VALUES ('Jay', 60.0, 167.0, 0.0, 0.0, 0.0)";
            db.execSQL(update);
            return true;
        }
        return false;
    }

    public int createWorkout(){
        Log.v(LOG_TAG, "Inside createWorkout Function");
        SQLiteDatabase db = getWritableDatabase();
        String insert = "INSERT INTO " + TABLE_WORKOUT + " (TIMESTAMP, DISTANCE, DURATION) VALUES (0, 0, 0)";
        db.execSQL(insert);
        Cursor c = db.rawQuery("SELECT _ID FROM " + TABLE_WORKOUT, null);
        c.moveToLast();
        db.close();
        return c.getInt(0);
    }

    public String addWorkoutPoint(Workout_Point workoutPoint){
        SQLiteDatabase db = getWritableDatabase();
        String insert = "INSERT INTO " + TABLE_WORKOUT_POINT + " (WORKOUT_ID, LATITUDE, LONGITUDE, CURRENTTIME) VALUES (" + workoutPoint.getWorkoutID() + ", " + workoutPoint.getLatitude() + ", " + workoutPoint.getLongitude() + ", " + workoutPoint.getCurrentTime() + ");";
        db.execSQL(insert);
        return "Successfully Updated WorkoutPoint(" + workoutPoint.getWorkoutID() + ", " + workoutPoint.getLatitude() + ", " + workoutPoint.getLongitude() + ", " + workoutPoint.getCurrentTime() + ")";
    }

    public ArrayList<LatLng> getLastWorkoutPath(){
        ArrayList<LatLng> result = new ArrayList<LatLng>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cWorkout = db.rawQuery("SELECT _ID FROM " + TABLE_WORKOUT, null);
        cWorkout.moveToLast();
        int lastWorkoutID = cWorkout.getInt(0);

        Cursor cWorkoutPoint = db.rawQuery("SELECT * FROM " + TABLE_WORKOUT_POINT + " WHERE WORKOUT_ID = " + lastWorkoutID, null);
        cWorkoutPoint.moveToFirst();
        for(int i = 0; i < cWorkoutPoint.getCount(); i++){
            cWorkoutPoint.moveToPosition(i);
            result.add(new LatLng(cWorkoutPoint.getDouble(2), cWorkoutPoint.getDouble(3)));
        }
        return result;
    }

    public double updateLastWorkout(long timeStamp, String workoutDuration, double distance){
        String array[] = workoutDuration.split(":");
        double stoppedMilliseconds = 0.0;
        if (array.length == 2) {
            stoppedMilliseconds = Double.parseDouble(Integer.parseInt(array[0]) * 60 * 1000 + "." +Integer.parseInt(array[1]) * 1000);
        } else if (array.length == 3) {
            stoppedMilliseconds = Double.parseDouble(Integer.parseInt(array[0]) * 60 * 60 * 1000
                    +"." +  Integer.parseInt(array[1]) * 60 * 1000 +"."
                    + Integer.parseInt(array[2]) * 1000);
        }

        SQLiteDatabase db = getWritableDatabase();
        Cursor cWorkout = db.rawQuery("SELECT _ID FROM " + TABLE_WORKOUT, null);
        cWorkout.moveToLast();
        int lastWorkoutID = cWorkout.getInt(0);

        double duration = stoppedMilliseconds;
      Log.v(LOG_TAG, "duration: " + duration);
        String update = "UPDATE " + TABLE_WORKOUT + " SET TIMESTAMP = " + timeStamp + ", DISTANCE = " + distance + ", DURATION = " + duration + " WHERE _ID = " + lastWorkoutID + ";";
        db.execSQL(update);
        db.close();
        return distance;
    }

    //Calculation for User Profile Page


    public void updateUserTable(String workoutDuration, double distance, double calories){
        String array[] = workoutDuration.split(":");
        double stoppedMilliseconds = 0.0;
        if (array.length == 2) {
            stoppedMilliseconds = Double.parseDouble(Integer.parseInt(array[0]) * 60 * 1000 + "." +Integer.parseInt(array[1]) * 1000);
        } else if (array.length == 3) {
            stoppedMilliseconds = Double.parseDouble(Integer.parseInt(array[0]) * 60 * 60 * 1000
                    +"." +  Integer.parseInt(array[1]) * 60 * 1000 +"."
                    + Integer.parseInt(array[2]) * 1000);
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USER, null);
        c.moveToLast();
       if(c.getCount() == 0){
           String insert = "INSERT INTO " + TABLE_USER + " (NAME, WEIGHT, HEIGHT, DISTANCE, DURATION, CALORIES) VALUES ('Jay', 60.0, 167.0, "+ distance + ", " + stoppedMilliseconds + ", " + calories + ");";
           db.execSQL(insert);

       }
        else {
           String lastUser = c.getString(1);
           String update = "UPDATE " + TABLE_USER + " SET DISTANCE = " + distance + ", DURATION = " + stoppedMilliseconds + ", CALORIES = " + calories + " WHERE NAME = '" + lastUser + "';";
           db.execSQL(update);
       }

    }

    public String getName() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USER, null);
        c.moveToLast();
        for (int i = 0; i < c.getCount(); i++) {
            c.moveToPosition(i);
//            Log.v(LOG_TAG, "Name" + i + ": " + c.getString(1));
        }
        return c.getString(1);
    }

    public double getHeight() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USER, null);
        c.moveToLast();
        for (int i = 0; i < c.getCount(); i++) {
            c.moveToPosition(i);
//            Log.v(LOG_TAG, "Height" + i + ": " + c.getDouble(3));
        }
        return c.getDouble(3);
    }

    public double getWeight() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USER, null);
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            c.moveToPosition(i);
//            Log.v(LOG_TAG, "Weight" + i + ": " + c.getDouble(2));
        }
        return c.getDouble(2);
    }

/** For User_Profile Activity **/
    public Boolean addToUserTableLastUser(double distance, double calories, double stoppedMilliseconds){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT NAME FROM " + TABLE_USER, null);
        if(c.getCount() != 0) {
            c.moveToLast();
            String lastUser = c.getString(0);
            String update = "UPDATE " + TABLE_USER + " SET DISTANCE = " + distance + ", DURATION = " + stoppedMilliseconds + ", CALORIES = " + calories + " WHERE NAME = '" + lastUser + "';";
            db.execSQL(update);
            return true;
        }
        return false;
    }

    //Calculate all time duration

    public String calcTotalDuration(String currentUser){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(DURATION) FROM " + TABLE_USER + " WHERE NAME = '" + currentUser + "'", null);
        c.moveToFirst();
        double milliseconds = c.getInt(0);
        return Double.toString(milliseconds) + " mins";
    }
    public String calcWeeklyDuration(String currentUser){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT AVG(DURATION) FROM " + TABLE_USER + " WHERE NAME = '" + currentUser + "'", null);
        c.moveToFirst();
        double milliseconds = c.getInt(0);
        return Double.toString(milliseconds) + " mins";
  }

    public double calcTotalDistance(String currentUser){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(DISTANCE) FROM " + TABLE_USER + " WHERE NAME = '" + currentUser + "'", null);
        c.moveToFirst();
        double totalDistance = (double) c.getInt(0);
        return totalDistance;
    }

    public double calcTotalCalories(String currentUser){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(CALORIES) FROM " + TABLE_USER + " WHERE NAME = '" + currentUser + "'", null);
        c.moveToFirst();
        double calories = (double) c.getInt(0);
        return calories;
    }

    public double calcWeeklyCalories(String currentUser){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT AVG(CALORIES) FROM " + TABLE_USER + " WHERE NAME = '" + currentUser + "'", null);
        c.moveToFirst();
        double calories = (double) c.getInt(0);
        return calories;
    }


    public String calcMinimum(){ //getting the minimum session duration
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT MIN(DURATION) FROM " + TABLE_CHART, null);
        c.moveToFirst();
        String milliseconds = Double.toString(c.getDouble(0));
        return milliseconds;
    }



    public String totalWorkouts(String currentUser){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USER + " WHERE NAME = '" + currentUser + "'",null);
        c.moveToFirst();
        int noOfWorkout = c.getInt(0);
        return Integer.toString(noOfWorkout);
    }

    public String weeklyWorkouts(String currentUser){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USER + " WHERE NAME = '" + currentUser + "'",null);
        c.moveToFirst();
        int noOfWorkout = c.getInt(0)/7;
        return Integer.toString(noOfWorkout);
    }

    //User Profile
    public void updateUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        String newUser = "INSERT INTO " + TABLE_USER + "(NAME, WEIGHT, HEIGHT, DISTANCE, DURATION, CALORIES) VALUES ('" + user.getName() + "', " + user.getWeight() + ", " + user.getHeight() +", 0.0, 0.0, 0.0)";
         db.execSQL(newUser);
        db.close();
        Log.v(LOG_TAG, "Added new User: " + user.getName());
    }

    //Get user Weight
    public double getLastUserWeight(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT WEIGHT FROM " + TABLE_USER, null);
        c.moveToLast();
        if(c.getCount()>0) {
            return c.getDouble(0);
        }
        else
            return 167.0;
    }

    /** Chart Purposes **/

    public void addToChart(int distance, double calories, double duration){
        SQLiteDatabase db = this.getWritableDatabase();
        String update = "INSERT INTO " + TABLE_CHART + " (DISTANCE, CALORIES, DURATION) VALUES (" + distance + ", " + calories + ", " + duration + ")";
        db.execSQL(update);
    }

    public List<Integer> readDistanceForChart(){
        SQLiteDatabase db = this.getWritableDatabase();
        List<Integer> distance = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CHART, null);
        if(c != null && c.moveToFirst()){
            for(int i=0; i <c.getCount(); i++){
                c.moveToPosition(i);
//                Log.v(LOG_TAG, " distances: " + c.getDouble(1));
                distance.add(c.getInt(1));
            }

        }
        return distance;
    }

    public List<Double> readCalorieForChart(){
        SQLiteDatabase db = this.getWritableDatabase();
        List<Double> calorie = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CHART, null);
        if(c != null && c.moveToFirst()){
            for(int i=0; i <c.getCount(); i++){
                c.moveToPosition(i);
//                Log.v(LOG_TAG, " calories: " + c.getDouble(2));
                calorie.add(c.getDouble(2));
            }

        }
        return calorie;
    }

    public void emptyChart(){
        SQLiteDatabase db = this.getWritableDatabase();
        String delete = "DELETE FROM " + TABLE_CHART;
        db.execSQL(delete);
    }

    public String calcMaximum(){ //getting the maximum session duration
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT MAX(DURATION) FROM " + TABLE_CHART, null);
        c.moveToFirst();
        Log.v(LOG_TAG, "calcMax: " + c.getDouble(0));
        String milliseconds = Double.toString(c.getDouble(0));
        return milliseconds;

    }

    public String calcAverage(){ //getting the average session duration
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT AVG(DURATION) FROM " + TABLE_CHART, null);
        c.moveToFirst();
        Log.v(LOG_TAG, "calcAverage: " + c.getDouble(0));
        String milliseconds = Double.toString(c.getDouble(0));
        return milliseconds;
    }
}
