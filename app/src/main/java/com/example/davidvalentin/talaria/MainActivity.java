package com.example.davidvalentin.talaria;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     *  onClickGoToProfileView
     *      1. Starts the ProfileViewController Activity and loads the layout
     * */
    public void onClickGoToProfileView(View view) {
        Log.d(TAG, "onClickGoToProfileView");

        Intent profileView = new Intent(this, ProfileViewController.class);
        startActivity(profileView);

    }

    /**
     *  onClickStartTimer
     *      1. Starts the service and updates the text for the
     *          a. timer
     *          b. miles ran
     * */
    public void onClickStartTimer(View view) {
        Log.d(TAG, "onClickStartTimer");

    }

    public void onClickSaveTime(View view) {
        Log.d(TAG, "onClickSaveTime");

    }

    public void onClickStopTimer(View view) {
        Log.d(TAG, "onClickStopTimer");
    }
}
