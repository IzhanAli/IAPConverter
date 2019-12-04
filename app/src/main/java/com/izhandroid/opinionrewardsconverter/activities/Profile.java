/*
 * Copyright (c) 2019. Izhan Ali. Accessing/leaking/modifying source codes is prohibited. Legal action will be taken for if you do such things.
 */

package com.izhandroid.opinionrewardsconverter.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.izhandroid.opinionrewardsconverter.R;
import com.izhandroid.opinionrewardsconverter.adapters.UserInfo;
import com.izhandroid.opinionrewardsconverter.utils.Constants;
import com.shashank.sony.fancytoastlib.FancyToast;

import static com.izhandroid.opinionrewardsconverter.utils.Constants.MY_PREFS_NAME;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.dbuser;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.isOnline;

public class Profile extends AppCompatActivity {
    EditText pay;
    TextInputLayout paylay;
    ChipGroup radioGroupone;
    Chip paytm, paypal, upi, phonepe, gpay;
    RadioButton paypalintl;
    Button btnsubmit;
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    FirebaseAuth auth;
    String username, useremail, paymethod, payinfo, usercountry, usertype;
    FirebaseUser user;
    TextView textView;
    ProgressBar progressBar;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ProgressDialog progressDialog = Constants.DialogUtils.showprgdialog(this);
        if(isOnline()){
            progressDialog.dismiss();
            LinearLayout mlinearLayout = findViewById(R.id.profilemainlayout);
            mlinearLayout.setVisibility(View.VISIBLE);
            LinearLayout errorlay = findViewById(R.id.profileerror);
            errorlay.setVisibility(View.INVISIBLE);
        }else {
            progressDialog.dismiss();
            LinearLayout mlinearLayout = findViewById(R.id.profilemainlayout);
            mlinearLayout.setVisibility(View.VISIBLE);
            LinearLayout errorlay = findViewById(R.id.profileerror);
            errorlay.setVisibility(View.INVISIBLE);
        }


        usercountry = getIntent().getStringExtra("country");
        username = getIntent().getStringExtra("name");
        useremail = getIntent().getStringExtra("email");

        pref = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = pref.edit();

