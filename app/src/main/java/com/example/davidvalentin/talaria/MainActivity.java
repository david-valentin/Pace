package com.example.davidvalentin.talaria;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String onClickColorChange = "#1D78C6";
    private static final String onClickOGColor = "#2294F7";


    // UI Components:
    private ImageButton stopBtn = null;
    private ImageButton saveBtn = null;
    private ImageButton startBtn = null;
    private ImageButton profileBtn = null;




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
        // The thread is doing something funky and destroying the main activity and creating a whole activity from a different thread
//        profileBtn = findViewById(R.id.profileBtn);
//        onClickChangeBtnColor(profileBtn);
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
        startBtn = findViewById(R.id.startBtn);
        onClickChangeBtnColor(startBtn);
    }

    /**
     *  onClickSaveTime
     *      1. Stops and saves the current time to the database
     * */
    public void onClickSaveTime(View view) {
        Log.d(TAG, "onClickSaveTime");
        saveBtn = findViewById(R.id.saveBtn);
        onClickChangeBtnColor(saveBtn);
    }

    /**
     *  onClickStopTimer
     *      1. Stops the current timer from running and the tracking
     *
     * */
    public void onClickStopTimer(View view) {
        stopBtn = findViewById(R.id.stopBtn);
        onClickChangeBtnColor(stopBtn);
        Log.d(TAG, "onClickStopTimer");
    }

    public void onClickChangeBtnColor(final ImageButton btn) {
        Log.d(TAG, "onClickChangeBtnColor");
        final long startTime = System.currentTimeMillis();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            public void run() {
                btn.setBackgroundColor(Color.parseColor(onClickColorChange));
                // Keep running the thread till the the time reaches zero
                if (System.currentTimeMillis() - startTime < 250) {
                    Log.d(TAG, "Handler still running");
                    handler.postDelayed(this, 0);
                } else {
                    Log.d(TAG, "Handler is dead");
                    btn.setBackgroundColor(Color.parseColor(onClickOGColor));
                }
            }
        }, 0);



    }
}
