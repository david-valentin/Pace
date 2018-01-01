package com.example.davidvalentin.talaria;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String onClickColorChange = "#1D78C6";
    private static final String onClickOGColor = "#2294F7";
    private static final int CHANNEL_ID = 1;
    private static int elapsedTime = 0;

    // Service Components
    private RunningTrackerService.RunningServiceBinder mRunningServiceBinder = null;

    // Logical Member Variables
    private boolean isTimerRunning = false;
    private Timer timer;


    // UI/XML  Components:
    private ImageButton stopBtn = null;
    private ImageButton saveBtn = null;
    private ImageButton startBtn = null;
    private ImageButton profileBtn = null;
    private TextView distanceText;
    private TextView timerText;


    /**
     * starts the service and binds the service to the current Context
     *
     * @param savedInstanceState is the bundle from the Activity
     *
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.startService(new Intent(this, RunningTrackerService.class));
        this.bindService(new Intent(this, RunningTrackerService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }


    /**
     *  Destroys the serviceConnection by unbinding the binder object
     * */
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        if(serviceConnection!=null) {
            unbindService(serviceConnection);
            serviceConnection = null;
        }
        super.onDestroy();
    }


    /**
     *  onPause creates a notification and alerts the notification manager
     *
     * */
    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        Notification mNotification = createNotification("Still Running", "Talaria");
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(CHANNEL_ID, mNotification);
        super.onPause();
    }


    /**
     *  Starts the ProfileViewController Activity and loads the layout
     *
     *  @param view This is the view context of the current button
     * */
    public void onClickGoToProfileView(View view) {
        Log.d(TAG, "onClickGoToProfileView");
        // The thread is doing something funky and destroying the main activity and creating a whole activity from a different thread
        // profileBtn = findViewById(R.id.profileBtn);
        // onClickChangeBtnColor(profileBtn);
        Intent profileView = new Intent(this, ProfileViewController.class);
        startActivity(profileView);
    }

    /**
     *  Creates/starts the service and the timer and updates the UI Textviews
     *
     *  @param view This is the view context of the current button
     *
     * */
    public void onClickStartTimer(View view) {
        Log.d(TAG, "onClickStartTimer");
        startBtn = findViewById(R.id.startBtn);
        onClickChangeBtnColor(startBtn);
        startBtn.setEnabled(false); // Had to disable the button because I continued to spawn new timer objects
        startTimer();

//        if (mRunningServiceBinder != null) {
//            mRunningServiceBinder.run();
//        } else {
//            Log.d(TAG, "Its still null");
//        }
        // Start the timer and start the service
//        Log.d(TAG, "YES? " + mRunningServiceBinder.isRunning());

    }

    /**
     *  Creates the handler object which handleMessage updates the textview of the timerText
     *
     * */
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            timerText = (TextView) findViewById(R.id.timerText);
            timerText.setText(timeFormat(elapsedTime)); //this is the textview
        }
    };

    /**
     *  startTimer creates a new Timer object
     *      1. if the isTimer is true
     *          a. runs the timer object which increments the global variable elapsed time
     *             by 1, and it also disables the startBtn to false
     *      2. if the isTimer is false
     *          a. it cancels the timer in the run method
     *
     * */
    protected void startTimer() {
        Log.d(TAG, "startTimer");
        timer = new Timer("timer", true);

        if (isTimerRunning() == false) {
            setTimerRunning(true);
            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    if (isTimerRunning()) {
                        Log.d(TAG, "Timer is running.");
                        elapsedTime += 1; //increase every sec
                        mHandler.obtainMessage(1).sendToTarget();
                    } else {
                        Log.d(TAG, "Timer is cancelled.");
                        timer.cancel();
                    }

                }
            }, 0, 1000);
        } else if (isTimerRunning() == false && startBtn.isEnabled() == false) {
            Toast alertToast = createToast("Timer is already running. Click Stop to stop the timer");
            alertToast.show();
        }


    }


    /*
    *  stopTimer re-enables the startBtn component and isTimerRunning to false
    *  otherwise, it creates a toast alert that timer has already been clicked.
    *
    * */
    protected void stopTimer() {

        if (isTimerRunning()) {
            Log.d(TAG, "stopTimer");
            setTimerRunning(false);
            startBtn.setEnabled(true);
        } else {
            Toast alertToast = createToast("Timer is already stopped. Click Start to resume the timer.");
            alertToast.show();
        }

    }


    /**
     *  Stops and kills the service and sends a query to save the current time and distance ran to the database
     *
     *  @param view This is the view context of the current button
     * */
    public void onClickSaveTime(View view) {
        Log.d(TAG, "onClickSaveTime");
        saveBtn = findViewById(R.id.saveBtn);
        onClickChangeBtnColor(saveBtn);
    }

    /**
     *  When clicked, stops the timer
     *
     *  @param view This is the view context of the current button
     * */
    public void onClickStopTimer(View view) {
        Log.d(TAG, "onClickStopTimer");
        stopBtn = findViewById(R.id.stopBtn);
        onClickChangeBtnColor(stopBtn);
        stopTimer();
        // Stopping the service
    }

    /*
    *   Helped from other class mate - uses the timeFormat to format the song in minutes properly
    *   and grabs the maxDuration
    *   Src: https://stackoverflow.com/questions/25796237/how-to-display-song-duration
    * */
    CallbackInterface callback = new CallbackInterface() {
        @Override
        public void distanceRan(final long currentDistance) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "callback Running");
                    // Updates the text of the timer
                    distanceText = (TextView) findViewById(R.id.timerText);