        editor.putBoolean("payment submitted", false).apply();


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (auth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, Registration.class));
        }


        databaseReference = FirebaseDatabase.getInstance().getReference();

        progressBar = findViewById(R.id.payprg);
        pay = findViewById(R.id.paymentdetails);
        btnsubmit = findViewById(R.id.submituser);
        radioGroupone = findViewById(R.id.radiogrp1);

        paytm = findViewById(R.id.but_paytm);
        phonepe = findViewById(R.id.but_phonepe);
        gpay = findViewById(R.id.but_gpay);
        paypal = findViewById(R.id.but_indpaypal);
        upi = findViewById(R.id.but_upi);
        paypalintl = findViewById(R.id.intl_paypal);

        paylay = findViewById(R.id.paylay);
        usercountry = pref.getString("country", null);



        btnsubmit.setText("Skip this");

        paymethod = "not";
        payinfo = "not";

        pay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                paylay.setError(null);
                if (pay.length() > 0) {
                    if (anyonechecked()) {
                        btnsubmit.setText("Submit");

                    } else {
                        btnsubmit.setText("Skip this");

                    }
                } else {
                    btnsubmit.setText("Skip this");

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        if (usercountry.equals("India")) {
            radioGroupone.setVisibility(View.VISIBLE);
            paypalintl.setVisibility(View.GONE);
        } else {
            radioGroupone.setVisibility(View.GONE);
            paypalintl.setVisibility(View.VISIBLE);
        }

        radioGroupone.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {

                paylay.setError(null);
                if (anyonechecked()) {
                    if (pay.length() > 0) {
                        btnsubmit.setText("Submit");
                    } else {
                        btnsubmit.setText("Skip this");
                    }
                } else {
                    btnsubmit.setText("Skip this");
                }
                if (paytm.isChecked()) {
                    pay.setHint("Enter Paytm Number");
                    pay.setInputType(InputType.TYPE_CLASS_NUMBER);
                    paylay.setHelperText("Paytm Number");
                    pay.setVisibility(View.VISIBLE);
                    btnsubmit.setVisibility(View.VISIBLE);
                    paymethod = "Paytm";
                } else if (gpay.isChecked()) {
                    pay.setHint("Enter Google Pay Number");
                    pay.setInputType(InputType.TYPE_CLASS_NUMBER);
                    paylay.setHelperText("Google Pay Number");
                    pay.setVisibility(View.VISIBLE);
                    btnsubmit.setVisibility(View.VISIBLE);
                    paymethod = "Google Pay";

                } else if (phonepe.isChecked()) {
                    pay.setHint("Enter PhonePe Number");
                    pay.setInputType(InputType.TYPE_CLASS_NUMBER);

                    paylay.setHelperText("PhonePe Number");
                    pay.setVisibility(View.VISIBLE);
                    btnsubmit.setVisibility(View.VISIBLE);
                    paymethod = "PhonePe";

                } else if (paypal.isChecked()) {
                    pay.setHint("Enter PayPal Email");
                    pay.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    paylay.setHelperText("PayPal Email");
                    pay.setVisibility(View.VISIBLE);
                    btnsubmit.setVisibility(View.VISIBLE);
                    paymethod = "PayPal";

                } else if (upi.isChecked()) {
                    pay.setHint("Enter UPI Id");
                    pay.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    paylay.setHelperText("UPI / VPA Address");
                    pay.setVisibility(View.VISIBLE);
                    btnsubmit.setVisibility(View.VISIBLE);
                    paymethod = "UPI";

                }
            }
        });

        paypalintl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (paypalintl.isChecked()) {
                    pay.setHint("Enter PayPal Email");
                    pay.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    paylay.setHelperText("PayPal");
                    pay.setVisibility(View.VISIBLE);
                    btnsubmit.setVisibility(View.VISIBLE);
                    paymethod = "PayPal";

                }
            }
        });


        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (btnsubmit.getText().toString().contains("Submit")) {


                    if(anyonechecked()) {
                        if(pay.length()>0) {
                            saveinfo();
                            progressBar.setVisibility(View.VISIBLE);
                            pay.setVisibility(View.INVISIBLE);
                            radioGroupone.setVisibility(View.INVISIBLE);
                            paypalintl.setVisibility(View.INVISIBLE);
                            btnsubmit.setVisibility(View.INVISIBLE);

                        }else {
                            if(paylay.getHelperText()!=null){
                                paylay.setError(paylay.getHelperText() + " can't be empty");
                            }
                            FancyToast.makeText(Profile.this, "Please enter "+paylay.getHelperText(), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                        }
                    }else{
                        paylay.setError("Select payment method");
                        FancyToast.makeText(Profile.this, "Please select Payment Method First!", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                    }
                } else {
                    saveprofile();



                }

                /*if (anyonechecked()) {
                    if (pay.length() == 0) {

                        if (btnsubmit.getText().toString().contains("Submit")) {
                            saveinfo();
                            Intent i= new Intent(Profile.this, MainActivity.class);
                            startActivity(i);
                        } else {
                            FancyToast.makeText(Profile.this, "You can add payment info later!", FancyToast.LENGTH_LONG, FancyToast.INFO, false).show();


                        }
        /*if(paylay.getHelperText()!=null){
            paylay.setError(paylay.getHelperText() + " can't be empty");
        }else{
            paylay.setError("Select payment method first");

        }

                    } else {
                        saveinfo();
                    }
                } else {
                    FancyToast.makeText(Profile.this, "Please select Payment Method First!", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                }*/


            }

        });

    }


    public void saveinfo() {
        payinfo = pay.getText().toString().trim();
        UserInfo userInfo = new UserInfo(username, useremail, paymethod, payinfo, usercountry);
        FirebaseUser user = auth.getCurrentUser();
        payinfo = pay.getText().toString();
        databaseReference.child(dbuser).child(user.getPhoneNumber()).setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


                if (task.isSuccessful()) {

                    editor.putBoolean("payment submitted", true).apply();
                    editor.commit();
                    FancyToast.makeText(Profile.this, "Details saved!", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false);

                    finish();
                    Intent i = new Intent(Profile.this, MainActivity.class);
                    startActivity(i);

                } else {
                    Toast.makeText(Profile.this, "A problem occurred. Retry later", Toast.LENGTH_SHORT).show();
                    Log.d("db", task.getException().getMessage());
                    progressBar.setVisibility(View.INVISIBLE);
                    pay.setVisibility(View.VISIBLE);
                    radioGroupone.setVisibility(View.VISIBLE);
                    paypalintl.setVisibility(View.VISIBLE);
                    btnsubmit.setVisibility(View.VISIBLE);
                }

            }
        });



    }
    public void saveprofile() {

        UserInfo userInfo = new UserInfo(username, useremail, paymethod, payinfo, usercountry);

        FirebaseUser user = auth.getCurrentUser();

        databaseReference.child(dbuser).child(user.getPhoneNumber()).setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


                if (task.isSuccessful()) {

                    editor.putBoolean("payment submitted", false).apply();
                    editor.commit();
                    FancyToast.makeText(Profile.this, "You can add payment info later!", FancyToast.LENGTH_LONG, FancyToast.INFO, false).show();

                    finish();
                    Intent i = new Intent(Profile.this, MainActivity.class);
                    startActivity(i);

                } else {
                    Toast.makeText(Profile.this, "A problem occurred. Retry later", Toast.LENGTH_SHORT).show();
                    Log.d("db", task.getException().getMessage());
                    progressBar.setVisibility(View.INVISIBLE);
                    pay.setVisibility(View.VISIBLE);
                    radioGroupone.setVisibility(View.VISIBLE);
                    paypalintl.setVisibility(View.VISIBLE);
                    btnsubmit.setVisibility(View.VISIBLE);
                }

            }
        });



    }
    private boolean anyonechecked() {
        return paytm.isChecked() || phonepe.isChecked() || paypalintl.isChecked() || paypal.isChecked() || gpay.isChecked() || upi.isChecked();
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("If you exit you will be needed to login again, continue?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //finish();u
                        auth.signOut();
                        finish();
                        Intent i=new Intent(Profile.this, Registration.class);
                        startActivity(i);
                        //super.onBackPressed();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    dialog.cancel();
                });
        AlertDialog alert = builder.create();
        alert.show();
    /*
    if (handleCancel()){
        super.onBackPressed();
    }
    */
    }



}