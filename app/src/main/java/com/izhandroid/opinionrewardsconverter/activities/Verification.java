/*
 * Copyright (c) 2019. Izhan Ali. Accessing/leaking/modifying source codes is prohibited. Legal action will be taken for if you do such things.
 */

package com.izhandroid.opinionrewardsconverter.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.izhandroid.opinionrewardsconverter.R;
import com.izhandroid.opinionrewardsconverter.utils.Constants;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.concurrent.TimeUnit;

import static com.izhandroid.opinionrewardsconverter.utils.Constants.MY_PREFS_NAME;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.dbupaymethod;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.dbuser;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.isOnline;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.showSnack;

public class Verification extends AppCompatActivity {
    private static final int COUNTDOWN_STEP = 100;
    EditText otp;
    Button login, resend;
    String no;
    String countrystring;
    ProgressBar progressBar;
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    TextView textView, txt;
    int TIME = 60500;
    CountDownTimer countDownTimer;
    private FirebaseAuth mAuth;
    private String mVerificationId;

    String username, useremail, paymethod, payinfo;
    FirebaseUser user;
RelativeLayout relativeLayout;
     DatabaseReference databaseReference;
    TextInputLayout textInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        otp = (EditText) findViewById(R.id.otpbox);
        mAuth = FirebaseAuth.getInstance();
        no = getIntent().getStringExtra("mobile");
        countrystring = getIntent().getStringExtra("country");
        username = getIntent().getStringExtra("name");
        useremail = getIntent().getStringExtra("email");
        login = (Button) findViewById(R.id.submit);
        resend = findViewById(R.id.resend);
textInputLayout = findViewById(R.id.txtinputverify);
txt = findViewById(R.id.userno);
txt.setText("OTP was sent on "+no);
        paymethod = "not";
        payinfo = "not";
relativeLayout = findViewById(R.id.verificationparent);

        pref = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = pref.edit();
        progressBar = findViewById(R.id.prg);

        resend.setEnabled(false);
textView=findViewById(R.id.info);
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isOnline()) {
                    startTimer(1);
                    sendVerificationCode(no);
                    FancyToast.makeText(Verification.this, "OTP Sent Successfully!", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                    resend.setEnabled(false);
                }else {
                    showSnack(relativeLayout, "Unable to connect! Check internet connection");
                }

            }
        });

        sendVerificationCode(no);
        startTimer(1);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                login.requestFocus();
                String code = otp.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    otp.setError("Enter valid code");
                    //otp.requestFocus();
                    return;
                }

                //verifying the code entered manually
                if(isOnline()) {
                    verifyVerificationCode(code);
                }else {
                    showSnack(relativeLayout, "Unable to connect! Check internet connection");
                }
            }
        });
    }

    private void writePref(String id, String value) {
        editor.putString(id, value);
        editor.apply();

    }

    private void startTimer(final int min) {
        countDownTimer = new CountDownTimer(60 * min * 1000, 500) {
            // 500 means, onTick function will be called at every 500 milliseconds

            @Override
            public void onTick(long leftTimeInMilliseconds) {
                long seconds = leftTimeInMilliseconds / 1000;

                // format the textview to show the easily readable format
                resend.setText("Resend OTP (" + String.format("%02d", seconds % 60) + "s)");

            }

            @Override
            public void onFinish() {
                resend.setEnabled(true);
                //if(resend.getText().equals("Resend OTP (00s)")){
                resend.setText("Resend OTP");
                resend.setEnabled(true);


            }
        }.start();
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                otp.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            FancyToast.makeText(Verification.this, e.getMessage(), FancyToast.LENGTH_LONG, FancyToast.CONFUSING, false).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };
    private void sendVerificationCode(String no) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                no,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);

    }

    private void verifyVerificationCode(String code) {
        //creating the credential

progressBar.setVisibility(View.VISIBLE);
textView.setVisibility(View.INVISIBLE);
textInputLayout.setVisibility(View.INVISIBLE);
resend.setVisibility(View.INVISIBLE);
textView.setVisibility(View.INVISIBLE);
login.setVisibility(View.INVISIBLE);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void createProfile(){
        setResult(1);
finish();
        Intent intent = new Intent(Verification.this, Profile.class);

        intent.putExtra("country",countrystring);
        intent.putExtra("name",username);
        intent.putExtra("email", useremail);

        startActivity(intent);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(Verification.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            writePref("country", countrystring);
                            writePref("name", username);
                           //ProgressDialog myprg = Constants.DialogUtils.showprgdialog(Verification.this);
                                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                   DatabaseReference usr = rootRef.child(dbuser);
                                    usr.child(no);
                                    usr.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange( DataSnapshot snapshot) {
                                            if (snapshot.getValue()==null) {
                                                createProfile();

                                                // run some code
                                            }else {
                                                DatabaseReference num = usr.child(no).child("list");
                                                num.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot snapshot) {
                                                        if (snapshot.getValue()==null) {
                                                            createProfile();

                                                        }else{
                                                            setResult(1);
                                                            finish();
                                                            Intent intent = new Intent(Verification.this, MainActivity.class);

                                                            startActivity(intent);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(Verification.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();

                                        }
                                    });





                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            textInputLayout.setVisibility(View.VISIBLE);
                            resend.setVisibility(View.VISIBLE);
                            textView.setVisibility(View.VISIBLE);
                            login.setVisibility(View.VISIBLE);
                            //verification unsuccessful.. display an error message


                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                FancyToast.makeText(Verification.this, "Incorrect OTP entered", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();

                            } else  {
                                FancyToast.makeText(Verification.this, "Unable to verify please retry later", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();

                            }


                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        setResult(1);
        super.onDestroy();
    }
}
