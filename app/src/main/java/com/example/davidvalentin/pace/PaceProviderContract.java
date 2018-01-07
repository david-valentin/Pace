package com.example.davidvalentin.pace;

import android.net.Uri;

/**
 * Created by davidvalentin on 12/30/17.
 */

public class PaceProviderContract {

    public static final String AUTHORITY = "com.example.davidvalentin.pace";

    public static final Uri RECIPE_URI = Uri.parse("content://"+AUTHORITY+"/paceDb");

    public static final String _ID = "_id";

    // Column value names
    public static final String TOTAL_DISTANCE_RAN = "totalDistanceRan";
    public static final String TOTAL_TIME = "totalTime";
    public static final String SPEED = "speed";
    public static final String DATE = "dateObj";

    // Content Provider Strings
    public static final String CONTENT_TYPE_SINGLE = "vnd.android.cursor.item/PaceContentProvider.data.text";
    public static final String CONTENT_TYPE_MULTIPLE = "vnd.android.cursor.dir/PaceContentProvider.data.text";

}
