package com.example.davidvalentin.pace;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by davidvalentin on 12/30/17.
 */

/**
 * A content provider provides access to structured data between different Android applications. This data is exposed to applications either as tables of data (in much the same way as a SQLite database) or as a handle to a file. This essentially involves the implementation of a client/server arrangement whereby the application seeking access to the data is the client and the content provider is the server, performing actions and returning results on behalf of the client.
 *
 * */
public class PaceContentProvider extends ContentProvider {

    // Aggregation
    private DBHelper dbHelper = null;

    private static final UriMatcher uriMatcher;

    static {

        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // routes to main database
        uriMatcher.addURI(PaceProviderContract.AUTHORITY, "paceDB", 1);
        // _id uri
        uriMatcher.addURI(PaceProviderContract.AUTHORITY, "paceDB/#", 2);

        // route for totalDistanceRan
        uriMatcher.addURI(PaceProviderContract.AUTHORITY, "totalDistanceRan", 3);

        uriMatcher.addURI(PaceProviderContract.AUTHORITY, "totalDistanceRan/#", 4);

        // route for
        uriMatcher.addURI(PaceProviderContract.AUTHORITY, "totalTime", 5);

        uriMatcher.addURI(PaceProviderContract.AUTHORITY, "totalTime/#", 6);
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
