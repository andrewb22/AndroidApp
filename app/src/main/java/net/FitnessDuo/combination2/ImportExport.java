package net.FitnessDuo.combination2;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class ImportExport extends AppCompatActivity {

    DatabaseHelper mDatabaseHelper;
    File CSVfileToImport;
    private String m_Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(net.FitnessDuo.combination2.R.layout.activity_import_export);

        //mDatabaseHelper = new DatabaseHelper(this);
        mDatabaseHelper = DatabaseHelper.getInstance(this);

        Button makeFileButton = (Button) findViewById(net.FitnessDuo.combination2.R.id.exportButton);
        makeFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStoragePermissionGranted()){
                    File testExportTextFile = null;
                    try {
                        testExportTextFile = createTextFile("November29Test");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    if (testExportTextFile != null){

                    }
                }
            }
        });

        Button importFileButton = (Button) findViewById(net.FitnessDuo.combination2.R.id.importButton);
        importFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    EditText etFN = (EditText) findViewById(net.FitnessDuo.combination2.R.id.editTextImportFilename);
                    String csvFileName = etFN.getText().toString();
                    String fullCSVFileName = Environment.getExternalStorageDirectory() + "/FitnessPhotos" + "/" + csvFileName;
                    File csvfile = new File(fullCSVFileName);
                    CSVReader reader = new CSVReader(new FileReader(fullCSVFileName));
                    String [] nextLine;
                    //int numLinesCounter = 0;
                    while ((nextLine = reader.readNext()) != null) {
                        // nextLine[] is an array of values from the line
                        //System.out.println(nextLine[0] + nextLine[1] + "etc...");
                        //Log.d(TAG, "onClick: " + nextLine[0] + nextLine[1] + "etc...");
                        //Log.d(TAG, "onClick: " + nextLine[0]);
                        AddData(nextLine[0], Long.parseLong(nextLine[1]), nextLine[2]);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    //Toast.makeText(this, "The specified file was not found", Toast.LENGTH_SHORT).show();
                    toastMessage("File Not Found.");
                }
            }
        });

        Button fileExploreButton = (Button) findViewById(R.id.fileExploreButton);
        fileExploreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();
            }
        });

        Button dialogBoxButton = (Button) findViewById(R.id.dialogBoxButton);
        dialogBoxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBox();
            }
        });
    }

    //For storage
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

    //For text file
    private File createTextFile(String name) throws IOException {
        File storageDir = new File(Environment.getExternalStorageDirectory()+"/FitnessPhotos");
        if (!storageDir.exists())
            storageDir.mkdirs();
//        File textFile = File.createTempFile(
////                "FitnessPhotosData_",  /* prefix */
////                //".txt",         /* suffix */
////                ".csv",         /* suffix */
////                storageDir      /* directory */
////      );
        File textFile = new File (storageDir+"/"+name+".csv");
        //mCurrentPhotoPath = textFile.getAbsolutePath();
        //NEW STUFF
        try
        {
            textFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(textFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

            Cursor data = mDatabaseHelper.getData();
            ArrayList<String> listDataPhotoPaths = new ArrayList<>();
            ArrayList<String> listDataDates = new ArrayList<>();
            ArrayList<String> listDataWeights = new ArrayList<>();
            while(data.moveToNext()){
                //Get the value from the database in column 1
                //Then add it to the ArrayList
                listDataPhotoPaths.add(data.getString(1));
                listDataDates.add(data.getString(2));
                listDataWeights.add(data.getString(3));
            }
            for (int i=0; i<listDataPhotoPaths.size(); i++){
                myOutWriter.append(listDataPhotoPaths.get(i) + "," + listDataDates.get(i) + "," + listDataWeights.get(i) + "\n");
            }


            myOutWriter.close();

            fOut.flush();
            fOut.close();
            toastMessage("Completed.");
        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }
        EditText recentFileNameET = (EditText) findViewById(net.FitnessDuo.combination2.R.id.editTextRecentFilename);
        String textFileName = textFile.toString().substring(textFile.toString().lastIndexOf("/") + 1);
        recentFileNameET.setText(textFileName);
        return textFile;
    }

    //Toast method
    private void toastMessage(String message){
        //Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void AddData(String newEntryPhotoPath, Long newEntryDate, String newEntryWeight){
        boolean insertData = mDatabaseHelper.addData(newEntryPhotoPath, newEntryDate, newEntryWeight);

        if(insertData){
            toastMessage("Data successfully inserted!");
        } else {
            toastMessage("Something went wrong");
        }
    }

    @Override
    public void onBackPressed()
    {
        NavUtils.navigateUpFromSameTask(this);
        super.onBackPressed();
    }

    private static final int READ_REQUEST_CODE = 29;

    public void performFileSearch(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        //intent.setType("text/csv");
        intent.setType("*/*");
        startActivityForResult(intent.createChooser(intent, "Open CSV"), READ_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == READ_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                CSVfileToImport = new File(data.getData().getPath());
                EditText recentFileNameET = (EditText) findViewById(net.FitnessDuo.combination2.R.id.editTextRecentFilename);
                String textFileName = CSVfileToImport.getName();
                recentFileNameET.setText(textFileName);
                try{
                    String fullCSVFileName = Environment.getExternalStorageDirectory() + "/FitnessPhotos" + "/" + textFileName;
                    File csvfile = new File(fullCSVFileName);
                    CSVReader reader = new CSVReader(new FileReader(fullCSVFileName));
                    String [] nextLine;
                    //int numLinesCounter = 0;
                    while ((nextLine = reader.readNext()) != null) {
                        // nextLine[] is an array of values from the line
                        //System.out.println(nextLine[0] + nextLine[1] + "etc...");
                        //Log.d(TAG, "onClick: " + nextLine[0] + nextLine[1] + "etc...");
                        //Log.d(TAG, "onClick: " + nextLine[0]);
                        AddData(nextLine[0], Long.parseLong(nextLine[1]), nextLine[2]);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    //Toast.makeText(this, "The specified file was not found", Toast.LENGTH_SHORT).show();
                    toastMessage("File Not Found.");
                }
            }
        }

    }

    private void dialogBox(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title");
        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                toastMessage(m_Text);
                if(isStoragePermissionGranted()){
                    File testExportTextFile = null;
                    try {
                        testExportTextFile = createTextFile(m_Text);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    if (testExportTextFile != null){

                    }
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
