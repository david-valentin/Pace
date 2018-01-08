package com.example.database.backend;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

/**
 * Created by davidvalentin on 1/8/18.
 */

class MyObserver extends ContentObserver {

    private static final String TAG = "ContentObserver";

    public MyObserver(Handler handler) {
        super(handler);
        Log.d(TAG, "MyObserver");
    }
    @Override
    public void onChange(boolean selfChange) {
        Log.d(TAG, "onChange");
        this.onChange(selfChange, null);
    }
    @Override
    public void onChange(boolean selfChange, Uri uri) {
        Log.d(TAG, "onChange");
    }
}
