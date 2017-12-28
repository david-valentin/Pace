package com.example.davidvalentin.talaria;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by davidvalentin on 11/24/17.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";

    private static final String TABLE_NAME = "        public long getCurrentDistance() {\n" +
            "            return currentDistance;\n" +
            "        }\n" +
            "\n" +
            "        public void setCurrentDistance(long currentDistance) {\n" +
            "            this.currentDistance = currentDistance;\n" +
            "        }\n";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                    int version) {
        super(context, "recipesDB", null, 1);
        // TODO Auto-generated constructor stub
        Log.d(TAG, "DBHelper constructor");
    }

    public DBHelper(Context context) {
        super(context, "recipesDB", null, 1);
        // TODO Auto-generated constructor stub
        Log.d(TAG, "DBHelper constructor");
    }

    /**
     *  onCreate
     *      Creates the sqliteDatabase
     * */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Schema player
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "totalDistanceRan VARCHAR(128) NOT NULL," +
                "totalTime VARCHAR(128) NOT NULL," +
                "dateObj DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ");");

        // TODO Auto-generated constructor stub
        Log.d(TAG, "DBHelper onCreate");
    }

    /**
     *  onUpgrade
     *
     * */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade");
        db.execSQL("DROP TABLE IF EXISTS talaria");
        onCreate(db);
    }

    /*
    *   GETTERS AND SETTERS HERE:
    * */
    public static String getTableName() {
        return TABLE_NAME;
    }

}

