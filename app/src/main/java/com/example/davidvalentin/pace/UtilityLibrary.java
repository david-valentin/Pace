package com.example.davidvalentin.pace;

/**
 * Created by davidvalentin on 1/2/18.
 */

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
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.sql.Date;
import java.util.ArrayList;

/**
 *  A class that holds several static functions
 *
 * */
public class UtilityLibrary {

    private static final String TAG = "UtilityLibrary";
    private static final String EXCEPTION_TAG = "ERROR in UtilityLibrary";

    private static final String ApplicationName = "Pace";

    // The context where the class is being called
    private Context context;

    // Static Variables
    private static final double metersToMiles = 0.000621371;
    private static final double metersToKilometers = 0.001;
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
    public String convertMetersToMilesString(double meters) {
        Log.d(TAG, "convertMetersToMilesString");
        double miles =  getMetersToMiles() * meters;
        return String.format("%.2f", miles);
    }

    /**
     * Converts meters to kilometers
     *
     * @param meters the number of meters ran
     * @return the string representation of the number of kilometers
     * */
    public String convertMetersToKilometersString(double meters) {
        Log.d(TAG, "convertMetersToKilometersSting");
        double miles = getMetersToKilometers() * meters ;
        return String.format("%.2f", miles);
    }

    /**
     *
     * @param meters
     * @return
     */
    public Double convertMetersToKilometers(double meters) {
        Log.d(TAG, "convertMetersToKilometers");
        Double miles = getMetersToKilometers() * meters;
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
     *
     * @param elapsedTime
     * @return
     */
    public float convertSecondsToHours(int elapsedTime) {

        Float decimalHours = (float) elapsedTime/SECOND_TO_HOURS;
        return decimalHours;
    }

    /**
     *
     * @return
     */
    public AlertDialog createAlertDialog() {
        Log.d(TAG, "createAlertDialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                        .setTitle("Confirm")
                                        .setMessage("Are you sure?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        return alert;
    }

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


    public String[] formatUserDataText(String colsToDisplay[]) {
        Log.d(TAG, "formatUserDataText");
        String reformattedValues[] = new String[]{};
        for (int i = 0; i < colsToDisplay.length; i++) {
            // When it will be the date object
            if (i != 3) {
                int indexOfDecimal = colsToDisplay[i].indexOf('.');
                // Get 2 chars after the . and then substring it => Ugly I know..
                reformattedValues[i] = colsToDisplay[i].substring(0, indexOfDecimal+2);
            } else {
                reformattedValues[i] = colsToDisplay[i];
            }
        }
        return reformattedValues;
    }


    public LineGraphSeries<DataPoint> createLineGraphSeries(int numberOfDataPoints, ArrayList<ContentValues> cursorValues) {

//        for (int i = numberOfDataPoints; i < numberOfDataPoints; i++) {
//
//            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
//                    new DataPoint(0, 1),
//                    new DataPoint(1, 5),
//                    new DataPoint(2, 3),
//                    new DataPoint(3, 2),
//                    new DataPoint(4, 6)
//            });
//        }
//
//        return series;

        return null;
    }



    /**
     *
     * Getters and Setters for UtilityLibrary
     *
     * */

    public static String getApplicationName() {
        return ApplicationName;
    }

    public static double getMetersToMiles() {
        return metersToMiles;
    }

    public static double getMetersToKilometers() {
        return metersToKilometers;
    }


}
