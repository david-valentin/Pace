package com.example.davidvalentin.talaria;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    // Static member variables
    private static final String TAG = "MainActivity";
    private static final String onClickColorChange = "#1D78C6";
    private static final String onClickOGColor = "#2294F7";
    private static final int CHANNEL_ID = 1;
    private static final String DEFAULT_TIME_TEXT = "0:00";
    private static final String DEFAULT_DISTANCE_TEXT = "0.00";

    // Service Components and Threads
    private RunningTrackerService.RunningServiceBinder mRunningServiceBinder;
    private final TimerHandler mTimerHandler = new TimerHandler(this);

    // Member Variables
    private boolean isTimerRunning = false; // Checks is the timerHandler is running
    private Timer timer; // Timer object that keeps track of time
    private int elapsedTime = 0; // THe amount of time that has passed since playBtn was clicked
    private Boolean playOrPause = false; // Bool that checks whether we are playing or pausing
    private Float distanceRan;


    // UI/XML Components:
    private ImageButton stopBtn;
    private ImageButton saveBtn;
    private ImageButton startBtn;
    private ImageButton restartBtn;
    private ImageButton profileBtn = null;
    private TextView distanceText;
    private TextView timerText;
    private NotificationCompat.Builder mNotification;
    private NotificationManager mNotificationManager;
    
    // Helper Class:
    private UtilityLibrary mUtilityLibrary;

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
     *   Helped from other class mate - uses the timeFormat to format the runningTime in seconds
     *
     * */
    CallbackInterface callback = new CallbackInterface() {

        /**
         * distanceRan updates the distanceText to the currentDistance value. It calls helper methods to
         * calculate the distance in miles or kilometers
         *
         * @param currentDistance is a float that holds the total distance in meters that the user
         *                        has currently run
         *
         * */
        @Override
        public void distanceRan(final float currentDistance) {
            runOnUiThread(new Runnable() {
                @Override
            public void run() {
                    Log.d(TAG, "callback Running");
                    setDistanceRan(currentDistance); // Sets my local variable of the distance ran currently even while running
                    // Updates the text of the timer
                    distanceText = findViewById(R.id.distanceText);
                    Log.d(TAG, "Current Distance: " + currentDistance);
                    distanceText.setText(mUtilityLibrary.convertMetersToKilometers(currentDistance));
                }
            });
        }
    };


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
        // Holds my helper methods - Didn't make the methods static because need the application context
        mUtilityLibrary = new UtilityLibrary(this.getApplicationContext());

        // Declare all XML Buttons
        startBtn = findViewById(R.id.startBtn);
        profileBtn = findViewById(R.id.profileBtn);
        restartBtn = findViewById(R.id.restartBtn);
        // Disabled the button
        // restartBtn.setEnabled(false);

        // Set the values to zero
        distanceRan = new Float(0);
    }


    /**
     *  Destroys the serviceConnection by unbinding the binder object
     *
     * */
    @Override
    public void onDestroy() {
        if(serviceConnection !=null ) {
            unbindService(serviceConnection);
            serviceConnection = null;
        }
        if(mRunningServiceBinder.isServiceRunning(mRunningServiceBinder.getClass())){
            this.stopService(new Intent(this, RunningTrackerService.class));
        }
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }


    /**
     *  onPause creates a notification and creates a notification to resume the activity
     *  and the service will continue to run
     *
     * */
    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();

