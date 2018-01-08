package com.example.davidvalentin.pace;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
    private DBHelper dbHelper = null;

    // Private Member Variables
    private SimpleCursorAdapter dataAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        queryContentProvider();
        dbHelper = new DBHelper(this.getApplicationContext());

        paceGraph = (GraphView) findViewById(R.id.paceGraph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
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
//
//        dbHelper = new DBHelper(this);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        Cursor recipeCursor = db.query("recipes", new String[] { "_id", "recipeTitle", "recipeInstructions" },
//                null, null, null, null, null);
//
//        String[] columns = new String[] {
//                RecipeProviderContract.RECIPE_TITLE
//        };
//
//        int[] to = new int[] {
//                R.id.recipeListRow,
//        };
//
//        dataAdapter = new SimpleCursorAdapter(
//                this, R.layout.recipelistviewrow,
//                recipeCursor,
//                columns,
//                to,
//                0);
//
//        dataAdapter.notifyDataSetChanged();
//        recipeListView.setAdapter(dataAdapter);
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
            String query = "SELECT * from paceData";
            Cursor c_2   = dbHelper.getReadableDatabase().rawQuery(query, null);

            ArrayList<ContentValues> retVal = new ArrayList<ContentValues>();
            ContentValues map;
            if(c.moveToFirst()) {
                do {
                    map = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(c, map);
                    retVal.add(map);
                    Log.d(TAG, "FLOAT " + c_2.getFloat(1));

                } while(c.moveToNext());
            }

            ArrayList<ContentValues> retVal_2 = new ArrayList<ContentValues>();
            ContentValues map_two;
            if(c_2.moveToFirst()) {
                do {
                    map_two = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(c_2, map_two);
                    retVal.add(map_two);
                    Log.d(TAG, "FLOAT " + c_2.getFloat(1));
                } while(c.moveToNext());
            }

//            Log.d(TAG, " " + cursor.getColumnCount());
//            Log.d(TAG, "Float " + cursor.getFloat(1));




            dataAdapter = new SimpleCursorAdapter(
                    this,
                    R.layout.pace_data_listview_row,
                    c_2,
                    colsToDisplay,
                    colResIds,
                    0);

            runningStatsListView = (ListView) findViewById(R.id.runningStatsListView);
            runningStatsListView.setAdapter(dataAdapter);
            c.close(); // that's important too, otherwise you're gonna leak cursors
            c_2.close(); // that's important too, otherwise you're gonna leak cursors


        } catch (Exception e) {
            Log.d(EXCEPTION_TAG, e.toString());
        }



    }


}
