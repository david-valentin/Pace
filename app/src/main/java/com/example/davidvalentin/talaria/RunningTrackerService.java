package com.example.davidvalentin.talaria;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteCallbackList;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by davidvalentin on 12/26/17.
 */

public class RunningTrackerService extends Service {

    private static final String TAG = "RunningTrackerService";
    private static final String CHANNEL_ID = "1";

    // Callback for the thread
    RemoteCallbackList<RunningServiceBinder> remoteCallbackList = new RemoteCallbackList<RunningServiceBinder>();

    private NotificationManager mNotificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class RunningThread extends Thread implements Runnable {

        // Boolean object to see if the thread is running
        public boolean threadRunning = true;

        // Bool object to check if we are running or not
        public boolean running = false;

        // Keeps track of the distance
        public long currentDistance = 0;


        public RunningThread() {
            // Start the thread invokes the threads run functions...I think
            this.start();
        }

        public void run()
        {
            while(this.threadRunning)
            {
                try {Thread.sleep(1000);} catch(Exception e) {return;}
                if(running) {
                    Log.d(TAG, "Thread is running");
                    // Get the current distance ran here => log the long variable and use the api to track the
                    // Do the math here
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

        // GETTERS AND SETTERS HERE:

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
