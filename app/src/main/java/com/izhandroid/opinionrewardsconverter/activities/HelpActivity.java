/*
 * Copyright (c) 2019. Izhan Ali. Accessing/leaking/modifying source codes is prohibited. Legal action will be taken for if you do such things.
 */

package com.izhandroid.opinionrewardsconverter.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.appbar.MaterialToolbar;
import com.izhandroid.opinionrewardsconverter.R;

public class HelpActivity extends AppCompatActivity {

    MaterialToolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        toolbar =  findViewById(R.id.toolbarl);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button cont = findViewById(R.id.contbtn);
        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
                        "playrewardsconverter@gmail.com", null));


                email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                email.putExtra(Intent.EXTRA_TEXT, "Hello, \n");
                startActivity(email);
            }
        });


    }


    public void tnc(View view) {
        Intent in = new Intent(HelpActivity.this, TCs.class);
        startActivity(in);
    }
}
