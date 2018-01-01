package com.example.davidvalentin.talaria;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteCallbackList;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Optional;


/**
 * Created by davidvalentin on 12/26/17.
 */

public class RunningTrackerService extends Service {

    // Member variables
    private static final String TAG = "RunningTrackerService";
    private static final String CHANNEL_ID = "1";

    // Callback for the thread
    RemoteCallbackList<RunningServiceBinder> remoteCallbackList = new RemoteCallbackList<RunningServiceBinder>();

    // To resume the running activity
    private NotificationManager mNotificationManager;

    // Instance of the thread class
    private RunnerThread mRunnerThread;

    // Runner class - instantiate it with the Service Context
    private Runner runner = new Runner(this);

    private LocationManager locationManager;


    /**
     * Creates the activity and creates the runner thread which
     *  starts automatically
     * */
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        Log.d(TAG, "onCreate");
        
        super.onCreate();
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
        mRunnerThread.threadRunning = false;
        mRunnerThread = null;
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
     *  Begins the thread, which begins that the runner is running
     *
     * */
    public void run() {
        mRunnerThread.running = true;
        runner.run();
    }

    /**
     *  Saves the current time and current distance traveled to the database
     *      1. Pauses the mRunnerThread
     *      2. Updates the database
     * */
    public void save() {
        mRunnerThread.running = false;
        runner.stop();
    }


    /**
     *  Stops
     *      1. Resets the mRunnerThread bar
     * */
    public void stop() {
        mRunnerThread.running = false;
        runner.stop();
    }

    /*
    *   Checks if the service is Running
    *
    *   @return boolean a boolean value whether the service is running
    *
    * */
    public boolean isRunning(){
        return RunningTrackerService.this.isRunning();
    };


    /**
     *  Thread Class
     * */
    public class RunnerThread extends Thread implements Runnable {

        // Bool object to check if we are running or not
        public boolean running = false;

        // Boolean object to see if the thread is running
        public boolean threadRunning = true;

        // Keeps track of the distance
        public long currentDistanceTravelled = 0;

        public int time = 0;


        public RunnerThread() {
            // Starts the thread
            this.start();
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // Access it regardless
//            runner.setStartLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));

        }

        /**
         *  run executes and performs the thread
         * */
        public void run()
        {
            while(this.threadRunning)
            {
                try {Thread.sleep(1000);} catch(Exception e) {Log.d(TAG, "Error Message: " + e.toString());}
                if(threadRunning) {
                    Log.d(TAG, "Thread is running");
                    currentDistanceTravelled += 1;
                    // Get the current distance ran here => log the long variable and use the api to track the
                    // Do the math here
//                    MyLocationListener locationListener = new MyLocationListener();

                }
                // Update the time and call the method doCallbacks => will get the broadcast item
                doCallbacks(currentDistanceTravelled);
            }
        }

        /**
         *  Performs the callback by braodcasting to the activity the new currentDistanceTravelled
         *
         *  @param currentDistanceTravelled  is the current distance travelled
         *
         * */
        public void doCallbacks(long currentDistanceTravelled)
        {
            Log.d(TAG, "doCallbacks");
            // Potentially do the math here?
            final int n = remoteCallbackList.beginBroadcast();
            for (int i=0; i<n; i++)
            {
//                Log.d(TAG, "Current Distance is: " + currentDistanceTravelled);
                remoteCallbackList.getBroadcastItem(i).callback.distanceRan(currentDistanceTravelled);
            }
            remoteCallbackList.finishBroadcast();
        }

        /*
        *
        *   GETTERS AND SETTERS:
        *
        * */

        public boolean isThreadRunning() {
            return threadRunning;
        }

        public void setThreadRunning(boolean threadRunning) {
            this.threadRunning = threadRunning;
        }

        public boolean isRunning() {
            return running;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        public long getCurrentDistance() {
            return currentDistanceTravelled;
        }

        public void setCurrentDistance(long currentDistanceTravelled) {
            this.currentDistanceTravelled = currentDistanceTravelled;
        }

    }


    /**
     *  Binder Class that interacts with the Activity and part of the service class
     *
     * */
    public class RunningServiceBinder extends Binder implements IInterface {

        private CallbackInterface callback;

        public void run(){
            RunningTrackerService.this.run();
        }
        public void save(){
            RunningTrackerService.this.save();
        }
        public void stop(){
            RunningTrackerService.this.stop();
        }

        public void registerCallback(CallbackInterface callback) {
            this.callback = callback;
            remoteCallbackList.register(RunningServiceBinder.this);
        }
        public void unregisterCallback(CallbackInterface callback) {
            remoteCallbackList.unregister(RunningServiceBinder.this);
        }

        public boolean isRunning(){
            return RunningTrackerService.this.isRunning();
        };

        @Override
        public IBinder asBinder() {
            return this;
        }
    }
}
