package com.example.davidvalentin.talaria;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class ProfileViewController extends AppCompatActivity {

    // TAG
    private static final String TAG = "ProfileViewController";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
    }

}