//                    Log.d(TAG, "Current Distance " + currentDistance);
//                    distanceText.setText((int) currentDistance);
                }
            });
        }
    };

    /**
     *  Private member variable that initializes the Service connection object
     *
     * */
    private ServiceConnection serviceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // Once the service is connected to the UI of the main activity
            Log.d(TAG, "onServiceConnected");
            mRunningServiceBinder = (RunningTrackerService.RunningServiceBinder) service;
            mRunningServiceBinder.registerCallback(callback);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            // Decouple the service and unregister the callback from it
            mRunningServiceBinder.unregisterCallback(callback);
            mRunningServiceBinder = null;
        }
    };


    /**
     *  PUT IN UTIL FUNCTIONS CLASS?
     *
     *  Help from Justin Li
     *  Src: https://stackoverflow.com/questions/27954303/how-to-format-a-songs-duration-time-into-minutes-and-seconds
     *
     * */
    private String timeFormat(int seconds) {

        String start = "0:00";
        // If its less
        try {
            if (seconds == -1){
                return start;
            }
            int minute = seconds / 60;
            // Modulo
            int remainingSeconds = seconds % 60;
            if (remainingSeconds < 10) {
                // Grabs the remaining value and adds ito the end
                return String.valueOf(minute) + ":0" + String.valueOf(remainingSeconds);
            } else {
                // Otherwise return the whole value
                return String.valueOf(minute) + ":" + String.valueOf(remainingSeconds);
            }
        } catch (Exception e) {
            return start;
        }

    }

    /**
     * Creates and returns a toast object
     *
     * @param msg - the msg string is the content associated with the toast
     * @return Toast - the toast object returned
     * */
    public Toast createToast(String msg) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, msg, duration);
        return toast;
    }

    /**
     * Creates and returns a notification object
     *
     * @param msg the msg string is the ContentText associated with the Notification
     * @param title the title string is the ContentTitle associated with the Notification
     * @return Notification the notification object
     *
     * */
    public Notification createNotification(String msg, String title) {

        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        Resources r = getResources();
        Notification notification = new android.support.v4.app.NotificationCompat.Builder(this)
                .setTicker(("Talaria"))
                .setSmallIcon(R.drawable.talaria_logo_96)
                .setContentTitle(title)
                .setContentText(msg)
                .setContentIntent(pi)
                .setAutoCancel(false)
                .build();
        return notification;

    }

    /**
     * PUT IN UTIL FUNCTIONS CLASS?
     * onClickChangeBtnColor
     *
     * */
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

    /*
    *
    *   Getters and Setters
    *
    * */

    public boolean isTimerRunning() {
        return isTimerRunning;
    }

    public void setTimerRunning(boolean timerRunning) {
        isTimerRunning = timerRunning;
    }

    public static int getElapsedTime() {
        return elapsedTime;
    }

    public static void setElapsedTime(int elapsedTime) {
        MainActivity.elapsedTime = elapsedTime;
    }

}
