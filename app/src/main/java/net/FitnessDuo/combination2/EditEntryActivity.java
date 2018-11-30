package net.FitnessDuo.combination2;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

public class EditEntryActivity extends AppCompatActivity {

    private static final String TAG = "EditEntryActivity";

    private Button btnDelete;
    private TextView dateText, weightText;
    private ImageView entryImage;

    DatabaseHelper mDatabaseHelper;

    private String selectedImagePath, selectedWeight;
    private Long selectedDate;
    private int selectedID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(net.FitnessDuo.combination2.R.layout.activity_edit_entry);
        btnDelete = (Button) findViewById(net.FitnessDuo.combination2.R.id.btnDelete);
        //mDatabaseHelper = new DatabaseHelper(this);
        mDatabaseHelper = DatabaseHelper.getInstance(this);
        dateText = (TextView) findViewById(net.FitnessDuo.combination2.R.id.DateText);
        weightText = (TextView) findViewById(net.FitnessDuo.combination2.R.id.WeightText);
        entryImage = (ImageView) findViewById(net.FitnessDuo.combination2.R.id.editImageView);

        Intent receivedIntent = getIntent();

        selectedID = receivedIntent.getIntExtra("id",-1);

        selectedImagePath = receivedIntent.getStringExtra("imagePath");
        selectedDate = receivedIntent.getLongExtra("date", 0);
        selectedWeight = receivedIntent.getStringExtra("weight");

        dateText.setText(getDate(selectedDate));
        weightText.setText(selectedWeight + " lbs");
        Glide.with(this)
                .asBitmap()
                .load(new File(selectedImagePath))
                .into(entryImage);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseHelper.deleteEntry(selectedID, selectedImagePath, selectedDate, selectedWeight);
                //deleteMyFile(selectedImagePath); Deletes the photo file from your phone. Not sure if I want to do that.
                UpdateGallery(selectedImagePath);
                //toastMessage("removed from database");
                toastMessage("Entry Removed.");
                finish();
            }
        });
    }

    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void deleteMyFile(String fullFilePath){
        File fdelete = new File(fullFilePath);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                Log.d(TAG, "deleteFile: " + "file Deleted :" + fullFilePath);
            } else {
                Log.d(TAG, "deleteFile: " + "file not Deleted :" + fullFilePath);
            }
        }
    }

    public void UpdateGallery(String filePath) {
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(filePath))));
    }

    //For displaying dates
    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd MMM yy", cal).toString();
        return date;
    }
}