//        PendingIntent pIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class),0);
//        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//        // IF the service is running that is analogous that the runner is running => Check Distance?
//        // Else => Create a notification to Continue Run?
//        if (mRunningServiceBinder.isServiceRunning()) {
//            mNotification = mUtilityLibrary.createNotification("Running", "Talaria", "Current Distance: " + mUtilityLibrary.convertMetersToKilometers(getDistanceRan()), pIntent);
//            mNotificationManager.notify(CHANNEL_ID, mNotification.build());
//        } else {
//            mNotification = mUtilityLibrary.createNotification("Not Running", "Talaria", "Continue Run?", pIntent);
//            NotificationCompat.Builder mNotification = mUtilityLibrary.createNotification("Still Running", "Talaria", "Check Distance", pIntent);
//            mNotificationManager.notify(CHANNEL_ID, mNotification.build());
//        }
    }


    /**
     *  Starts the ProfileViewController Activity and loads the layout
     *
     *  @param view This is the view context of the current button
     *
     * */
    public void onClickGoToProfileView(View view) {
        Log.d(TAG, "onClickGoToProfileView");
        // The thread is doing something funky and destroying the main activity and creating a whole activity from a different thread
        Intent profileView = new Intent(this, ProfileViewController.class);
        startActivity(profileView);
    }

    /**
     *  Creates/starts the service and the timer and updates the UI Textviews
     *
     *  @param view This is the view context of the current button
     *
     * */
    private void onClickStartTimer(View view) {
        Log.d(TAG, "onClickStartTimer");
        onClickChangeBtnColor(startBtn);
        startTimer();
        if (mRunningServiceBinder == null) {
            bindAndStartService();
        } else if (this.getCallback() == null) {
            Log.d(TAG, "It is null");
        }
//        if (mRunningServiceBinder != null) {
//            Log.d(TAG, "Starting the Service");
//            // Pause the service => Service is still running and locations still listening BUT TextViews are not updating
//            mRunningServiceBinder.run();
//            Log.d(TAG, "IS BINDER ALIVE: " + mRunningServiceBinder.isBinderAlive());
//            Log.d(TAG, "CALLBACK IS NULL: " + this.getCallback());
//
//        } else if (mRunningServiceBinder == null)  {
//            Log.d(TAG, "mRunningServiceBinder is null");
//            if (this.callback == null) {
//                Log.d(TAG, "callback is null");
//            }
//
//        } else {
//            Log.d(TAG, "Service connection is not null");
//        }
    }

    /**
     *  Stops the timer from continuing but the service is still running in the background
     *
     *  @param view This is the view context of the current button
     *
     * */
    private void onClickPauseTime(View view) {
        Log.d(TAG, "onClickPause");
        onClickChangeBtnColor(startBtn);
        stopTimer();
        mRunningServiceBinder.pause();
    }

    /**
     *
     *  1. Stops/kills the timerHandler
     *  2. Stops the service
     *  3. Resets all distanceText and timerText values back to their initial values
     *
     *  @param view This is the view context of the current button
     *
     * */
    public void onClickRestart(View view) {
        Log.d(TAG, "onClickRestart");
        onClickChangeBtnColor(restartBtn);
        stopTimer();
        // Stop the service from continue tracking
        if (mRunningServiceBinder != null) {
            Log.d(TAG, "Stopping and Resetting Service Binder");
            // Stop the service binder
            mRunningServiceBinder.restart();
//            mRunningServiceBinder.getRunningTrackerService().setmLocationListener(null);
//            mRunningServiceBinder.getRunningTrackerService().setmLocationManager(null);
        }

    }

    /**
     *  Stops and kills the service and sends a query to save the current time and distance ran to the database
     *
     *  @param view This is the view context of the current button
     *
     * */
    public void onClickSaveTime(View view) {
        Log.d(TAG, "onClickSaveTime");
        saveBtn = findViewById(R.id.saveBtn);
        onClickChangeBtnColor(saveBtn);
    }

    /**
     *  Handles the logic of whether our btn calls the onClickStartTimer btn or
     *  the onClickPauseTime
     *
     *  @param view the current view that is being accessed
     *
     * */
    public void onClickRunOrPauseHandler(View view) {
        Log.d(TAG, "onClickRunOrPauseHandler");
        if (!getPlayOrPause()) { // If we aren't running => run
            Drawable pauseImg = getResources().getDrawable(R.drawable.pause_btn_96);
            startBtn.setImageDrawable(pauseImg);
            onClickStartTimer(view);
            setPlayOrPause(true);
        } else { // We are running => pause
            Drawable playImg = getResources().getDrawable(R.drawable.play_btn_96);
            startBtn.setImageDrawable(playImg);
            onClickPauseTime(view);
            setPlayOrPause(false);
        }
    }

    /**
     *  Binds and starts the service to the main activity
     * */
    public void bindAndStartService() {
        Log.d(TAG, "bindAndStartService");
        this.startService(new Intent(this, RunningTrackerService.class));
        this.bindService(new Intent(this, RunningTrackerService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     *  Binds and starts the service to the main activity
     * */
    public void unbindAndStopService() {
        Log.d(TAG, "bindAndStartService");
        this.stopService(new Intent(this, RunningTrackerService.class));
        this.unbindService(serviceConnection);
    }


    /**
     *  The handler class which updates the textview of the timerText
     *
     * */
    private static class TimerHandler extends Handler {

        private static final String TAG = "TimerHandler";
        private final WeakReference<MainActivity> mainActivity; // A weak reference to the main activity is declared so that it can be properly garbage collected

        public TimerHandler(MainActivity activity) {
            Log.d(TAG, "onCreate");
            mainActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "handleMessage");
            MainActivity activity = mainActivity.get();
            try {
                activity.timerText = (TextView) activity.findViewById(R.id.timerText);
                activity.timerText.setText(activity.timeFormat(activity.getElapsedTime())); //this is the textview
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }

        }
    }

    /**
     *  startTimer creates a new Timer object
     *      1. if the isTimer is true
     *          a. runs the timer object which increments the global variable elapsed time
     *             by 1, and it also disables the startBtn to false
     *      2. if the isTimer is false
     *          a. it cancels the timer in the run method
     *
     * */
    private void startTimer() {
        Log.d(TAG, "startTimer");
        timer = new Timer("timer", true);
        if (isTimerRunning() == false) {
            setTimerRunning(true);
            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    if (isTimerRunning()) {
                        Log.d(TAG, "Timer is running.");
                        elapsedTime += 1; //increase every sec
                        mTimerHandler.obtainMessage(1).sendToTarget();
                    } else {
                        Log.d(TAG, "Timer is cancelled.");
                        timer.cancel();
                    }
                }
            }, 0, 1000);
        } else if (!isTimerRunning()) {
            Toast alertToast = mUtilityLibrary.createToast("Timer is already running. Click Stop to stop the timer", 1);
            alertToast.show();
        }
    }

    /**
     *  killtimer re-enables the startBtn component and isTimerRunning to false
     *  otherwise, it creates a toast alert that timer has already been clicked.
     *
     * */
    private void killTimer() {
        Log.d(TAG, "killTimer");

        if (!isTimerRunning()) {
            Log.d(TAG, "Killed Timer");

            timer.purge();
            timer.cancel();
            setElapsedTime(0);
            mRunningServiceBinder.restart();
//            mTimerHandler.removeCallbacks(null);
//            mTimerHandler.removeCallbacksAndMessages(null);
            // Reset the Text Views
            distanceText = findViewById(R.id.distanceText);
            distanceText.setText(DEFAULT_DISTANCE_TEXT);
            timerText = findViewById(R.id.timerText);
            timerText.setText(DEFAULT_TIME_TEXT);
        } else { // Timer is running => Tell the user to stop the timer first
            Toast alertToast = mUtilityLibrary.createToast("Timer must be paused before restarting.", 1);
            alertToast.show();
        }
    }


    /**
    *  stopTimer re-enables the startBtn component and isTimerRunning to false
    *  otherwise, it creates a toast alert that timer has already been clicked.
    *
    * */
    private void stopTimer() {
        Log.d(TAG, "stopTimer");
        if (isTimerRunning()) {
            Log.d(TAG, "Timer Stopped");
            setTimerRunning(false);
        } else {
            Log.d(TAG, "Timer Running");
            Toast alertToast = mUtilityLibrary.createToast("Timer is already paused. Click Start to resume", 1);
            alertToast.show();
        }
    }

    /**
     *  timeFormat takes an int variable and converts the int properly into a string
     *  that represents the number of seconds passed
     *
     * @return String the strings represents the second passed i.e. start = "0:40"
     * @param seconds is an int value that is passed from an internal handler function
     *
     * */
    private String timeFormat(int seconds) {
        try {
            if (seconds == -1){
                return DEFAULT_TIME_TEXT;
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
            return DEFAULT_TIME_TEXT;
        }
    }

    /**
     * Creates a Handler which changes background color of the the btn param when clicked
     * for less than 250 ms
     *
     * @param btn is the btn whose color will be changed onClick
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
                    //Log.d(TAG, "Handler still running");
                    handler.postDelayed(this, 0);
                } else {
                    //Log.d(TAG, "Handler is dead");
                    btn.setBackgroundColor(Color.parseColor(onClickOGColor));

                }
            }
        }, 0);
    }

    /**
    *
    *   Getters and Setters for MainActivity
    *
    * */


    public CallbackInterface getCallback() {
        return callback;
    }

    public boolean isTimerRunning() {
        return isTimerRunning;
    }

    public void setTimerRunning(boolean timerRunning) { isTimerRunning = timerRunning; }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(int elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public Boolean getPlayOrPause() {
        return playOrPause;
    }

    public void setPlayOrPause(Boolean playOrPause) {
        this.playOrPause = playOrPause;
    }

    public Float getDistanceRan() {
        return distanceRan;
    }

    public void setDistanceRan(Float distanceRan) {
        this.distanceRan = distanceRan;
    }



}
