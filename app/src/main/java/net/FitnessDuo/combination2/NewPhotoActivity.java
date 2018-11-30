package net.FitnessDuo.combination2;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewPhotoActivity extends AppCompatActivity {

    //For database. Obviously
    DatabaseHelper mDatabaseHelper;
    private static final String TAG = "NewPhotoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(net.FitnessDuo.combination2.R.layout.activity_new_photo);

        //For Database
        //mDatabaseHelper = new DatabaseHelper(this);
        mDatabaseHelper = DatabaseHelper.getInstance(this);

        final String newStringOfFilePath;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newStringOfFilePath= null;
            } else {
                newStringOfFilePath= extras.getString("String of photo path");
            }
        } else {
            newStringOfFilePath= (String) savedInstanceState.getSerializable("String of photo path");
        }

        //TextView fileNameText = (TextView) findViewById(R.id.textView_fileName);
        //fileNameText.setText(newStringOfFilePath);

        File imageFile = new File(newStringOfFilePath);
        if(imageFile.exists()){
            //New way (To fix rotation)
            ImageView preview = (ImageView) findViewById(net.FitnessDuo.combination2.R.id.imageView);
            Glide.with(this)
                    .load(imageFile)
                    .into(preview);

            //Old way
//            Bitmap myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
//            ImageView preview = (ImageView) findViewById(net.FitnessDuo.combination2.R.id.imageView);
//            myBitmap = rotateBitmap(myBitmap, 90);
//            preview.setImageBitmap(myBitmap);
        }

        TextView todaysDateText = (TextView) findViewById(net.FitnessDuo.combination2.R.id.textView_todaysDate);
        final long time = System.currentTimeMillis();
        todaysDateText.setText(getDate(time));

        Button addEntryButton = (Button) findViewById(net.FitnessDuo.combination2.R.id.button_addEntry);
        addEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(net.FitnessDuo.combination2.R.id.editText_weightEntry);
                String weight = editText.getText().toString();
                if(editText.length() != 0){
                    galleryAddPic(newStringOfFilePath);
                    AddDataToDatabase(newStringOfFilePath, time, weight);
                    Log.d("MYTAG", "onClick: Just added "+newStringOfFilePath+", "+time+", "+weight);
                    Intent viewProgressIntent = new Intent(NewPhotoActivity.this, progressPage.class);
                    startActivity(viewProgressIntent);
                    finish();
                } else {
                    toastMessage("You must enter a weight.");
                }
            }
        });

        //For auto-loading the most recent weight
        //mDatabaseHelper = new DatabaseHelper(this);
        Cursor databaseData = mDatabaseHelper.getData();
        ArrayList<String> weights = new ArrayList<>();
        //ArrayList<String> dates = new ArrayList<>();
        //ArrayList<String> imagePaths = new ArrayList<>();
        try {
            while (databaseData.moveToNext()) {
                //imagePaths.add(databaseData.getString(1));
                //dates.add(databaseData.getString(2));
                weights.add(databaseData.getString(3));
            }
        } finally {
            if (databaseData != null)
                databaseData.close();
        }
        EditText editText = (EditText) findViewById(net.FitnessDuo.combination2.R.id.editText_weightEntry);
        if(!weights.isEmpty()){
            editText.setText(weights.get(weights.size()-1));
        }

    }

    private  Bitmap rotateBitmap(Bitmap source, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    //Not used in this file actually but for adding pictures to the gallery
    //I think it is used in this file actually
    private void galleryAddPic(String mCurrentPhotoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    //For database
    //SHOULD SOON BE (String imageFilename, String date, String weight)
    public void AddDataToDatabase(String newEntry1, Long newEntry2, String newEntry3){
        boolean insertData = mDatabaseHelper.addData(newEntry1, newEntry2, newEntry3);

        if(insertData){
            //toastMessage("Data successfully inserted!");
            toastMessage("Entry Added!");
        } else {
            toastMessage("Something went wrong.");
        }
    }

    //Just toast message
    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    //For displaying dates
    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd MMM yy", cal).toString();
        return date;
    }
}
