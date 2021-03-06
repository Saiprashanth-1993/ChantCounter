/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.courtcounter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * This activity keeps track of the basketball score for 2 teams.
 */
public class MainActivity extends AppCompatActivity {

    public static final int MIN_VALUE = 0;

    private static final String PREF_KEEP_SCREEN_ON = "keepScreenOn";

    private static final String PREF_THEME = "theme";

    private static final String THEME_DARK = "dark";

    private static final String THEME_LIGHT = "light";

    private static final long DEFAULT_VIBRATION_DURATION = 20; // Milliseconds

    private static int countScore = 0;

    SharedPreferences sharedPref;

    String Count = "Count";

    boolean doubleBackToExitPressedOnce = false;

    private Vibrator vibrator;

    private Button decrementButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPref.getString(PREF_THEME, THEME_LIGHT).equals(THEME_DARK)) {
            setTheme(R.style.AppTheme_Dark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        decrementButton = (Button) findViewById(R.id.decrement);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        sharedPref = getPreferences(MODE_PRIVATE);
        countScore = sharedPref.getInt(Count, 0);

        displayCountValue(countScore);
    }

    /**
     * Displays the given score for Team A.
     */
    public void displayCountValue(int score) {
        TextView scoreView = (TextView) findViewById(R.id.team_a_score);
        scoreView.setText(String.valueOf(score));
        checkStateOfButtons();
    }

    private void checkStateOfButtons() {
        if (countScore <= MIN_VALUE) decrementButton.setEnabled(false);
        else decrementButton.setEnabled(true);
    }

   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event) || this.onKeyDown(keyCode, event);
    }
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage("Do you want to exit the App ?").setCancelable(false)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.finishAffinity(MainActivity.this);
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();

        dialog.show();

        /*if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK twice to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPref.getBoolean(PREF_KEEP_SCREEN_ON, false)) {
            getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void vibrate(long duration) {
        if (sharedPref.getBoolean("vibrationOn", true)) {
            vibrator.vibrate(duration);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event, View view) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (sharedPref.getBoolean("hardControlOn", true)) {
                    addOneCount(view);
                    return true;
                }
                return false;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (sharedPref.getBoolean("hardControlOn", true)) {
                    reduceOneCount(view);
                    return true;
                }
                return false;
            case KeyEvent.KEYCODE_CAMERA:
                if (sharedPref.getBoolean("hardControlOn", true)) {
                    resetScore(view);
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    /**
     * Increase the score for Team A by 1 point.
     */
    public void addOneCount(View v) {
        countScore = countScore + 1;
        displayCountValue(countScore);
        vibrate(DEFAULT_VIBRATION_DURATION);
        sharedPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(Count, countScore);
        editor.commit();
        return;
    }

    public void reduceOneCount(View v) {
        countScore = countScore - 1;
        displayCountValue(countScore);
        vibrate(DEFAULT_VIBRATION_DURATION + 20);
        sharedPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(Count, countScore);
        editor.commit();
        return;
    }

    /**
     * Resets the score for both teams back to 0.
     */
    public void resetScore(View v) {
        countScore = 0;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(Count, countScore);
        editor.commit();
        displayCountValue(countScore);
    }
}
