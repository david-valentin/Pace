package com.example.davidvalentin.pace;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.database.backend.DBHelper;
import com.example.database.backend.PaceProviderContract;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.net.URI;
import java.security.Key;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * ProfileViewController renders the activity_profile_view.
 * It is responsible for:
 *  1. Displaying the user data in both a listview and graph view through an adapter
 *  2. Updating the the listview onResume()
 *
 */
public class ProfileViewController extends AppCompatActivity {

    // TAG
    private static final String TAG = "ProfileViewController";
    private static final String EXCEPTION_TAG = "ERROR PVController";

    //XML COMPONENTS
    private ListView runningStatsListView;
    private GraphView paceGraph;

    // Private Member Variables
    private SimpleCursorAdapter dataAdapter;
    private UtilityLibrary mUtilityLibrary;

    // Database values - Kept reusing them
    private String[] DEFAULT_PROJECTION = new String[] {
            PaceProviderContract._ID,
            PaceProviderContract.TOTAL_KILOMETERS_RAN,
            PaceProviderContract.TOTAL_HOURS,
            PaceProviderContract.KILOMETERS_PER_HOUR,
            PaceProviderContract.DATE,
    };

    private String DEFAULT_COLS_TO_DISPLAY [] = new String[] {
            PaceProviderContract.TOTAL_KILOMETERS_RAN,
            PaceProviderContract.TOTAL_HOURS,
            PaceProviderContract.KILOMETERS_PER_HOUR,
            PaceProviderContract.DATE
    };

    private int[] colResIds = new int[] {
            R.id.totalDistance,
            R.id.totalTime,
            R.id.speed,
            R.id.date
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        this.mUtilityLibrary = new UtilityLibrary(this);
        // Populating with straight data.
        queryContentProvider();

        try {
            createLineGraphSeries();
        } catch (Exception e) {
            Log.d(EXCEPTION_TAG, "No data to display: " + e.toString());
        }
    }

    /**
     *   onResume()
     *       1. Reinitializes the cursor object and queries the database
     *       2. Reinitializes the SimpleCursorAdapter and updates the ListViewAdapter
     *       3. Updates the listview adapter to listen and query the database again
     *
     * */
    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        Cursor c = getContentResolver().query(PaceProviderContract.CONTENT_URI, DEFAULT_PROJECTION, null, null, null);

