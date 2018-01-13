package com.example.database.backend;

import android.net.Uri;

/**
 * Provides static strings used for the creation fo the database and table
 *
 * Created by davidvalentin on 12/30/17.
 */

public class PaceProviderContract {

    public static final String AUTHORITY = "com.example.database.backend.PaceContentProvider";

    public static final String PACE_TABLE = "paceData";
    public static final String DATABASE_NAME = "paceDB";


    public static final Uri CONTENT_URI = Uri.parse("content://"+ AUTHORITY+ "/" + PACE_TABLE);

    public static final String _ID = "_id";

    // Column value names
    public static final String TOTAL_KILOMETERS_RAN = "totalKilometersRan";
    public static final String TOTAL_HOURS = "totalHours";
    public static final String KILOMETERS_PER_HOUR = "kilometersPerHour";
    public static final String DATE = "date";

    // Content Provider Strings
    public static final String CONTENT_TYPE_SINGLE = "vnd.android.cursor.item/PaceContentProvider.data.text";
    public static final String CONTENT_TYPE_MULTIPLE = "vnd.android.cursor.dir/PaceContentProvider.data.text";

}
