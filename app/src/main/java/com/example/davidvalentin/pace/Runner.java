package com.example.davidvalentin.pace;

import android.content.Context;
import android.location.Location;
import android.util.Log;


/**
 * Created by davidvalentin on 12/28/17.
 *
 * Notes:
 *  I figured that I should probably abstract some of the aspects of a Runner
 *  in a Runner class similar to the MP3Player Class
 *
 *  The class basically holds some of the properties of a runner
 *      1. Running
 *
 */


public class Runner {

    private static final String TAG = "Runner";

    // Protected Member Variables:
    protected int time;
    protected String timerString;
    protected RunnerState state;

    //Location Variables:
    protected Location startLocation;
    protected Location endLocation;
    protected Location intermediaryLocation;

    // Private Member Variables
    private Context context;
    private java.util.Date noteTS;

    /* STATES EXPLAINED:
    *   RUNNING => The runner is running:
    *       1. The service = running
    *       2. Location listener = running
    *       3. TextViews = updating
    *   SAVED => The runner has stopped and finished their run:
    *       1. The service != running i.e. unbinded/stopped
    *       2. The location listener != running
    *       3. TextViews != updating => TextViews set to defaults
    *   STOPPED => The runner has stopped running (pre req to save):
    *       1. The service = running i.e. unbinded/stopped
    *       2. The location listener != running i.e. null
    *       3. TextViews != updating => TextViews set to defaults
    *   PAUSED => The runner has paused their running i.e. for some reason
    *       1. The service = running
    *       2. Location listener = running
    *       3. TextViews != updating => TextViews and distance is calculated from the latest point
    * */
    public enum RunnerState {
        ERROR,
        RUNNING,
        SAVED,
        PAUSED,
        RESTARTED
    }


    /**
     *  Runner
     *      Constructor for the Runner Class
     *
     */
    public Runner(Context context) {
        Log.d(TAG, "Runner");
        this.state = RunnerState.PAUSED;
        // So that we can instantiate the start location member class in the runnerClass:
        // https://stackoverflow.com/questions/4870667/how-can-i-use-getsystemservice-in-a-non-activity-class-locationmanager
        this.context = context;
    }

    /**
     *  Initiates that the runner is running and would like
     *      1. Record their time and distance travelled
     * */
    public void run() {
        Log.d(TAG, "run");
        if(this.state == RunnerState.PAUSED) {
            Log.d(TAG, "run");
            this.state = RunnerState.RUNNING;
        // Made a separate if else to know when we are in the restarted state
        } else if (this.state == RunnerState.RESTARTED) {
            Log.d(TAG, "RESTARTED => RUNNING");
            this.state = RunnerState.RUNNING;
        }
    }

    /**
     * Initiates that the runner has:
     *  1. Paused Running => i.e. in the Paused state
     *  2. Would like to record the time and distance travelled
     *
     * */
    public void save() {
        Log.d(TAG, "save");
        if(this.state != RunnerState.RUNNING) {
            this.state = RunnerState.SAVED;
        }
    }

    /**
     *  Initiates that the runner has paused running but would still like to:
     *      1. Resume their run and track their time
     * */
    public void pause() {
        Log.d(TAG, "pause");
        if(this.state == RunnerState.RUNNING) {
            this.state = RunnerState.PAUSED;
        }
    }

    /**
     *  Initiates that the runner is running and would like
     *      1. Record their time and distance travelled
     * */
    public void restart() {
        Log.d(TAG, "restart");
        if(this.state == RunnerState.PAUSED) {
            this.state = RunnerState.RESTARTED;
        }
    }

    /*
    *   GETTERS AND SETTERS
    *
    * */

    public RunnerState getState() {
        return this.state;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(Location endLocation) {
        this.endLocation = endLocation;
    }

    public Location getIntermediaryLocation() {
        return intermediaryLocation;
    }

    public void setIntermediaryLocation(Location intermediaryLocation) {
        this.intermediaryLocation = intermediaryLocation;
    }

    public void setRunnerState(RunnerState state) {
        this.state = state;
    }

}
