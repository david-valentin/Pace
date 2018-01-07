package com.example.davidvalentin.pace;

/**
 * Created by davidvalentin on 1/2/18.
 */

import android.app.PendingIntent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

/**
 *  A class that holds several static functions
 *
 * */
public class UtilityLibrary {

    private static final String TAG = "UtilityLibrary";

    private static final String ApplicationName = "Pace";

    // The context where the class is being called
    private Context context;

    // Conversion variables
    private static final double metersToMiles = 0.000621371;
    private static final double metersToKilometers = 0.001;

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
    public NotificationCompat.Builder createNotification(String msg, String title, String contentText, PendingIntent pendingIntent) {
        Log.d(TAG, "createNotification");
        int color = context.getResources().getColor(R.color.colorPrimary);
        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.pace_logo_96)
                        .setContentIntent(pendingIntent)
                        .setColorized(true)
                        .setColor(color)
                        .setContentTitle(ApplicationName)
                        .setContentText(contentText);

        return notification;
    }

    /**
     * Creates and returns a toast object
     *
     * @param msg - the msg string is the content associated with the toast
     * @return Toast - the toast object returned
     * */
    public Toast createToast(String msg, int duration) {
        Log.d(TAG, "createToast");
        Context context = this.context.getApplicationContext();
        Toast toast = new Toast(this.context);

        if (duration == 0) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            return toast;
        } else {
            toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
            return toast;
        }
    }

    /**
     * Converts the meters to miles
     *
     * @param meters the number of meters ran
     * @return double the miles
     * */
    public String convertMetersToMiles(double meters) {

        double miles =  getMetersToMiles() * meters;
        return String.format("%.2f", miles);
    }

    /**
     * Converts meters to kilometers
     *
     * @param meters the number of meters ran
     * @return the number of kilometers
     * */
    public String convertMetersToKilometers(double meters) {
        double miles = getMetersToKilometers() * meters ;
        return String.format("%.2f", miles);
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
