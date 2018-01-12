package com.example.davidvalentin.pace;

import android.content.ContentValues;
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
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.net.URI;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        this.mUtilityLibrary = new UtilityLibrary(this);
        paceGraph = (GraphView) findViewById(R.id.paceGraph);
        // Populating with straight data.
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>();

        try {
            series = createLineGraphSeries();
            paceGraph.addSeries(series);
            queryContentProvider();
        } catch (Exception e) {
            Log.d(TAG, "No data to display");
        }
    }

    public BarGraphSeries<DataPoint> createLineGraphSeries() {
        Log.d(TAG, "createLineGraphSeries");

        String[] projection = new String[] {
                PaceProviderContract._ID,
                PaceProviderContract.TOTAL_KILOMETERS_RAN,
                PaceProviderContract.TOTAL_HOURS,
                PaceProviderContract.KILOMETERS_PER_HOUR,
                PaceProviderContract.DATE,
        };

        DBHelper dbHelper = new DBHelper(this);
        Cursor c = getContentResolver().query(PaceProviderContract.CONTENT_URI, projection, null, null, null
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
        }

        /// need to fetch the specific values - date time and

        // declare an array of total kilometers ran - floa
        ArrayList<Float> kilometers_ran = new ArrayList<Float>();
        ArrayList<Float> all_speeds = new ArrayList<Float>();
        ArrayList<Date> date = new ArrayList<Date>();


        // declare an array of all the available dates - get up only the most recent 7 days. display those days

        for (int i = 0; i < retVal.size(); i++) {
            for (int j = 0; j < projection.length; j++) {
                if (projection[j] == PaceProviderContract.TOTAL_KILOMETERS_RAN) {
                    kilometers_ran.add((Float) retVal.get(i).get(projection[j]));
                } else if (projection[j] == PaceProviderContract.DATE) {
                    kilometers_ran.add((Float) retVal.get(i).get(projection[j]));
                } else if (projection[j] == PaceProviderContract.KILOMETERS_PER_HOUR) {
                    kilometers_ran.add((Float) retVal.get(i).get(projection[j]));
                }
            }
        }

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("E"); // the day of the week abbreviated

        for (int k = 0; k < kilometers_ran.size(); k++) {
            series.appendData(new DataPoint(date.get(k), kilometers_ran.get(k)), true, 7);
        }


        series.setSpacing(50);

        // draw values on top
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);
        return series;



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

        String[] projection = new String[] {
                PaceProviderContract._ID,
                PaceProviderContract.TOTAL_KILOMETERS_RAN,
                PaceProviderContract.TOTAL_HOURS,
                PaceProviderContract.KILOMETERS_PER_HOUR,
                PaceProviderContract.DATE,
        };
        String colsToDisplay [] = new String[] {
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

        Cursor c = getContentResolver().query(PaceProviderContract.CONTENT_URI, projection, null, null, null);

        if (c != null && c.getCount() > 0) {
            try {
                dataAdapter = new SimpleCursorAdapter(
                        this,
                        R.layout.pace_data_listview_row,
                        c,
                        colsToDisplay,
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
    *   queryContentProvider:
    *       1. Sets the query for the content provider and accesses the content resolver wrapper
    *       2. Sets the list view adapter to the results queried from the content resolver
    * */
    public void queryContentProvider() {
        Log.d(TAG, "queryContentProvider");

        runningStatsListView = findViewById(R.id.runningStatsListView);

        try {
            String[] projection = new String[] {
                    PaceProviderContract._ID,
                    PaceProviderContract.TOTAL_KILOMETERS_RAN,
                    PaceProviderContract.TOTAL_HOURS,
                    PaceProviderContract.KILOMETERS_PER_HOUR,
                    PaceProviderContract.DATE,
            };

            String colsToDisplay [] = new String[] {
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

            Cursor c = getContentResolver().query(PaceProviderContract.CONTENT_URI, projection, null, null, null);
            dataAdapter = new SimpleCursorAdapter(
                    this,
                    R.layout.pace_data_listview_row,
                    c,
                    colsToDisplay,
                    colResIds,
                    0);
            runningStatsListView = findViewById(R.id.runningStatsListView);
            runningStatsListView.setAdapter(dataAdapter);


            c.close();

        } catch (Exception e) {
            Log.d(EXCEPTION_TAG, e.toString());

        }
    }


}
