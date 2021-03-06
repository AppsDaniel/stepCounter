package com.example.macbookair.stepcounter;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static int steps = 0;
    TextView showStep, personsName;
    private DatabaseHandler sqlFinder;
    ChangeActivities changeActivities;
    private ProgressBar progressBar;
    String dateTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sqlFinder = new DatabaseHandler(this);
        sqlFinder.insert();

        String checkTodaysDate = sqlFinder.findTodaysDate();
        dateTime = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());

        if (checkTodaysDate.equals(dateTime)) {

            startService(new Intent(this, StepService.class));

        } else {

//            String name = sqlFinder.findNameYesterday();
//            String stepGoal = sqlFinder.findStepGoalYesterday();
//            int goal;

            StepCounter step = sqlFinder.createDay(steps, dateTime);
            startService(new Intent(this, StepService.class));

//            if (name.isEmpty()) {
//
//                name = "Name";
//                goal = 1000;
//                StepCounter something = sqlFinder.addProfile(name, goal);
//
//            } else {
//                goal = Integer.parseInt(stepGoal);
//                StepCounter something = sqlFinder.addProfile(name, goal);
//            }
        }
        showStep = findViewById(R.id.amountSteps);
//        personsName = findViewById(R.id.personsName);

//        personsName.setText(sqlFinder.findName().toString());

        Thread upDateTextView = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String findSteps = sqlFinder.findSteps();
                                showStep.setText(findSteps);
                                progressBar = findViewById(R.id.progressBarSteps);
                                int stepsTaken = Integer.parseInt(findSteps);

                                progressBar(stepsTaken);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        upDateTextView.start();


        showDebugDBAddressLogToast(this);

    }

    private void progressBar(int stepsTaken) {

//        String getGoal = sqlFinder.getGoal();
        int goal = 10;
//        int changegetGoal = Integer.parseInt(getGoal);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String goalStep = sharedPreferences.getString("goalstep", "0");
        int steps = Integer.parseInt(goalStep);


//        if (changegetGoal != 0) {
//            int setGoal = changegetGoal / 100;
//            int progress = stepsTaken / setGoal;
//
//            progressBar.setProgress(progress);
//        } else {
//
//            int progress = stepsTaken / goal;
//            progressBar.setProgress(progress);
//        }
        if (steps != 0) {
            int setGoal = steps / 100;
            int progress = stepsTaken / setGoal;

            progressBar.setProgress(progress);
        } else {

            int progress = stepsTaken / goal;
            progressBar.setProgress(progress);
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        sqlFinder.insert();

    }

    @Override
    protected void onPause() {
        super.onPause();
        sqlFinder.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == R.id.historik) {

            ChangeActivities.StartNewActivity(MainActivity.this, HistoryList.class);

        }
        if (item.getItemId() == R.id.profil) {

            ChangeActivities.StartNewActivity(MainActivity.this, Profile.class);

        }
        if (item.getItemId() == R.id.action_settings) {

            ChangeActivities.StartNewActivity(MainActivity.this, UserSettings.class);

        }

        return super.onOptionsItemSelected(item);
    }

    public static void showDebugDBAddressLogToast(Context context) {
        if (BuildConfig.DEBUG) {
            try {
                Class<?> debugDB = Class.forName("com.amitshekhar.DebugDB");
                Method getAddressLog = debugDB.getMethod("getAddressLog");
                Object value = getAddressLog.invoke(null);
                Toast.makeText(context, (String) value, Toast.LENGTH_LONG).show();

            } catch (Exception ignore) {

            }
        }
    }

}

