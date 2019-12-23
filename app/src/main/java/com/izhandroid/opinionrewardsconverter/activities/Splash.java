/*
 * Copyright (c) 2019. Izhan Ali. Accessing/leaking/modifying source codes is prohibited. Legal action will be taken for if you do such things.
 */

package com.izhandroid.opinionrewardsconverter.activities;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.izhandroid.opinionrewardsconverter.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Splash extends AppCompatActivity {


    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private final Handler mHideHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        /**
         * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        int AUTO_HIDE_DELAY_MILLIS = 2200;
        mHideHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (user == null) {
                    finish();
                    startActivity(new Intent(Splash.this, Registration.class));
                } else {
                    finish();
                    startActivity(new Intent(Splash.this, MainActivity.class));
                }
            }
        }, AUTO_HIDE_DELAY_MILLIS);

    }


}
