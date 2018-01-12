package com.example.davidvalentin.pace;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
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
    private static final String EXCEPTION_TAG = "ERROR IN RTService";

    private static final int CHANNEL_ID = 0;

    // Callback for the thread
    RemoteCallbackList<RunningServiceBinder> remoteCallbackList = new RemoteCallbackList<RunningServiceBinder>();

    //Location Variables:
    private LocationManager locationManager;
    private MyLocationListener locationListener;

    // Member Variables to resume the service
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotification;

    //Utility Library:
    private UtilityLibrary mUtilityLibrary;

    // Logical Member variables
    private RunnerThread mRunnerThread;
    private Runner runner;

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
        this.run();
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
        runner.run();
        mRunnerThread.setThreadRunning(true);
        if (mRunnerThread != null) {
            Log.d(TAG, "STARTING THE SERVICE INTENT IN RUN");
            mRunnerThread.setThreadRunning(true);
        }
        else {
            Log.d(TAG, "RUNNER THREAD IS NULL");
            mRunnerThread = new RunnerThread();
            mRunnerThread.setThreadRunning(true);
        }
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
        runner.save();
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

    //Checks if the mp3 player is playing
    public boolean isRunnerRunning(){
        Log.d(TAG, "isRunnerRunning");
        if(runner.getState() == Runner.RunnerState.RUNNING){
            Log.d(TAG, "RUNNING");
            return true;
        } else if (runner.getState() == Runner.RunnerState.PAUSED)  {
            Log.d(TAG, "PAUSED");
            return false;
        } else if (runner.getState() == Runner.RunnerState.RESTARTED) {
            Log.d(TAG, "RESTARTED");
            return false;
        } else if (runner.getState() == Runner.RunnerState.SAVED) {
            Log.d(TAG, "SAVED");
            return false;
        } else if (runner.getState() == Runner.RunnerState.RESTARTED) {
            Log.d(TAG, "RESTARTED");
            return false;
        } else {
            Log.d(TAG, "ANOMALY: " + runner.getState().toString());
            return false;
        }
    }


    /**
     *
     * Getters and Setters for RunningTrackerService
     *
     * */

    public Runner getRunner() {
        return this.runner;
    }

    public RunningTrackerService getRunningTrackerService() {
        return this;
    }

    public RunnerThread getmRunnerThread() {
        return this.mRunnerThread;
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
        private boolean threadRunning = true;

        // Keeps track of the difference in distance
        private float currentDistanceTravelled ;

        // Keeps track of the total distance travelled
        private float totalDistanceRan;

        private float speed = 0;

        public RunnerThread() {
            // Starts the thread
            Log.d(TAG, "runnerThread created");

            // Instantiate and set up the RunnerThreads variables


//            // Reset the values when first instantiated
//            setCurrentDistanceTravelled(0);
//            setTotalDistanceRan(0);

            // Access it regardless
            try {
                locationManager =
                        (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                locationListener = new MyLocationListener();

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        5, // minimum time interval between updates
                        5, // minimum distance between updates, in metres
                        locationListener);
                runner.setStartLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            } catch(SecurityException e) {
                Log.d(EXCEPTION_TAG, e.toString());
            }

            this.start();

        }

        /**
         *  Executes the thread and checks the current distance of the and continually
         *  calculates the current distance and returns that distance through the callback.
         * */
        public void run() {
            Log.d(TAG, "run");
            Log.d(TAG, "Is Thread running: " + isThreadRunning());
            while (isThreadRunning()) {

                if (runner.getState() == Runner.RunnerState.RUNNING) {
                    try {Thread.sleep(1000);} catch (Exception e) {Log.d(EXCEPTION_TAG, "Error Message: " + e.toString());}
                    try {

                        runner.setIntermediaryLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)); // Update the intermediary location as the most recent location

                        currentDistanceTravelled = runner.getIntermediaryLocation().distanceTo(runner.getStartLocation()); // get the distance between the two points

                        runner.setStartLocation(runner.getIntermediaryLocation()); // Set the new start location as the intermediary location => Allows us to accurately capture the totalDistanceRan

                        setSpeed(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getSpeed()); // Get the speed from the last location

                        totalDistanceRan += currentDistanceTravelled; // Add that distance to the total distance

                        Log.d(TAG, "Total Distance Travelled: " + getTotalDistanceRan());
                    } catch (SecurityException e) {
                        Log.d(EXCEPTION_TAG, "Error: " + e.toString());

                        // Get the current distance ran here => log the long variable and use the api to track the
                    }
                    // Update the time and call the method doCallbacks => will get the broadcast item
                    doCallbacks(totalDistanceRan);
                } else {

                    Log.d(TAG, "RUNNER STATE: " + runner.getState());
                }

            }
        }

        /**
        *
        *   Getters and Setters for RunnerThread
        *
        * */

        private boolean isThreadRunning() {
            return threadRunning;
        }

        private void setThreadRunning(boolean threadRunning) {
            this.threadRunning = threadRunning;
        }

        public float getCurrentDistanceTravelled() {return currentDistanceTravelled;}

        public void setCurrentDistanceTravelled(float currentDistanceTravelled) {this.currentDistanceTravelled = currentDistanceTravelled;}

        public float getTotalDistanceRan() {
            return totalDistanceRan;
        }

        private void setTotalDistanceRan(float totalDistanceRan) {this.totalDistanceRan = totalDistanceRan;}


        public float getSpeed() {return speed;}

        private void setSpeed(float speed) {this.speed = speed;}

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

        public boolean isRunnerRunning() {
            return RunningTrackerService.this.isRunnerRunning();
        }

        /**
         *
         *  Getters and Setters for RunningServiceBinder
         *
         * */

        public RunningTrackerService getRunningTrackerService() {
            return RunningTrackerService.this.getRunningTrackerService();
        }

        public Runner getRunner() {
            return RunningTrackerService.this.getRunner();
        }

        public RunnerThread getmRunnerThread() {
            return RunningTrackerService.this.getmRunnerThread();
        }

        public void setmRunnerThread(RunnerThread mRunnerThread) {
            RunningTrackerService.this.setmRunnerThread(mRunnerThread);
        }
    }
}
