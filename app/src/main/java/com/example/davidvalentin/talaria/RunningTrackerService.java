package com.example.davidvalentin.talaria;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteCallbackList;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


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

    // Callback for the thread
    RemoteCallbackList<RunningServiceBinder> remoteCallbackList = new RemoteCallbackList<RunningServiceBinder>();

    // Member Variables to resume the service
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotification;

    //Utility Library:
    private UtilityLibrary mUtilityLibrary;

    // Logical Member variables
    private RunnerThread mRunnerThread;
    private Runner runner;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    /**
     * Creates the activity and creates the runner thread which
     *  starts automatically
     * */
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        Log.d(TAG, "onCreate");
        super.onCreate();
        mUtilityLibrary = new UtilityLibrary(this.getApplicationContext()); // Utility Library Instantiation
        // Created the notification
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class),0);
        mNotification = mUtilityLibrary.createNotification("Talaria", mUtilityLibrary.getApplicationName(), "Running", pIntent);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(CHANNEL_ID, mNotification.build());

        runner = new Runner(this); // Instantiate the new runner object
        mRunnerThread = new RunnerThread(); // Instantiate the runner thread
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
     *      1. Stops the runnerThread
     *      2. Gets the last known location and last known time calledback
     *      3. Updates the databased with that data
     * */
    public void save() {
        Log.d(TAG, "save");
        mRunnerThread.setThreadRunning(false);
        runner.stop();
    }

    public void restart() {
        Log.d(TAG, "save");
        mRunnerThread.setThreadRunning(false);
        runner.restart();
    }


    /**
     *  Stops the thread from running and the runner class is not longer running
     *      1. Stops the runnerThread
     *      2. Stops location listening => The runner is officially done running
     * */
    public void stop() {
        Log.d(TAG, "stop");
        mRunnerThread.setThreadRunning(false);
        runner.stop();
        setmLocationManager(null);
        setmLocationListener(null);
    }

    /**
     *  Pauses the runner
     *      1. runnerThread is still running
     *      2. locationListener is still listening
     *      3. Callbacks are no longer necessary
     * */
    public void pause() {
        Log.d(TAG, "pause");
        mRunnerThread.setThreadRunning(false);
        runner.pause();
    }

    /**
    *   Checks if the service is running
    *
    *   @return boolean a boolean value whether the service is running
    *
    * */
    public boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

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
            remoteCallbackList.getBroadcastItem(i).callback.distanceRan(currentDistanceTravelled);
        }
        remoteCallbackList.finishBroadcast();
    }


    /**
     *  WARNING: DANGEROUS
     *  Creates a new runner thread instance and replaces the old instance if it is null
     *
     */
    protected void createNewRunnerThread() {
        Log.d(TAG, "createNewRunnerThread");
        if (this.mRunnerThread == null) {
            this.mRunnerThread = new RunnerThread();
        } else {
            Log.d(TAG, "mRunnerThread already Exists!");
        }
    }

    /**
     *
     * Getters and Setters for RunningTrackerService
     *
     * */

    public Runner getRunner() {
        return runner;
    }

    public LocationManager getmLocationManager() {
        return mLocationManager;
    }


    public LocationListener getmLocationListener() {
        return mLocationListener;
    }

    public void setmLocationManager(LocationManager mLocationManager) {
        this.mLocationManager = mLocationManager;
    }

    public void setmLocationListener(LocationListener mLocationListener) {
        this.mLocationListener = mLocationListener;
    }

    public RunningTrackerService getRunningTrackerService() {
        return this;
    }

    public RunnerThread getmRunnerThread() {
        return mRunnerThread;
    }

    public void setmRunnerThread(RunnerThread mRunnerThread) {
        this.mRunnerThread = mRunnerThread;
    }

    /**
     *  Thread Class that keeps track of the location of the user
     *
     * */
    public class RunnerThread extends Thread implements Runnable {

        // Internal TAG String
        private static final String TAG = "RunnerThread";

        // Boolean object to see if the thread is running
        public boolean threadRunning = true;

        // Keeps track of the difference in distance
        public float currentDistanceTravelled = 0;

        // Keeps track of the total distance travelled
        public float totalDistanceRan = 0;

        public RunnerThread() {
            // Starts the thread
            Log.d(TAG, "runnerThread created");

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

            this.start();

        }

        /**
         *  Executes the thread and checks the current distance of the and continually
         *  calculates the current distance and returns that distance through the callback.
         * */
        public void run() {
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

                        totalDistanceRan += currentDistanceTravelled;

                        Log.d(TAG, "Current Distance Travelled: " + Float.toString(currentDistanceTravelled));
                        Log.d(TAG, "Total Distance Travelled: " + getTotalDistanceRan());


                    } catch (SecurityException e) {
                        Log.d(TAG, "Error: " + e.toString());

                        // Get the current distance ran here => log the long variable and use the api to track the
                    }
                    // Update the time and call the method doCallbacks => will get the broadcast item
                    doCallbacks(getTotalDistanceRan());
                }
            }
        }

        /**
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

        public float getCurrentDistance() {
            return currentDistanceTravelled;
        }

        public void setCurrentDistance(float currentDistanceTravelled) {
            this.currentDistanceTravelled = currentDistanceTravelled;
        }

        public Runner getRunner() {
            return runner;
        }

        public float getTotalDistanceRan() {
            return totalDistanceRan;
        }

        public void setTotalDistanceRan(float totalDistanceRan) {
            this.totalDistanceRan = totalDistanceRan;
        }

    }


    /**
     *  Binder Class that interacts with the Activity and part of the service class
     *
     * */
    public class RunningServiceBinder extends Binder implements IInterface {

        private CallbackInterface callback;

        void restart(){
            RunningTrackerService.this.restart();
        }

        void run(){
            RunningTrackerService.this.run();
        }

        void save(){
            RunningTrackerService.this.save();
        }

        void stop(){
            RunningTrackerService.this.stop();
        }

        void pause(){
            RunningTrackerService.this.pause();
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

        public boolean isServiceRunning(Class<?> serviceClass){
            return RunningTrackerService.this.isServiceRunning(serviceClass);
        };

        public RunningTrackerService getRunningTrackerService() {
            return RunningTrackerService.this.getRunningTrackerService();
        }

        public Runner getRunner() {
            return runner;
        }

        public RunnerThread getmRunnerThread() {
            return this.getmRunnerThread();
        }

        public void setmRunnerThread(RunnerThread mRunnerThread) {
            this.setmRunnerThread(mRunnerThread);
        }
    }
}
