package com.example.davidvalentin.talaria;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;


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


    public enum RunnerState {
        ERROR,
        RUNNING,
        SAVED,
        STOPPED
    }


    /**
     *  Runner
     *      Constructor for the Runner Class
     *
     */
    public Runner(Context context) {

        this.state = RunnerState.STOPPED;

        // So that we can instantiate the start location member class in the runnerClass:
        // https://stackoverflow.com/questions/4870667/how-can-i-use-getsystemservice-in-a-non-activity-class-locationmanager
        this.context = context;
    }

    public int getProgress() {
        if(this!=null) {
            if(this.state == RunnerState.STOPPED || this.state == RunnerState.RUNNING)
                return 0;
        }
        return 0;
    }

    /**
     *  Initiates that the runner is running and not in a stopped state
     * */
    public void run() {
        if(this.state == RunnerState.STOPPED) {
            this.state = RunnerState.RUNNING;
        }
    }

    /**
     *
     * Initiates that the runner has:
     *  1. Stopped Running
     *  2. Would like to record the time and distance travelled
     * */
    public void save() {
        if(this.state == RunnerState.RUNNING) {
            state = RunnerState.SAVED;
        }
    }

    /**
     *  Initiates that the Runner has stopped running:
     *
     * */
//    public void pause() {
//        if(this.state == RunnerState.RUNNING) {
//            state = RunnerState.SAVED;
//        }
//    }

    /**
     *  Initiates that the Runner has stopped running
     *
     * */
    public void stop() {
        if(this !=null) {
            state = RunnerState.STOPPED;
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

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

}
