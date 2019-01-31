package com.thesis.temilola.feid;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;


public class MainActivity extends AppCompatActivity {

    private final static String LOG_TAG = MainActivity.class.getCanonicalName(); //String for LOGGING.
    private static final int REQUEST_PERMISSION_WRITE = 100;
    private Spinner mAppTypeSpinner; //The Spinner (drop down selector) that you choose which app to work on.
    //    private Button mExtractButton; //Button the user presses to perform the extraction.
    ArrayAdapter<CharSequence> mAdapter; //This 'Adapts' the Array of CharSequence to make it usable by the mAppTypeSpinner.
    private boolean permissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeUI();
    }

    /**
     * This method sets up/gets reference to the UI components
     */
    private void initializeUI(){
        // Set the layout
        setContentView(R.layout.activity_main);

        // Initialize the views.
        mAppTypeSpinner = findViewById(R.id.appTypeSpinner);

        //Setup the dropdown
        //Initialize the adapter
        mAdapter = ArrayAdapter.createFromResource(this, R.array.app_type, android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Associate the ArrayAdapter with the Spinner.
        mAppTypeSpinner.setAdapter(mAdapter);

        //After layouts are handled we can request permissions if they haven't been already
        if(!permissionGranted){
            checkPermissions();
        }
    }

    /**
     * This method is called when the button is pressed
     * @param buttonPress
     */
    public void buttonPressed(View buttonPress){
        Log.i(LOG_TAG, "Start buttonPressed()");
        String appSelected = mAppTypeSpinner.getSelectedItem().toString();
        String alertTitle, alertMessage, appType;
        if (appSelected.equalsIgnoreCase("Select One...")){
            Log.i(LOG_TAG, appSelected +" has been selected");
            //Create an Error Alert Dialog to be output to the user
            alertTitle = "Error";
            alertMessage = "Please select one application";
            callAlertDialog(alertTitle, alertMessage);
        }else if(appSelected.equalsIgnoreCase("Amazon Alexa")){
            Log.i(LOG_TAG, appSelected +" has been selected");
            //Call the alert dialog
            alertTitle = "Confirmation";
            alertMessage = "Are you sure you want to extract Amazon Alexa";
            appType = "Amazon Alexa";
            callAppAlertDialog(alertTitle, alertMessage, appType);
        }else if(appSelected.equalsIgnoreCase("FitBit")){
            Log.i(LOG_TAG, appSelected +" has been selected");
            //Call the alert dialog
            alertTitle = "Confirmation";
            alertMessage = "Are you sure you want to extract FitBit";
            appType = "FitBit";
            callAppAlertDialog(alertTitle, alertMessage, appType);
        }
        Log.i(LOG_TAG, "End buttonPressed()");
    }

    /**
     *
     * @param alertTitle
     * @param alertMessage
     */
    private void callAlertDialog(String alertTitle, String alertMessage){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(alertMessage)
                .setTitle(alertTitle)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked OK button
                        dialogInterface.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        Log.i(LOG_TAG, "Error Alert Dialog Displayed");
    }

    /**
     *
     * @param alertTitle
     * @param alertMessage
     * @param appType
     */
    private void callAppAlertDialog(String alertTitle, String alertMessage, final String appType){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(alertMessage)
                .setTitle(alertTitle)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked Cancel button
                        dialogInterface.cancel();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked OK button
                        if(appType.equalsIgnoreCase("FitBit")){
                            extractFitBit(appType);
                        }else if (appType.equalsIgnoreCase("Amazon Alexa")){
                            extractAmazonAlexa(appType);
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        Log.i(LOG_TAG, "Alert Dialog Displayed");
    }

    /**
     * This method check if the output folder exists and creates it if it doesn't exist
     *
     * @param appName
     * @return
     */
    private String directoryCheck(String appName){

        Log.i(LOG_TAG, "directoryCheck Function Called");
        String outputPath = Environment.getExternalStorageDirectory().getPath()+"/FEID/"+appName;

        Log.i(LOG_TAG, outputPath);

        File dir = new File(outputPath);
        if(!dir.exists()){
            if(dir.mkdirs()) {
                Log.i(LOG_TAG, "Directory Created");
                Toast.makeText(this, "New Directory created", Toast.LENGTH_LONG).show();
            }else{
                Log.e(LOG_TAG, "Directory NOT Created");
                Toast.makeText(this, "Directory can't be created", Toast.LENGTH_LONG).show();
            }
        }else{
            Log.i(LOG_TAG, "Directory already exists");
            Toast.makeText(this, "Directory already exists", Toast.LENGTH_LONG).show();
        }
        return outputPath;
    }

    /**
     * This method is called when the extraction process for Amazon Alexa is to begin
     * param
     */
    private void extractFitBit(String appName){
        Log.i(LOG_TAG, "FitBit Function Called");
        String outputPath, rootPath, databasePath;
        File rootFile, databaseFile, filesFile;
        outputPath = directoryCheck(appName);

        rootPath = Environment.getDataDirectory()+"/data/com.fitbit.FitbitMobile/";

        Log.i(LOG_TAG, rootPath);
        rootFile = new File(rootPath);
        if(!rootFile.exists()){
            Log.e(LOG_TAG, "The Application folder does not exist");
        }else {
            Log.i(LOG_TAG, "The Application folder exist");
        }


        databasePath = Environment.getDataDirectory()+"/data/com.fitbit.FitbitMobile/databases";
        Log.i(LOG_TAG, databasePath);
        databaseFile = new File(databasePath);
        if(!databaseFile.exists()){
            Log.e(LOG_TAG, "The Application Database folder does not exist");
        }else {
            Log.i(LOG_TAG, "The Application Database folder exist");
        }
    }

    /**
     * This method is called when the extraction process for Amazon Alexa is to begin
     * param
     */
    private void extractAmazonAlexa(String appName){
        Log.i(LOG_TAG, "Amazon Alexa Function Called");
        String outputPath, rootPath, databasePath, filesPath;
        File rootFile, databaseFile, filesFile;
        outputPath = directoryCheck(appName);


        rootPath = Environment.getDataDirectory()+"/data/com.amazon.dee.app/";
        Log.i(LOG_TAG, rootPath);
        rootFile = new File(rootPath);
        if(!rootFile.exists()){
            Log.e(LOG_TAG, "The Application folder does not exist");
            return;
        }else {
            Log.i(LOG_TAG, "The Application folder exist");
        }


//        databasePath = Environment.getDataDirectory()+"/data/com.amazon.dee.app/databases";
//        Log.i(LOG_TAG, databasePath);
//        databaseFile = new File(databasePath);
//        if(!databaseFile.exists()){
//            Log.e(LOG_TAG, "The Application Database folder does not exist");
//            //return;
//        }else {
//            Log.i(LOG_TAG, "The Application Database folder exist");
//        }
//
//        filesPath = rootPath + "files/";
//        Log.i(LOG_TAG, filesPath);
//        filesFile = new File(filesPath);
//        if(!filesFile.exists()){
//            Log.e(LOG_TAG, "The Application does not have any file");
//            //return;
//        }else {
//            Log.i(LOG_TAG, "The Application have files");
//        }

    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    // Initiate request for permissions.
    private boolean checkPermissions() {

        if (!isExternalStorageReadable() || !isExternalStorageWritable()) {
            Toast.makeText(this, "This app only works on devices with usable external storage",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_WRITE);
            return false;
        } else {
            return true;
        }
    }

    // Handle permissions result
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_WRITE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true;
                    Toast.makeText(this, "External storage permission granted",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "You must grant permission!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
