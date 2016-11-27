package com.website.alphafitness;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

public class RecordWorkout extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_workout);

    }

    public void goToUserProfile (View v){
        Intent userIntent = new Intent(RecordWorkout.this, User_Profile.class);
        startActivity(userIntent);
    }


}
