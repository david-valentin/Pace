package com.example.davidvalentin.pace;


import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

/**
 *  A class that holds several static functions
 *  shared across all classes
 *
 *  Created by davidvalentin on 1/2/18.
 *
 * */
public class UtilityLibrary {

    private static final String TAG = "UtilityLibrary";
    private static final String EXCEPTION_TAG = "ERROR in UtilityLibrary";

    private static final String ApplicationName = "Pace";

    // The context where the class is being called
    private Context context;

    // Static Variables
    private static final Float metersToMiles = 0.000621371f;
    private static final Float metersToKilometers = 0.001f;
    private static final String onClickColorChange = "#1D78C6";
    private static final String onClickOGColor = "#2294F7";
    private static final int SECOND_TO_HOURS = 3600;

    private static final String DEFAULT_TIME_TEXT = "0:00";
    private static final String DEFAULT_DISTANCE_TEXT = "0.00";

    public UtilityLibrary(Context context) {
        this.context = context;
    }

    /**
     * Creates and returns a notification object
     *
     * @param msg the msg string is the ContentText associated with the Notification
     * @param title the title string is the ContentTitle associated with the Notification
     * @return Notification the notification object
     *
     * */
    public Notification.Builder createNotification(String msg, String title, String contentText, PendingIntent pendingIntent) {
        Log.d(TAG, "createNotification");
        int color = context.getResources().getColor(R.color.colorPrimary);
        Notification.Builder notification =
                new Notification.Builder(context)
                        .setSmallIcon(R.drawable.pace_logo_96)
                        .setContentIntent(pendingIntent)
                        .setContentTitle(ApplicationName)
                        .setContentText(contentText);
        return notification;
    }

    /**
     * Creates and returns a toast object
     *
     *
     * @param msg - the msg string is the content associated with the toast
     * @param duration - the duration of the toast message
     * @return Toast - the toast object returned
     * */
    public Toast createToast(String msg, int duration) {
        Log.d(TAG, "createToast");
        Context context = this.context.getApplicationContext();
        Toast toast = new Toast(this.context);
        toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        return toast;
    }

    /**
     * Converts the meters to miles
     *
     * @param meters the number of meters ran
     * @return the string representation of the number of miles
     * */
    public String convertMetersToMilesString(Float meters) {
        Log.d(TAG, "convertMetersToMilesString");
        Float miles =  getMetersToMiles() * meters;
        return String.format("%.2f", miles);
    }

    /**
     * Converts meters to kilometers
     *
     * @param meters the number of meters ran
     * @return the string representation of the number of kilometers
     * */
    public String convertMetersToKilometersString(Float meters) {
        Log.d(TAG, "convertMetersToKilometersSting");
        Float miles = getMetersToKilometers() * meters ;
        return String.format("%.2f", miles);
    }

    /**
     *
     * @param meters
     * @return
     */
    public Float convertMetersToKilometers(Float meters) {
        Log.d(TAG, "convertMetersToKilometers");
        Float miles = getMetersToKilometers() * meters;
        return miles;
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
     * Converts seconds to hours appropriately
     *
     * @param elapsedTime
     * @return
     */
    public float convertSecondsToHours(int elapsedTime) {

        Float decimalHours = (float) elapsedTime/SECOND_TO_HOURS;
        return decimalHours;
    }

    /**
     * Compares the string text values to the Default text values
     *
     * @param distanceText
     * @param timerText
     * @return
     */
    public boolean checkForDefaultTextValues(TextView distanceText, TextView timerText) {
        Log.d(TAG, "checkForDefaultTextValues");
        if (distanceText.getText().toString().equalsIgnoreCase(DEFAULT_DISTANCE_TEXT) && timerText.getText().toString().equalsIgnoreCase(DEFAULT_TIME_TEXT)) {
            Log.d("check", "true");
            return true;
        } else {
            Log.d("check", "false");
            return false;
        }
    }


    /**
     * Stores the outputs of the cursor object into a ArrayList and
     * logs the data
     *
     * @param c is the cursor object we are checking if there are values
     */
    public void logCursorContents(Cursor c) {
        Log.d(TAG, "logCursorContents");
        try {
            ArrayList<ContentValues> retVal = new ArrayList<ContentValues>();
            ContentValues map;
            if(c.moveToFirst()) {
                do {
                    map = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(c, map);
                    retVal.add(map);
                    Log.d(TAG, "Content Values: " + retVal.get(c.getPosition()));
                } while(c.moveToNext());
            }
        } catch (Exception e) {
            Log.d(EXCEPTION_TAG, e.toString());
        }
    }

    /**
     * Checks if there are even contents being returned to the cursor object
     *
     * @param c
     * @return boolean
     */
    public boolean doesCursorContentsExist(Cursor c) {
        Log.d(TAG, "doesCursorContentsExist");
        try {
            ArrayList<ContentValues> retVal = new ArrayList<ContentValues>();
            ContentValues map;
            if(c.moveToFirst()) {
                do {
                    map = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(c, map);
                    retVal.add(map);
                } while(c.moveToNext());
            }
            return true;
        } finally {
            Log.d(EXCEPTION_TAG, "doesCursorContentsExist");
            c.close();
            return false;
        }
    }

    /**
     *
     * Getters and Setters for UtilityLibrary
     *
     * */

    public static String getApplicationName() {
        return ApplicationName;
    }

    public static Float getMetersToMiles() {
        return metersToMiles;
    }

    public static Float getMetersToKilometers() {
        return metersToKilometers;
    }


}
