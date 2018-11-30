package net.FitnessDuo.combination2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //For taking pictures
    static final int REQUEST_IMAGE_CAPTURE =1;
    String mCurrentPhotoPath;
    private TextView testTextView, viewProgressDescription, compareDescription, graphDescription;
    DatabaseHelper mDatabaseHelper; //For knowing how many pictures you have taken

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(net.FitnessDuo.combination2.R.layout.activity_main);

        testTextView = (TextView) findViewById(R.id.testTextViewID);
        viewProgressDescription = (TextView) findViewById(R.id.viewProgressDescription);
        compareDescription = (TextView) findViewById(R.id.compareDescription);
        graphDescription = (TextView) findViewById(R.id.graphDescription);
        testTextView.setText("Hello World");
        //Check if the user has taken any pictures yet
        mDatabaseHelper = DatabaseHelper.getInstance(this);
        Cursor databaseData = mDatabaseHelper.getData();
        ArrayList<String> imagePaths = new ArrayList<>();
        while(databaseData.moveToNext()){
            imagePaths.add(databaseData.getString(1));
        }
        final int numPics = imagePaths.size();
        String stringToShow = Integer.toString(numPics);
        testTextView.setText("Pictues Taken: "+stringToShow);
        testTextView.setVisibility(View.INVISIBLE); //For Debugging, set this to not invisible
        if(numPics>0){
            viewProgressDescription.setVisibility(View.INVISIBLE);
        }
        if(numPics>1){
            compareDescription.setVisibility(View.INVISIBLE);
            graphDescription.setVisibility(View.INVISIBLE);
        }

        Button photoButton = (Button) findViewById(net.FitnessDuo.combination2.R.id.photoButton);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        Button viewProgressButton = (Button) findViewById(net.FitnessDuo.combination2.R.id.button_viewProgress);
        viewProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numPics==0){
                    toastMessage("No progress to view! Take your first picture.");
                }
                else {
                    Intent intent = new Intent(MainActivity.this, progressPage.class);
                    intent.putExtra("From Comparison", 0);
                    startActivity(intent);
                }
            }
        });

        Button compareButton = (Button) findViewById(net.FitnessDuo.combination2.R.id.button_compare);
        compareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numPics<2){
                    toastMessage("Not enough pictures to compare! Take your first 2 pictures first.");
                }
                else {
                    Intent intent = new Intent(MainActivity.this, Compare.class);
                    startActivity(intent);
                }
            }
        });

        Button graphButton = (Button) findViewById(net.FitnessDuo.combination2.R.id.button_graph);
        graphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numPics<2){
                    toastMessage("Not enough data! Take your first 2 pictures first.");
                }
                else {
                    Intent intent = new Intent(MainActivity.this, GraphActivity.class);
                    startActivity(intent);
                }
            }
        });

        Button importExportButton = (Button) findViewById(net.FitnessDuo.combination2.R.id.importExportButton);
        importExportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ImportExport.class);
                startActivity(intent);
            }
        });

    }

    //For taking pictures
    private void dispatchTakePictureIntent() {
        if(isStoragePermissionGranted()){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }

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
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //For taking pictures
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            //mImageView.setImageBitmap(imageBitmap);

            //galleryAddPic();

            Intent goToPreview = new Intent(this, NewPhotoActivity.class);
            //goToPreview.putExtra("photo", imageBitmap);
            goToPreview.putExtra("String of photo path",mCurrentPhotoPath);
            startActivity(goToPreview);
        }
    }

//    //Not used in this file actually but for adding pictures to the gallery
//    private void galleryAddPic() {
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        File f = new File(mCurrentPhotoPath);
//        Uri contentUri = Uri.fromFile(f);
//        mediaScanIntent.setData(contentUri);
//        this.sendBroadcast(mediaScanIntent);
//    }

    //For taking pictures
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.d("DEBUG","Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }

    //Just toast message
    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
