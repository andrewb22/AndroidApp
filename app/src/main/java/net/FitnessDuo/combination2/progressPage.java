package net.FitnessDuo.combination2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

public class progressPage extends AppCompatActivity {

    //vars for RecyclerView
    private ArrayList<Long> mDates = new ArrayList<>();
    //private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<Uri> mImageUris = new ArrayList<>();
    private ArrayList<String> mWeights = new ArrayList<>();

    //To access database
    DatabaseHelper mDatabaseHelper;

    private int mIsFromComparisonPage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(net.FitnessDuo.combination2.R.layout.activity_progress_page);

        //For Database
        //mDatabaseHelper = new DatabaseHelper(this);
        mDatabaseHelper = DatabaseHelper.getInstance(this);

        //For comparison page
        Intent intentThatStartedThis = getIntent();
        mIsFromComparisonPage = intentThatStartedThis.getIntExtra("From Comparison", 0);

        initImageBitmaps(); //For RecyclerView
    }

    //For taking pictures, and coincidentally for getting image files for the recyclerView
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d("DEBUG","Permission is granted");
                return true;
            } else {

                Log.d("DEBUG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.d("DEBUG","Permission is granted");
            return true;
        }
    }

    //For recyclerView
    //This is where the arrays of what show up in the recyclerView get set up
    //It's a little complicated right now, because it just gets all the pictures in the file
    private void initImageBitmaps(){
        //Log.d(TAG, "initImageBitmaps: preparing bitmaps.");

        Cursor data = mDatabaseHelper.getData();
        while(data.moveToNext()){
            Log.d("MYTAG", "initImageBitmaps: Adding " + data.getString(1));
            mImageUris.add(Uri.parse(data.getString(1)));
            mDates.add(data.getLong(2));
            Log.d("MYTAG", "initImageBitmaps: Adding " + data.getString(2));
            mWeights.add(data.getString(3)+" lbs");
            Log.d("MYTAG", "initImageBitmaps: Adding " + data.getString(3));
        }
        initRecyclerView();
    }

    //For recyclerView
    private void initRecyclerView(){
        //Log.d(TAG, "initRecyclerView: init recyclerview.");
        RecyclerView recyclerView = findViewById(net.FitnessDuo.combination2.R.id.recycler_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mDates, mImageUris, mWeights, mIsFromComparisonPage, this);
        while(!isStoragePermissionGranted()){
            //Log.d(TAG, "initRecyclerView: Waiting for permission to be granted to continue...");
        }
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onResume() {
        super.onResume();
        //adapter.notifyDataSetChanged();
        //The below code is maybe not so efficient because in a way it makes everything run twice.
        mDates.clear();
        mImageUris.clear();
        mWeights.clear();
        initImageBitmaps();
        initRecyclerView();
    }

    @Override
    public void onBackPressed()
    {
        if(getCallingActivity()==null){
            NavUtils.navigateUpFromSameTask(this);
            super.onBackPressed();
        }else{
            super.onBackPressed();
        }

    }
}
