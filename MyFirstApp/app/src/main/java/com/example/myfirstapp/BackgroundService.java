package com.example.myfirstapp;

import android.Manifest;
import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.jar.Attributes;

import static android.R.attr.name;

/**
 * Created by zaidalattar on 11/19/16.
 */

public class BackgroundService extends Service implements LocationListener {
    // Must create a default constructor

    private PowerManager pm;
    private boolean isScreenOn;
    private String oldPassiveProvider;
    private String newPassiveProvider;
    private Calendar calendar;
    private Date date;
    private String screenStatusStr;


    //log stuff
    File externalStorageDir;
    File internalStorageDir;
    File myFile;

    FileOutputStream fostream;
    OutputStreamWriter oswriter;
    BufferedWriter bwriter;



    @Override
    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.

        //initialize a power manager to check screen status later
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        //initialize a location manager and select passive provider
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1000, 0, this);


        //go ahead and open the file
        openfile();

    }
    @Override
    public void onDestroy()
    {

        closefile();
    }



    public void openfile() {

        externalStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        myFile = new File(externalStorageDir, "/log.txt");


        if (myFile.exists()) {
            try {
                fostream = new FileOutputStream(myFile, true);
                oswriter = new OutputStreamWriter(fostream);
                bwriter = new BufferedWriter(oswriter);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                myFile.createNewFile();
                fostream = new FileOutputStream(myFile, true);
                oswriter = new OutputStreamWriter(fostream);
                bwriter = new BufferedWriter(oswriter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void closefile() {

        try {
            bwriter.close();
            oswriter.close();
            fostream.close();
        } catch (IOException e) {
        }

    }

    public void writefile(boolean screenStatus, Date timestamp, String provider_name) {

        try {
            if (screenStatus == true) {
                screenStatusStr = "ON";
            } else {
                screenStatusStr = "OFF";
            }
            bwriter.append("S: " + screenStatusStr + ". T: " + timestamp + ". Src: " + provider_name);
            bwriter.newLine();
            bwriter.flush();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }




    @Override
    public void onLocationChanged(Location location) {
        boolean isScreen;
        String provider = location.getProvider();
        isScreen = pm.isInteractive();
        calendar = Calendar.getInstance();
        date = calendar.getTime();

        //When the location changes,
        //log Screen Status, time stamp, and the location provider (GPS, Network, and etc)
        writefile(isScreen, date, provider);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}


