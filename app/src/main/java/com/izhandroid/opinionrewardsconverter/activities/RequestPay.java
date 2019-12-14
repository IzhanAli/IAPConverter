/*
 * Copyright (c) 2019. Izhan Ali. Accessing/leaking/modifying source codes is prohibited. Legal action will be taken for if you do such things.
 */

package com.izhandroid.opinionrewardsconverter.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.izhandroid.opinionrewardsconverter.R;

public class RequestPay extends AppCompatActivity {
FirebaseAuth auth;
FirebaseUser user;
EditText txttr;
TextInputLayout txtlay;
MaterialButton btn;
String trid, num;
InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_pay);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6711729529292720/9924960825");
        //TODO ad here
        //mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                submit();
                super.onAdClosed();
            }
        });
        ExtendedFloatingActionButton fab = findViewById(R.id.extendedFloatingActionButtonreq);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(RequestPay.this, HelpActivity.class);
                startActivity(i);

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        num = user.getPhoneNumber();

        btn = findViewById(R.id.btnsub);
        txttr = findViewById(R.id.reqpay_txt);
        txtlay = findViewById(R.id.reqpay_lay);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               trid= txttr.getText().toString();
               if(trid.isEmpty()){
                   txtlay.setError("Transaction id can't be empty");
               }else {
                   txtlay.setError(null);

                   if(mInterstitialAd.isLoaded()) {
                      mInterstitialAd.show();

                   }else {
                       submit();
                   }
               }
            }
        });




    }

    private void submit(){

        Intent email = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "playrewardsconverter@gmail.com", null));
        //email.setData(Uri.parse("mailto:playrewardsconverter@gmail.com"));
        email.putExtra(Intent.EXTRA_EMAIL, "playrewardsconverter@gmail.com");
        email.putExtra(Intent.EXTRA_SUBJECT, "TR ID:"+trid+" &&User:"+num);
        email.putExtra(Intent.EXTRA_TEXT, trid+"\n Enter your issue below:\n\n");
        startActivity(email);
    }

}