        if (c != null && c.getCount() > 0) {
            try {
                dataAdapter = new SimpleCursorAdapter(
                        this,
                        R.layout.pace_data_listview_row,
                        c,
                        DEFAULT_COLS_TO_DISPLAY,
                        colResIds,
                        0);
                runningStatsListView = findViewById(R.id.runningStatsListView);
                dataAdapter.notifyDataSetChanged();
                runningStatsListView.setAdapter(dataAdapter);
            } catch (Exception e) {
                Log.d(EXCEPTION_TAG, e.toString());
            }
        } else {
            Log.d(EXCEPTION_TAG, "onResume check failed");
        }
    }


    /**
     * Will
     *
     * @return
     */
    public void createLineGraphSeries() {
        Log.d(TAG, "createLineGraphSeries");

        // Get any values from the database
        ArrayList<ContentValues> retVal = retrieveAndStoreContentValues(DEFAULT_PROJECTION);
        // ArrayList


//        Calendar calendar = Calendar.getInstance();
//        Date d1 = calendar.getTime();
//        calendar.add(Calendar.DATE, 1);
//        Date d2 = calendar.getTime();
//        calendar.add(Calendar.DATE, 1);
//        Date d3 = calendar.getTime();

        paceGraph = findViewById(R.id.paceGraph);
//        DataPoint[] list = generateDataPoints();
//        Log.d(TAG, "X: " + list[0].getX());

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(generateDataPoints());
        paceGraph.addSeries(series);

        // set date label formatter
        paceGraph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        paceGraph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

        try {

            SimpleDateFormat df = new SimpleDateFormat("yyyy-dd-mm");

            ArrayList<String> date = getSpecificColumnValuesForFloatsForDate(PaceProviderContract.DATE);

            paceGraph.getViewport().setMinX(df.parse(date.get(0)).getTime());
            paceGraph.getViewport().setMaxX(df.parse(date.get(date.size()-1)).getTime());
            paceGraph.getViewport().setXAxisBoundsManual(true);
        } catch (Exception e) {
            Log.d(EXCEPTION_TAG, e.toString());
        }


    }

    /**
     *
     *
     * @return
     */
    private DataPoint[] generateDataPoints() {
        Log.d(TAG, "generateDataPoints");

        ArrayList<ContentValues> retVal = retrieveAndStoreContentValues(DEFAULT_PROJECTION);// ArrayList
        ArrayList<Float> total_kilometers_ran = getSpecificColumnValuesForFloats(PaceProviderContract.TOTAL_KILOMETERS_RAN);
        ArrayList<String> date = getSpecificColumnValuesForFloatsForDate(PaceProviderContract.DATE);
        DataPoint[] values = new DataPoint[retVal.size()];

        SimpleDateFormat df = new SimpleDateFormat("yyyy-dd-mm");


        try {
            for (int i=0; i<total_kilometers_ran.size(); i++) {
                Date x = df.parse(date.get(i));
                Float y = total_kilometers_ran.get(i);
                DataPoint v = new DataPoint(x, y);
                values[i] = v;
            }
        } catch (Exception e) {
            Log.d(EXCEPTION_TAG, e.toString());
        }

        return values;
    }

    /**
     * Creates and returns an ArrayList of values of the specific column name
     * and stores any values matched in the ContentValues ArrayList in the array
     *
     * @param columnName - the columnName of values that we are searching for
     * @return
     */
    private ArrayList<String> getSpecificColumnValuesForFloatsForDate(String columnName) {
        Log.d(TAG, "getSpecificColumnValuesForFloatsForDate");
        ArrayList<ContentValues> retVal = retrieveAndStoreContentValues(DEFAULT_PROJECTION);
        ArrayList<String> retrievedValues = new ArrayList<String>();
        // declare an array of all the available dates - get up only the most recent 7 days. display those days
        for (int i = 0; i < retVal.size(); i++) {
            for (int j = 0; j < DEFAULT_PROJECTION.length; j++) {
                if (DEFAULT_PROJECTION[j] == columnName) {
                    Log.d(TAG, " " + columnName.toUpperCase() + " : " + retVal.get(i).get(DEFAULT_PROJECTION[j]));
                    String d = String.valueOf(retVal.get(i).get(DEFAULT_PROJECTION[j]));
                    retrievedValues.add(d);
                }
            }
        }
        return retrievedValues;
    }

