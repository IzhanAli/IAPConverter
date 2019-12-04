/*
 * Copyright (c) 2019. Izhan Ali. Accessing/leaking/modifying source codes is prohibited. Legal action will be taken for if you do such things.
 */

package com.izhandroid.opinionrewardsconverter.activities;

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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.izhandroid.opinionrewardsconverter.R;
import com.shashank.sony.fancytoastlib.FancyToast;

import static com.izhandroid.opinionrewardsconverter.utils.Constants.MY_PREFS_NAME;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.dbupaydetails;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.dbupaymethod;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.dbuser;

public class EditPay extends AppCompatActivity {
    EditText pay;
    TextInputLayout paylay;
    ChipGroup radioGroupone;
    Chip paytm, paypal, upi, phonepe, gpay;
    RadioButton paypalintl;
    Button btnsubmit;

    SharedPreferences pref;
    FirebaseAuth auth;
    String  paymethod, payinfo, usercountry;
    FirebaseUser user;
    TextView textView;
    ProgressBar progressBar;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_profile);
        super.onCreate(savedInstanceState);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (auth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, Registration.class));
        }

        progressBar = findViewById(R.id.payprg);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        pref = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
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


        btnsubmit.setText("Submit");
        btnsubmit.setEnabled(true);

        pay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                paylay.setError(null);
                if(pay.length()>0){
                    if(anyonechecked()){
                        btnsubmit.setEnabled(true);

                    }else {
                        btnsubmit.setEnabled(false);

                    }
                }else {
                    btnsubmit.setEnabled(false);

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

                if(anyonechecked()){
                    if(pay.length()>0){
                        btnsubmit.setEnabled(true);

                    }else {
                        btnsubmit.setEnabled(false);
                    }
                }else{
                    btnsubmit.setEnabled(false);
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
                    paylay.setHelperText("PayPal Email");
                    pay.setVisibility(View.VISIBLE);
                    btnsubmit.setVisibility(View.VISIBLE);
                    paymethod = "PayPal";

                }
            }
        });

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (anyonechecked()) {
                    if (pay.length() > 0) {


                        saveinfo();
                        progressBar.setVisibility(View.VISIBLE);
                        pay.setVisibility(View.INVISIBLE);
                        radioGroupone.setVisibility(View.INVISIBLE);
                        paypalintl.setVisibility(View.INVISIBLE);
                        btnsubmit.setVisibility(View.INVISIBLE);



                    } else {
                        if(paylay.getHelperText()!=null){
                            paylay.setError(paylay.getHelperText() + " can't be empty");
                        }
                        FancyToast.makeText(EditPay.this, "Please enter "+paylay.getHelperText(), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();

                    }
                } else {
                    paylay.setError("Select payment method");
                    FancyToast.makeText(EditPay.this, "Please select Payment Method First!", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                }
            }
        });



    }
    public void saveinfo() {



        payinfo = pay.getText().toString();

        databaseReference.child(dbuser).child(user.getPhoneNumber()).child(dbupaymethod).setValue(paymethod).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //Do what you need to do
                    databaseReference.child(dbuser).child(user.getPhoneNumber()).child(dbupaydetails).setValue(payinfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            finish();
                            FancyToast.makeText(EditPay.this, "Details updated!", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false);

                        }
                    });
                } else {
                    Toast.makeText(EditPay.this, "A problem occurred. Retry later", Toast.LENGTH_SHORT).show();
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
}
