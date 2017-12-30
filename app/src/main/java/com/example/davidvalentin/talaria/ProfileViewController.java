package com.example.davidvalentin.talaria;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ProfileViewController extends AppCompatActivity {

    // TAG
    private static final String TAG = "ProfileViewController";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

    }

    /*
*   queryContentProvider:
*       1. Sets the query for the content provider and accesses the content resolver wrapper
*       2. Sets the list view adapter to the results queried from the content resolver
* */
    public void queryContentProvider() {

//        String[] projection = new String[] {
//                RecipeProviderContract._ID,
//                RecipeProviderContract.RECIPE_TITLE,
//                RecipeProviderContract.RECIPE_INSTRUCTIONS,
//                RecipeProviderContract.RECIPE_IMG_PATH
//        };
//
//        String colsToDisplay [] = new String[] {
//                RecipeProviderContract.RECIPE_TITLE
//        };
//
//        int[] colResIds = new int[] {
//                R.id.recipeListRow
//        };
//
//        Cursor cursor = getContentResolver().query(Uri.parse(RecipeProviderContract.RECIPE_TITLE), projection, null, null, null);
//
//        dataAdapter = new SimpleCursorAdapter(
//                this,
//                R.layout.recipelistviewrow,
//                cursor,
//                colsToDisplay,
//                colResIds,
//                0);
//
//        recipeListView = (ListView) findViewById(R.id.recipeListView);
//        recipeListView.setAdapter(dataAdapter);
    }


}
