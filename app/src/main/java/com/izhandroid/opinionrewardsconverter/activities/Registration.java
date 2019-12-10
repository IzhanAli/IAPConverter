/*
 * Copyright (c) 2019. Izhan Ali. Accessing/leaking/modifying source codes is prohibited. Legal action will be taken for if you do such things.
 */

package com.izhandroid.opinionrewardsconverter.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.izhandroid.opinionrewardsconverter.R;
import com.izhandroid.opinionrewardsconverter.utils.Constants;
import com.shashank.sony.fancytoastlib.FancyToast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import static com.izhandroid.opinionrewardsconverter.utils.Constants.dbupaymethod;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.isOnline;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.showSnack;

public class Registration extends AppCompatActivity {
    CountryCodePicker ccp;
    TextInputLayout namelay, emaillay, otplay;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog myprogressDialog;

    EditText editTextCarrierNumber,emailbox, namebox;
    Button button;
    String no, name, email, country;

    FirebaseUser user;
    RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_registration);

        user = FirebaseAuth.getInstance().getCurrentUser();


        relativeLayout=findViewById(R.id.registrartionparent);
        emailbox = findViewById(R.id.email_edittxt);
        namebox = findViewById(R.id.username_edittxt);

        emaillay = findViewById(R.id.emaillay);
        namelay = findViewById(R.id.namelay);

        button = findViewById(R.id.btn);
        ccp = findViewById(R.id.ccp);
        editTextCarrierNumber =  findViewById(R.id.edittext_number);

        namebox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(namebox.length() >0){
                    namelay.setError(null);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(namebox.length() ==0){
                    namelay.setError("Name can't be empty");
                }
            }
        });

        emailbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(emailbox.length() >0){
                    emaillay.setError(null);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(emailbox.length() ==0){
                    emaillay.setError("Email is empty");
                }
            }
        });




        ccp.registerCarrierNumberEditText(editTextCarrierNumber);


        //country

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                no = ccp.getFullNumberWithPlus();
                country = ccp.getSelectedCountryEnglishName();
                name = namebox.getText().toString().trim();
                email = emailbox.getText().toString().trim();



                /*if(!ccp.isValidFullNumber() && no.isEmpty()){
                    isok=false;
                    otplay.setError("Enter valid number");
                    FancyToast.makeText(Registration.this,"Invalid Phone Number",FancyToast.LENGTH_LONG, FancyToast.ERROR,false).show();

                } else


                if(!email.contains("@.") && emailbox.length() <5){

                    emaillay.setError("Invalid Email");
                    isok=false;
                }
                if(isok){
                    allok();
                }*/

                if(namebox.length() == 0 && namebox.getText().toString().isEmpty() ){

                    namelay.setError("Name can't be empty");


                }else{
                    namelay.setError(null);

                }
                if(ccp.isValidFullNumber() && ccp != null){

                    if(isValidEmail() && !namebox.getText().toString().isEmpty() ){
                        allok();

                    }


                } else {
                    FancyToast.makeText(Registration.this,"Invalid Phone Number",FancyToast.LENGTH_LONG, FancyToast.ERROR,false).show();

                }


            }
        });


    }
private void allok (){
    myprogressDialog = Constants.DialogUtils.showprgdialog(Registration.this);
        if(isOnline()){
            myprogressDialog.dismiss();

            Intent intent = new Intent(Registration.this, Verification.class);
            intent.putExtra("mobile",no);
            intent.putExtra("country",country);
            intent.putExtra("name",name);
            intent.putExtra("email", email);
            startActivityForResult(intent,2);


        }else {
            showSnack(relativeLayout, "No Internet Connection Available!");
            myprogressDialog.dismiss();
        }

}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            finish();
        }


    }

    public boolean isValidEmail(){
    if(emailbox.getText().toString().trim().matches(emailPattern) && emailbox.length() >= 4){
        emaillay.setError(null);

        return true;
    }else {
        emaillay.setError("Invalid Email!");
        return false;
    }

}

}
