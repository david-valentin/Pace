package com.example.davidvalentin.talaria;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.RemoteCallbackList;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Optional;


/**
 *
 * RunningTrackerService is a remote service that tracks the gps coordinates
 * of the user and runs a thread and uses a callback to communicate
 * the location of the user to binded activity
 *
 * Created by davidvalentin on 12/26/17.
 *
 */
public class RunningTrackerService extends Service {

    // Member variables
    private static final String TAG = "RunningTrackerService";
    private static final int CHANNEL_ID = 0;
    private static final MainActivity mainActivity = new MainActivity();

    // Callback for the thread
    RemoteCallbackList<RunningServiceBinder> remoteCallbackList = new RemoteCallbackList<RunningServiceBinder>();

    // Member Variables to resume the service
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotification;

    // Instance of the thread class
    private RunnerThread mRunnerThread;

    // Runner class - instantiate it with the Service Context
    public Runner runner;

    protected LocationManager mLocationManager;
    protected LocationListener mLocationListener;


    /**
     * Creates the activity and creates the runner thread which
     *  starts automatically
     * */
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        Log.d(TAG, "onCreate");
        super.onCreate();

        // Instantiate the new runner object
        runner = new Runner(this);



        // Created the notification
//        PendingIntent pIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class),0);
//        mNotification = new NotificationCompat.Builder(this);
//        mNotification.setContentTitle("Talaria");
//        mNotification.setSmallIcon(R.drawable.talaria_logo_96);
//        mNotification.setContentIntent(pIntent);
//        mNotification.build();
//
//        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.notify(CHANNEL_ID, mNotification.build());

