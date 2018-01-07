package com.example.davidvalentin.pace;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by davidvalentin on 11/24/17.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    private static final String TABLE_NAME = "paceDB";

    public DBHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
        // TODO Auto-generated constructor stub
        Log.d(TAG, "DBHelper constructor");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "totalDistanceRan REAL NOT NULL," +
                "totalTime REAL NOT NULL," +
                "speed REAL NOT NULL," +
                "dateObj DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ");");
        // TODO Auto-generated constructor stub
        Log.d(TAG, "DBHelper onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade") ;
        db.execSQL("DROP TABLE IF EXISTS paceDB");
        onCreate(db);
    }

    /*
    *   GETTERS AND SETTERS HERE:
    * */

    public static String getTableName() {
        return TABLE_NAME;
    }

}

