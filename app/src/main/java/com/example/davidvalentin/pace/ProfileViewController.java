package com.example.davidvalentin.pace;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
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
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
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
        queryContentProvider();

        this.mUtilityLibrary = new UtilityLibrary(this);

        paceGraph = (GraphView) findViewById(R.id.paceGraph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });

        series.appendData(new DataPoint(5, 7), true, 7);

        paceGraph.addSeries(series);
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


            UtilityLibrary mUtilityLibrary = new UtilityLibrary(this);
//            mUtilityLibrary.logCursorContents(c);

//            ArrayList<ContentValues> myData = mUtiliLibrary.returnCursorContents(c);
//            Set<String> keySet = myData.get(1).keySet();
//
//            for (int i = 0; i < myData.size()-1; i++) {
//                Log.d(TAG, "MyData: " + myData.get(i));
//                Log.d(TAG, "Keys: " + keySet.toString());
//            }

            dataAdapter = new SimpleCursorAdapter(
                    this,
                    R.layout.pace_data_listview_row,
                    c,
                    colsToDisplay,
                    colResIds,
                    0);

            runningStatsListView = findViewById(R.id.runningStatsListView);
            runningStatsListView.setAdapter(dataAdapter);
            Log.d(TAG, "VIEW " + runningStatsListView.getItemAtPosition(0
            ));

        } catch (Exception e) {
            Log.d(EXCEPTION_TAG, e.toString());
        }
    }


}