//    private ArrayList<Date> test(String columnName) throws ParseException {
//        Log.d(TAG, "getSpecificColumnValuesForFloatsForDate");
//        ArrayList<ContentValues> retVal = retrieveAndStoreContentValues(DEFAULT_PROJECTION);
//        ArrayList<Date> retrievedValues = new ArrayList<Date>();
//
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-dd-mm");
//
//        // declare an array of all the available dates - get up only the most recent 7 days. display those days
//        for (int i = 0; i < retVal.size(); i++) {
//            for (int j = 0; j < DEFAULT_PROJECTION.length; j++) {
//                if (DEFAULT_PROJECTION[j] == columnName) {
//                    Log.d(TAG, " " + columnName + ":" + retVal.get(i).get(DEFAULT_PROJECTION[j]));
//                    Date d = df.parse(retVal.get(i).get(DEFAULT_PROJECTION[j]).toString());
//                    retrievedValues.add(d);
//                }
//            }
//        }
//        return retrievedValues;
//    }



    /**
     * Creates and returns an ArrayList of values of the specific column name
     * and stores any values matched in the ContentValues ArrayList in the array
     *
     * @param columnName - the columnName of values that we are searching for
     * @return
     */
    private ArrayList<Float> getSpecificColumnValuesForFloats(String columnName) {
        Log.d(TAG, "getSpecificColumnValuesForFloats");
        ArrayList<ContentValues> retVal = retrieveAndStoreContentValues(DEFAULT_PROJECTION);
        ArrayList<Float> retrievedValues = new ArrayList<Float>();
        // declare an array of all the available dates - get up only the most recent 7 days. display those days
        for (int i = 0; i < retVal.size(); i++) {
            for (int j = 0; j < DEFAULT_PROJECTION.length; j++) {
                if (DEFAULT_PROJECTION[j] == columnName) {
                    Log.d(TAG, " " + columnName.toUpperCase() + " : " + retVal.get(i).get(DEFAULT_PROJECTION[j]));
                    String s = retVal.get(i).get(DEFAULT_PROJECTION[j]).toString();
                    retrievedValues.add(Float.parseFloat(s));
                }
            }
        }
        return retrievedValues;
    }

    /**
     * This queries the databased based off the DEFAULT_PROJECTION string and
     * stores the Cursors contentvalues in an ArrayList of ContentValues
     *
     * @param DEFAULT_PROJECTION
     * @return
     */
    private ArrayList<ContentValues> retrieveAndStoreContentValues(String[] DEFAULT_PROJECTION) {
        Log.d(TAG, "retrieveAndStoreContentValues");

        Cursor c = getContentResolver().query(PaceProviderContract.CONTENT_URI, DEFAULT_PROJECTION, null, null, null
                , null);
        ArrayList<ContentValues> retVal = new ArrayList<ContentValues>();

        ContentValues map;
        if(c.moveToFirst()) {
            do {
                map = new ContentValues();
                // gets the content
                DatabaseUtils.cursorRowToContentValues(c, map);
                retVal.add(map);
            } while(c.moveToNext());
        } else {
            Log.d(TAG, "No values were returned. Retval is null.");
            return retVal; // Will be empty
        }
        c.close();
        return retVal;
    }

    /**
    *   queryContentProvider:
    *       1. Sets the query for the content provider and accesses the content resolver wrapper
    *       2. Sets the list view adapter to the results queried from the content resolver
    * */
    private void queryContentProvider() {
        Log.d(TAG, "queryContentProvider");

        runningStatsListView = findViewById(R.id.runningStatsListView);

        try {
            String[] DEFAULT_PROJECTION = new String[] {
                    PaceProviderContract._ID,
                    PaceProviderContract.TOTAL_KILOMETERS_RAN,
                    PaceProviderContract.TOTAL_HOURS,
                    PaceProviderContract.KILOMETERS_PER_HOUR,
                    PaceProviderContract.DATE,
            };

            String DEFAULT_COLS_TO_DISPLAY [] = new String[] {
                    PaceProviderContract.TOTAL_KILOMETERS_RAN,
                    PaceProviderContract.TOTAL_HOURS,
                    PaceProviderContract.KILOMETERS_PER_HOUR,
                    PaceProviderContract.DATE
            };

            int[] colResIds = new int[] {
                    R.id.totalDistance,
                    R.id.totalTime,
                    R.id.speed,
                    R.id.date
            };

            Cursor c = getContentResolver().query(PaceProviderContract.CONTENT_URI, DEFAULT_PROJECTION, null, null, null);
            dataAdapter = new SimpleCursorAdapter(
                    this,
                    R.layout.pace_data_listview_row,
                    c,
                    DEFAULT_COLS_TO_DISPLAY,
                    colResIds,
                    0);
            runningStatsListView = findViewById(R.id.runningStatsListView);
            runningStatsListView.setAdapter(dataAdapter);


            c.close();

        } catch (Exception e) {
            Log.d(EXCEPTION_TAG, e.toString());

        }
    }

    /*
    *
    *   Getters and setters for ProfileViewController
    *
    * */


    public Context getActivity() {
        return this;
    }
}
