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
     *
     * */
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        Log.d(TAG, "onCreate");
        
        mRunnerThread = new RunnerThread();
        
        super.onCreate();
    }

    /**
     *
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

        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        Resources r = getResources();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setTicker(("message"))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle("title")
                .setContentText("text")
                .setContentIntent(pi)
                .setAutoCancel(false)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(Integer.parseInt(CHANNEL_ID), notification);
        return super.onUnbind(arg0);
    }

    /**
     *  Plays the current song
     *      1. Must keep playing the song => Where the service comes into play
     *      2. Continues playing the song till the song is over
     * */
    public void run() {
        mRunnerThread.running = true;
        runner.run();
    }

    /**
     *  Pauses the current song
     *      1. Pauses the mRunnerThread bar => grabs the int value for the mRunnerThread bar and set it to that
     *      2. Keep track of the place where the song was stopped
     * */
    public void save() {
        mRunnerThread.running = false;
        runner.stop();
    }


    /**
     *  Stops the current song
     *      1. Resets the mRunnerThread bar
     * */
    public void stop() {
        mRunnerThread.running = false;
        runner.stop();
    }

    /*
    *   Running the service
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
        public long currentDistance = 0;

        public int time = 0;


        public RunnerThread() {
            // Starts the thread
            this.start();
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // Access it regardless
//            runner.setStartLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));

        }

        public void run()
        {
            while(this.threadRunning)
            {
                try {Thread.sleep(1000);} catch(Exception e) {Log.d(TAG, "Error Message: " + e.toString());}
                if(threadRunning) {
                    Log.d(TAG, "Thread is running");
                    // Get the current distance ran here => log the long variable and use the api to track the
                    // Do the math here
                    MyLocationListener locationListener = new MyLocationListener();

                    try {
                        // Requesting the location update
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                5, // minimum time interval between updates
                                5, // minimum distance between updates, in metres
                                locationListener);
                        // Set the
                        runner.setIntermediaryLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
                        // calculate the current distance and use this to update the textview
                        currentDistance = (long) runner.getIntermediaryLocation().distanceTo(runner.getStartLocation());
                        time = runner.getProgress();

                    } catch(SecurityException e) {
                        Log.d(TAG, e.toString());
                    }

                }
                // Update the time and call the method doCallbacks => will get the broadcast item
                doCallbacks(currentDistance);
            }
        }

        public void doCallbacks(long currentDistance)
        {
            // Potentially do the math here?
            final int n = remoteCallbackList.beginBroadcast();
            for (int i=0; i<n; i++)
            {
                remoteCallbackList.getBroadcastItem(i).callback.distanceRan(currentDistance);
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
            return currentDistance;
        }

        public void setCurrentDistance(long currentDistance) {
            this.currentDistance = currentDistance;
        }

    }


    /**
     *  Binder Class that interacts with the Thread
     *
     * */
    public class RunningServiceBinder extends Binder implements IInterface {

        private CallbackInterface callback;

        public void registerCallback(CallbackInterface callback) {
            this.callback = callback;
            remoteCallbackList.register(RunningServiceBinder.this);
        }
        public void unregisterCallback(CallbackInterface callback) {
            remoteCallbackList.unregister(RunningServiceBinder.this);
        }

        @Override
        public IBinder asBinder() {
            return null;
        }
    }
}
