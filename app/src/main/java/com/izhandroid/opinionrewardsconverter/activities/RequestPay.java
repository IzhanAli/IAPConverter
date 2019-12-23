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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.izhandroid.opinionrewardsconverter.R;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.List;

public class RequestPay extends AppCompatActivity {
FirebaseAuth auth;
FirebaseUser user;
EditText txttr, det;
Spinner spinner;
boolean nothing;
TextInputLayout txtlay, detlay;
MaterialButton btn;
String trid, num, query, issue;
InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_pay);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.help, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.current_status_adnative);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               query = adapterView.getItemAtPosition(i).toString();
                nothing = query.equals("SELECT ISSUE");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                nothing = true;

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

        det = findViewById(R.id.reqpay_txtinfo);
        detlay = findViewById(R.id.reqpay_layinfo);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               trid= txttr.getText().toString();
               String info = det.getText().toString();
               if(nothing){
                   FancyToast.makeText(RequestPay.this,"Please select issue first", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();

               }else if(trid.isEmpty()){
                   txtlay.setError("Transaction id can't be empty");

               }else if(info.isEmpty()){
                   detlay.setError("Please enter your query details");
               } else {
                   txtlay.setError(null);
                   detlay.setError(null);
                   submit(info);

               }
            }
        });




    }

    private void submit(String inf){

        Intent email = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "playrewardsconverter@gmail.com", null));
        //email.setData(Uri.parse("mailto:playrewardsconverter@gmail.com"));
        email.putExtra(Intent.EXTRA_EMAIL, "playrewardsconverter@gmail.com");
        email.putExtra(Intent.EXTRA_SUBJECT, "TR ID:"+trid+" &&User:"+num+" &&Type:"+query);
        email.putExtra(Intent.EXTRA_TEXT, trid+"\n"+inf+"\n\n");
        startActivity(email);
    }

}
