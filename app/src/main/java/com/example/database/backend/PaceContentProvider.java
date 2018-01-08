package com.example.database.backend;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by davidvalentin on 12/30/17.
 */

/**
 * A content provider provides access to structured data between different Android applications. This data is exposed to applications either as tables of data (in much the same way as a SQLite database) or as a handle to a file. This essentially involves the implementation of a client/server arrangement whereby the application seeking access to the data is the client and the content provider is the server, performing actions and returning results on behalf of the client.
 *
 * Source/Reading Material:
 * http://www.techotopia.com/index.php/An_Android_Content_Provider_Tutorial
 *
 * */
public class PaceContentProvider extends ContentProvider {


    // Aggregation
    private DBHelper dbHelper = null;
    private static final String TAG = "PaceContentProvider";

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

    /**
     *
     * @return
     */
    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate");
        try {
            this.dbHelper = new DBHelper(this.getContext(), "paceDb", null, 7);
            return true;
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            return false;
        }

    }

    /**
     *
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query");

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(dbHelper.getTableName());

        int uriType = uriMatcher.match(uri);

        switch (uriType) {
            case 1:
                queryBuilder.appendWhere(PaceProviderContract._ID + "="
                        + uri.getLastPathSegment());
                break;
            case 2:
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }

        Cursor cursor = queryBuilder.query(dbHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     *
     * @param uri
     * @return
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        Log.d(TAG, "getType");
        return null;
    }

    /**
     * insert creates a uri and inserts the contentValues into the database
     *
     * @param uri the uri to be matched with our list of uris
     * @param contentValues the values to be inserted into the database
     * @return Uri generated
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        Log.d(TAG, "insert");

        int uriType = uriMatcher.match(uri);

        SQLiteDatabase paceDb = dbHelper.getWritableDatabase();

        long id = 0;
        switch (uriType) {
            case 1:
                id = paceDb.insert(dbHelper.getDatabaseName(),
                        null, contentValues);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        dbHelper.close();
        return Uri.parse(dbHelper.getDatabaseName() + "/" + id);
    }

    /**
     *
     * @param uri
     * @param s
     * @param strings
     * @return
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        Log.d(TAG, "delete");

        return 0;
    }

    /**
     *
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Log.d(TAG, "update");

        int uriType = uriMatcher.match(uri);
        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
        int rowsUpdated = 0;

        switch (uriType) {
            case 1:
                rowsUpdated = sqlDB.update(PaceProviderContract._ID,
                        values,
                        selection,
                        selectionArgs);
                break;
            case 2:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated =
                            sqlDB.update(dbHelper.getTableName(),
                                    values,
                                    PaceProviderContract._ID + "=" + id,
                                    null);
                } else {
                    rowsUpdated =
                            sqlDB.update(dbHelper.getTableName(),
                                    values,
                                    PaceProviderContract._ID + "=" + id
                                            + " and "
                                            + selection,
                                    selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
