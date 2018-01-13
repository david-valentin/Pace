package com.example.database.backend;

import android.content.ContentProvider;
import android.content.ContentUris;
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

import static com.example.database.backend.PaceProviderContract.PACE_TABLE;

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
    /*
    * UriMatcher instance to return a value of 1 when the URI references the entire products table,
    * and a value of 2 when the URI references the ID of a specific row in the products table
    * */
    public static final int PACE_DATA = 1;
    public static final int PACE_DATA_ID = 2;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // routes to main database
        uriMatcher.addURI(PaceProviderContract.AUTHORITY, PACE_TABLE + "/",  PACE_DATA);
        // _id uri
        uriMatcher.addURI(PaceProviderContract.AUTHORITY, PaceProviderContract.PACE_TABLE + "/#",
                PACE_DATA_ID);
    }

    /**
     * Called when the content provider is first created
     *
     * @return
     */
    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate");
        try {
            this.dbHelper = new DBHelper(this.getContext(), PaceProviderContract.DATABASE_NAME, null, 7);
            return true;
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            return false;
        }

    }

    /**
     * This method will be called when a client requests that data be retrieved from the content provider.
     * It is the responsibility of this method to identify the data to be retrieved (either single or multiple rows),
     * perform the data extraction and return the results wrapped in a Cursor object.
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
        Log.d(TAG, "query | Matched URI: " + uriMatcher.match(uri));

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(DBHelper.getTableName());

        int uriType = uriMatcher.match(uri);

        switch(uriType)
        {
            case PACE_DATA_ID: // The case where we are fetching a specific id
                selection = "_ID = " + uri.getLastPathSegment();
                queryBuilder.appendWhere(PaceProviderContract._ID + "="
                        + uri.getLastPathSegment());
                break;
            case PACE_DATA: // The case where we are fetching all values
//                String q7 = String.format("SELECT * FROM " + PACE_TABLE);
//                return db.rawQuery(q7, selectionArgs);
                break;
            default:
                return null;
        }
        Cursor cursor = queryBuilder.query(dbHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     *  Returns the MIME type of the data stored by the content provider.
     *
     * @param uri
     * @return
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        Log.d(TAG, "getType");
        String contentType;
        if (uri.getLastPathSegment()==null)
        {
            contentType = PaceProviderContract.CONTENT_TYPE_MULTIPLE;
        }
        else
        {
            contentType = PaceProviderContract.CONTENT_TYPE_SINGLE;
        }
        return contentType;
    }

    /**
     * Insert creates a uri and inserts the contentValues into the database
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
            case PACE_DATA_ID:
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
     *
     * @param uri
     * @param s
     * @param strings
     * @return
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        Log.d(TAG, "delete");
        throw new UnsupportedOperationException("not implemented");
    }

    /**
     * The method called when existing rows need to be updated on behalf of the client.
     * The method uses the arguments passed through to update the appropriate table rows and return the
     * number of rows updated as a result of the operation.
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
