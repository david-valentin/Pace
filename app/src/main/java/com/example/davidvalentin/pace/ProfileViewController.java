package com.example.davidvalentin.pace;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.database.backend.PaceProviderContract;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
    private TextView graphHeaderText;
    private TextView userDataHeaderText;

    // Private Member Variables
    private SimpleCursorAdapter dataAdapter;
    private SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-dd-mm hh:mm:ss");


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
        queryContentProvider(); // Populating the listview with data

        try {
            createLineGraphSeries();
        } catch (Exception e) {
            TextView noDataText = findViewById(R.id.noDataTextView);
            noDataText.setVisibility(View.VISIBLE);
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

        paceGraph = findViewById(R.id.paceGraph);
        try {

            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(generateDataPoints());
            series = setProperties(series);
            paceGraph.addSeries(series);

            ArrayList<String> date = getSpecificColumnValuesForDate(PaceProviderContract.DATE);

            paceGraph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
            paceGraph.getGridLabelRenderer().setNumHorizontalLabels(date.size());

            paceGraph.getViewport().setMinX(DEFAULT_DATE_FORMAT.parse(date.get(0)).getTime());
            paceGraph.getViewport().setMaxX(DEFAULT_DATE_FORMAT.parse(date.get(date.size()-1)).getTime());
        } catch (Exception e) {
            Log.d(EXCEPTION_TAG, e.toString());
        }


    }

    private LineGraphSeries setProperties(LineGraphSeries series) {
        series.setTitle("Running Data");
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);
        series.setDataPointsRadius(10);
        return series;
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
        ArrayList<String> date = getSpecificColumnValuesForDate(PaceProviderContract.DATE);
        DataPoint[] values = new DataPoint[retVal.size()];


        try {
            for (int i=0; i<total_kilometers_ran.size(); i++) {
                Date x = DEFAULT_DATE_FORMAT.parse(date.get(i));
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
    private ArrayList<String> getSpecificColumnValuesForDate(String columnName) {
        Log.d(TAG, "getSpecificColumnValuesForDate");
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

        // MAXIMUM NUMBER OF DAYS WE WANT TO DISPLAY ON THE GRAPH - LIMIT IT
        int MAX_DAYS = 7;
        ContentValues map;
        if(c.moveToFirst() && MAX_DAYS > 0) {
            do {
                map = new ContentValues();
                // gets the content
                DatabaseUtils.cursorRowToContentValues(c, map);
                retVal.add(map);
                --MAX_DAYS;
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
