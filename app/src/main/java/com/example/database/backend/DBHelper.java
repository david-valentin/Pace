package com.example.database.backend;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.example.database.backend.PaceProviderContract.*;
import static com.example.database.backend.PaceProviderContract.KILOMETERS_PER_HOUR;

/**
 * Created by davidvalentin on 11/24/17.
 */

public class DBHelper extends SQLiteOpenHelper {

    // Static Member Values
    private static final String TAG = "DBHelper";
    private static final String TABLE_NAME = "paceData";
    private static final String DATABASE_NAME  = "paceDB";

    /**
     *
     * @param context
     * @param name
     * @param factory
     * @param version
     */
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                    int version) {
        super(context, name, null, 1);
        // TODO Auto-generated constructor stub
        Log.d(TAG, "DBHelper");
    }

    /***
     *
     * @param context
     */
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        // TODO Auto-generated constructor stub
        Log.d(TAG, "DBHelper");
    }

    /**
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE " +
                        "%s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT ," +
                        "%s REAL NOT NULL," +
                        "%s REAL NOT NULL," +
                        "%s REAL NOT NULL," +
                        "date DEFAULT (datetime('now','localtime')));",
                TABLE_NAME, _ID, TOTAL_KILOMETERS_RAN, TOTAL_HOURS, KILOMETERS_PER_HOUR));
        Log.d(TAG, "onCreate");
    }

    /**
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade") ;
        db.execSQL("DROP TABLE IF EXISTS " + getTableName());
        onCreate(db);
    }

    /*
    *
    *   Getters and Setters:
    *
    * */

    public static String getTableName() {
        return TABLE_NAME;
    }

}