        mRunnerThread = new RunnerThread();
    }

    /**
     *  Turns the thread from continuing to run
     *  Makes the thread null
     *  and destroys the service.
     * */
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        mRunnerThread.setThreadRunning(false);
        mRunnerThread = null;
        // Cancel the notification
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(CHANNEL_ID);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.d(TAG, "onBind");
        return new RunningServiceBinder();
    }

    @Override
    public boolean onUnbind(Intent arg0) {
        Log.d(TAG, "unBind");
        return super.onUnbind(arg0);
    }

    /**
     *  When the service rebinds with the activity
     * */
    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind");
        super.onRebind(intent);
    }

    /**
     *  Begins the thread, which means that the runner class is running
     *
     * */
    public void run() {
        Log.d(TAG, "run");
        mRunnerThread.setThreadRunning(true);
        runner.run();
    }

    /**
     *  Saves the current time and current distance traveled to the database
     *      1. Pauses the mRunnerThread
     *      2. Updates the database
     * */
    public void save() {
        Log.d(TAG, "save");
        mRunnerThread.setThreadRunning(false);
        runner.stop();
    }


    /**
     *  Stops the thread from running and the runner class is not longer running
     *      1. Resets the mRunnerThread
     * */
    public void stop() {
        Log.d(TAG, "stop");
        mRunnerThread.setThreadRunning(false);
        runner.stop();
    }

    /**
    *   Checks if the service is running
    *
    *   @return boolean a boolean value whether the service is running
    *
    * */
    public boolean isServiceRunning(){
        Log.d(TAG, "isServiceRunning");
        return RunningTrackerService.this.isServiceRunning();
    };

    /**
     *  Performs the callback by braodcasting to the activity the new currentDistanceTravelled
     *
     *  @param currentDistanceTravelled  is the current distance travelled
     *
     * */
    public void doCallbacks(float currentDistanceTravelled)
    {
        Log.d(TAG, "doCallbacks");
        // Potentially do the math here?
        final int n = remoteCallbackList.beginBroadcast();
        for (int i=0; i<n; i++)
        {
//            Log.d(TAG, "Current Distance is: " + currentDistanceTravelled);
            remoteCallbackList.getBroadcastItem(i).callback.distanceRan(currentDistanceTravelled);
        }
        remoteCallbackList.finishBroadcast();
    }

    /**
     *
     * Getters and Setters for RunningTrackerService
     *
     * */

    public Runner getRunner() {
        return runner;
    }


    /**
     *  Thread Class that keeps track of the location of the user
     *
     * */
    public class RunnerThread extends Thread implements Runnable {

        // Internal TAG String
        private static final String TAG = "RunnerThread";

        // Bool object to check if we are running or not
        public boolean running = false;

        // Boolean object to see if the thread is running
        public boolean threadRunning = true;

        // Keeps track of the distance
        public float currentDistanceTravelled = 0;

        public RunnerThread() {
            // Starts the thread
            Log.d(TAG, "runnerThread created");
            this.start();

            // Instantiate and set up the RunnerThreads variables
            mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            mLocationListener = new MyLocationListener();

            // Access it regardless
            try {
                // Set the start location as the initial location
                runner.setStartLocation(mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        5, // minimum time interval between updates
                        5, // minimum distance between updates, in metres
                        mLocationListener);
                Log.d(TAG, "Start Location is: " + runner.getStartLocation());
            } catch (SecurityException e) {
                Log.d(TAG, e.toString());
            }


        }

        /**
         *  Executes the thread and checks the current distance of the and continually
         *  calculates the current distance and returns that distance through the callback.
         * */
        public void run() {
//            Looper.prepare();
            while (isThreadRunning()) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    Log.d(TAG, "Error Message: " + e.toString());
                }
                if (isThreadRunning()) {

                    try {
                        // Set the intermediary location as this
                        runner.setIntermediaryLocation(mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
                        currentDistanceTravelled = runner.getIntermediaryLocation().distanceTo(runner.getStartLocation());

                        runner.totalDistanceRan += currentDistanceTravelled;

                        Log.d(TAG, "Current Distance Travelled: " + Float.toString(currentDistanceTravelled));
                        Log.d(TAG, "Total Distance Travelled: " + runner.getTotalDistanceRan());


                    } catch (SecurityException e) {
                        Log.d(TAG, "Error: " + e.toString());

                        // Get the current distance ran here => log the long variable and use the api to track the
                    }
                    // Update the time and call the method doCallbacks => will get the broadcast item
                    doCallbacks(runner.getTotalDistanceRan());

                }
            }
        }

        /*
        *
        *   Getters and Setters for RunnerThread
        *
        * */

        public boolean isThreadRunning() {
            return threadRunning;
        }

        public void setThreadRunning(boolean threadRunning) {
            this.threadRunning = threadRunning;
        }
        
        public void setRunning(boolean running) {
            this.running = running;
        }

        public float getCurrentDistance() {
            return currentDistanceTravelled;
        }

        public void setCurrentDistance(float currentDistanceTravelled) {
            this.currentDistanceTravelled = currentDistanceTravelled;
        }

        public Runner getRunner() {
            return runner;
        }

    }


    /**
     *  Binder Class that interacts with the Activity and part of the service class
     *
     * */
    public class RunningServiceBinder extends Binder implements IInterface {

        private CallbackInterface callback;

        void run(){
            RunningTrackerService.this.run();
        }

        void save(){
            RunningTrackerService.this.save();
        }

        void stop(){
            RunningTrackerService.this.stop();
        }

        public IBinder asBinder() {
            return this;
        }

        public void registerCallback(CallbackInterface callback) {
            this.callback = callback;
            remoteCallbackList.register(RunningServiceBinder.this);
        }

        public void unregisterCallback(CallbackInterface callback) {
            remoteCallbackList.unregister(RunningServiceBinder.this);
        }

        /**
         *
         *  Getters and Setters for RunningServiceBinder
         *
         * */

        public boolean isServiceRunning(){
            return RunningTrackerService.this.isServiceRunning();
        };

        public Runner getRunner() {
            return runner;
        }

    }
}
