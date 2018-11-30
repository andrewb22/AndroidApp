package net.FitnessDuo.combination2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jsibbold.zoomage.ZoomageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Compare extends AppCompatActivity {

    private static final String TAG = "Compare";

    private TextView beforeWeightText, beforeDateText, afterWeightText, afterDateText;
    private ZoomageView beforeImage, afterImage;

//    private String selectedBeforeWeight, selectedBeforeDate, selectedBeforeImagePath;
//    private String selectedAfterWeight, selectedAfterDate, selectedAfterImagePath;
//    private int beforeOrAfter;

    static final int BEFORE_DATA_REQUEST_CODE = 34;
    static final int AFTER_DATA_REQUEST_CODE = 35;

    private Button saveButton;

    //To access database
    DatabaseHelper mDatabaseHelper; //So it auto-loads the before and after pictures.

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(net.FitnessDuo.combination2.R.layout.activity_compare);

        if (savedInstanceState != null) {
            String message = savedInstanceState.getString("message");
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }


        Button changeBeforePhotoButton = (Button) findViewById(net.FitnessDuo.combination2.R.id.btn_before);
        changeBeforePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Compare.this, progressPage.class);
                intent.putExtra("From Comparison", 1);
                startActivityForResult(intent, BEFORE_DATA_REQUEST_CODE);
                //startActivity(intent);
                //finish();
            }
        });

        Button changeAfterPhotoButton = (Button) findViewById(net.FitnessDuo.combination2.R.id.btn_after);
        changeAfterPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Compare.this, progressPage.class);
                intent.putExtra("From Comparison", 2);
                startActivityForResult(intent, AFTER_DATA_REQUEST_CODE);
                //startActivity(intent);
                //finish();
            }
        });

        final Button saveButton = (Button) findViewById(net.FitnessDuo.combination2.R.id.btn_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap;
                ViewGroup v1 = findViewById(net.FitnessDuo.combination2.R.id.compare_layout);


                v1.setDrawingCacheEnabled(true);
                bitmap = Bitmap.createBitmap(v1.getDrawingCache());
                v1.setDrawingCacheEnabled(false);

                //Fix the background color

                if(isStoragePermissionGranted()){
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(photoFile != null){
                        if (photoFile.exists())
                            photoFile.delete();
                        try {
                            FileOutputStream out = new FileOutputStream(photoFile);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                            out.flush();
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        final Uri contentUri = Uri.fromFile(photoFile);
                        scanIntent.setData(contentUri);
                        sendBroadcast(scanIntent);
                        //toastMessage("Saved!");
                        toastMessage("Screenshot Saved to: " + ".../FitnessPhotos");
                    }
                }


            }
        });


        //Intent receivedIntent = getIntent();
        //beforeOrAfter = receivedIntent.getIntExtra("beforeOrAfter", 0);

        beforeWeightText = (TextView) findViewById(net.FitnessDuo.combination2.R.id.before_weight_textView);
        beforeDateText = (TextView) findViewById(net.FitnessDuo.combination2.R.id.before_date_textView);
        beforeImage = (ZoomageView) findViewById(net.FitnessDuo.combination2.R.id.beforeImage);

        afterWeightText = (TextView) findViewById(net.FitnessDuo.combination2.R.id.after_weight_textView);
        afterDateText = (TextView) findViewById(net.FitnessDuo.combination2.R.id.after_date_textView);
        afterImage = (ZoomageView) findViewById(net.FitnessDuo.combination2.R.id.afterImage);


        //Below is for loading the before and after images automatically
        //mDatabaseHelper = new DatabaseHelper(this);
        mDatabaseHelper = DatabaseHelper.getInstance(this);
        Cursor databaseData = mDatabaseHelper.getData();
        ArrayList<String> weights = new ArrayList<>();
        ArrayList<Long> dates = new ArrayList<>();
        ArrayList<String> imagePaths = new ArrayList<>();
        while(databaseData.moveToNext()){
            imagePaths.add(databaseData.getString(1));
            dates.add(databaseData.getLong(2));
            weights.add(databaseData.getString(3));
        }

        if(!weights.isEmpty()){
            beforeWeightText.setText(weights.get(0) + " lbs");
            beforeDateText.setText(getDate(dates.get(0)));
            beforeImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Glide.with(this)
                    .asBitmap()
                    .load(new File(imagePaths.get(0)))
                    .into(beforeImage);

            afterWeightText.setText(weights.get(weights.size()-1) + " lbs");
            afterDateText.setText(getDate(dates.get(dates.size()-1)));
            afterImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Glide.with(this)
                    .asBitmap()
                    .load(new File(imagePaths.get(imagePaths.size()-1)))
                    .into(afterImage);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == BEFORE_DATA_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK){
//                selectedBeforeWeight = data.getStringExtra("weight");
//                beforeWeightText.setText(selectedBeforeWeight);
                beforeWeightText.setText(data.getStringExtra("weight") + " lbs");
                //beforeDateText.setText(data.getStringExtra("date"));
                beforeDateText.setText(getDate(data.getLongExtra("date", 0)));
                beforeImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Glide.with(this)
                        .asBitmap()
                        .load(new File(data.getStringExtra("imagePath")))
                        .into(beforeImage);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        if (requestCode == AFTER_DATA_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK){
//                selectedAfterWeight = data.getStringExtra("weight");
//                afterWeightText.setText(selectedAfterWeight);
                afterWeightText.setText(data.getStringExtra("weight") + " lbs");
                afterDateText.setText(getDate(data.getLongExtra("date", 0)));
                afterImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Glide.with(this)
                    .asBitmap()
                    .load(new File(data.getStringExtra("imagePath")))
                    .into(afterImage);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //outState.putString("message", "This is my message to be reloaded");
        super.onSaveInstanceState(outState);
    }

    //Just toast message
    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

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


    //For taking pictures
    private File createImageFile() throws IOException {
        File storageDir = new File(Environment.getExternalStorageDirectory()+"/FitnessPhotos");
        if (!storageDir.exists())
            storageDir.mkdirs();
        File image = File.createTempFile(
                "example",  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        //mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //For displaying dates
    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd MMM yy", cal).toString();
        return date;
    }


}
