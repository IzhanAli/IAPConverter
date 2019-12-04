/*
 * Copyright (c) 2019. Izhan Ali. Accessing/leaking/modifying source codes is prohibited. Legal action will be taken for if you do such things.
 */

package com.izhandroid.opinionrewardsconverter.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.izhandroid.opinionrewardsconverter.R;
import com.squareup.picasso.Picasso;

import static com.izhandroid.opinionrewardsconverter.utils.Constants.dbamt;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.dbcrnt;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.dbdatec;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.dbmainsc;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.dbprimary;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.dbsecondary;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.dbtrid;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.dbupaydetails;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.dbupaymethod;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.dbuser;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.isOnline;


public class CurrentStatus extends AppCompatActivity {
    private final String TAG = NativeAd.class.getSimpleName();

    String mstatus, amtpaid, date, trid, crnttitle, crntdesc;
    private TextView mainStatustv, amountPaidtv, datetv, trIdtv, primarytv, secondarytv, paynametv, paydetailtv,txterr;
    private ImageView copyIv, payiconiv, imgerr;
    private ProgressBar prg;
    FirebaseAuth auth;
    FirebaseUser user;
    MaterialButton btnreq;
    ClipboardManager clipboardManager;
    ClipData clipData;
    private RelativeLayout relativeLayout;
    private NativeAdLayout nativeAdLayout;
    private LinearLayout offlineview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_status);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        relativeLayout=findViewById(R.id.status_lay1);
        mainStatustv = findViewById(R.id.crnt_mainstauts);
        amountPaidtv = findViewById(R.id.amt_crnt);
        datetv = findViewById(R.id.date_crt);
        trIdtv = findViewById(R.id.trno_crt);
        copyIv = findViewById(R.id.copy_tr_img);
        primarytv = findViewById(R.id.crnt_staustitle);
        secondarytv = findViewById(R.id.crnt_stausdesc);
        paynametv = findViewById(R.id.crntpaymentacname);
        paydetailtv = findViewById(R.id.crntpaymentdetails);
        payiconiv = findViewById(R.id.paylogocrnt);
        offlineview = findViewById(R.id.noreqslay);
        imgerr = findViewById(R.id.imageViewerrorcrnt);
        txterr = findViewById(R.id.txtviewcrnt);
        prg = findViewById(R.id.progrcrnt);
        btnreq = findViewById(R.id.btnrequestcrnt);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (auth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, Registration.class));
        }
        View view = getWindow().getDecorView().getRootView();
        ExtendedFloatingActionButton fab = findViewById(R.id.extendedFloatingActionButtonstatus);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(CurrentStatus.this, HelpActivity.class);
                startActivity(i);
            }
        });

        checkexist();

        if(!isOnline()){
            Toast.makeText(this, "Turn on the Mobile Data/WiFi to get fresh data", Toast.LENGTH_SHORT).show();
        }


        AudienceNetworkAds.initialize(this);
       NativeAdLayout nativeAdLayout = findViewById(R.id.native_ad_crntstatus);
        NativeAdFb.loadAdNative(this, "895176207533823_895194007532043",nativeAdLayout);

       getSupportActionBar().setDisplayHomeAsUpEnabled(true);




       clipboardManager=(ClipboardManager) getSystemService(CLIPBOARD_SERVICE);






    }

    private void checkexist() {
        relativeLayout.setVisibility(View.INVISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(dbuser);
        DatabaseReference reference = databaseReference.child(user.getPhoneNumber());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(dbcrnt)){
                    retriveData();
                }else{
                    prg.setVisibility(View.INVISIBLE);
                    relativeLayout.setVisibility(View.GONE);
                    offlineview.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CurrentStatus.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                prg.setVisibility(View.INVISIBLE);
                relativeLayout.setVisibility(View.GONE);
                offlineview.setVisibility(View.VISIBLE);
                txterr.setText("An error occurred. Please check network or try again later");
                imgerr.setImageDrawable(getResources().getDrawable(R.drawable.ic_warning_black_24dp));
                Log.e(TAG,databaseError.getDetails());
            }
        });
    }

    private void retriveData() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(dbuser);
        DatabaseReference reference = databaseReference.child(user.getPhoneNumber());
        reference.child(dbcrnt).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 mstatus = dataSnapshot.child(dbmainsc).getValue(String.class);
                 amtpaid = dataSnapshot.child(dbamt).getValue(String.class);
                 date = dataSnapshot.child(dbdatec).getValue(String.class);
                 trid = dataSnapshot.child(dbtrid).getValue(String.class);
                crnttitle = dataSnapshot.child(dbprimary).getValue(String.class);

                crntdesc = dataSnapshot.child(dbsecondary).getValue(String.class);
                Log.d(TAG, "ok");
              setTvs();
            }

            private void setTvs() {

                if (mstatus.equals("a")) {
                    mainStatustv.setText("Amount to be credited");
                } else if (mstatus.equals("b")) {
                    mainStatustv.setText("Payment will not be made");
                    mainStatustv.setTextColor(getResources().getColor(R.color.md_red_400));
                } else {
                    mainStatustv.setText(mstatus);
                    mainStatustv.setTextColor(getResources().getColor(R.color.md_blue_800));
                }

                switch (crnttitle) {
                    case "a":
                        primarytv.setText("Awaiting Confirmation of your purchase");
                        break;
                    case "b":
                        primarytv.setText("Payment will be made soon");
                        break;
                    case "c":
                        primarytv.setText("Your payment is held");
                        break;
                    case "done":
                        primarytv.setText("Your payment has been sent");
                        primarytv.setTextColor(getResources().getColor(R.color.md_green_600));
                        primarytv.setCompoundDrawables(getResources().getDrawable(R.drawable.ic_done_black_24dp),null,null,null);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            primarytv.getCompoundDrawables()[0].setTint(getResources().getColor(R.color.md_green_600));
                        }

                        break;
                    case "x":
                        primarytv.setText("Your purchase could not be verified");
                        primarytv.setTextColor(getResources().getColor(R.color.md_red_600));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            primarytv.getCompoundDrawables()[0].setTint(getResources().getColor(R.color.md_red_600));
                        }
                        break;
                    case "z":
                        primarytv.setText("Invalid Purchase");
                        primarytv.setTextColor(getResources().getColor(R.color.md_red_600));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            primarytv.getCompoundDrawables()[0].setTint(getResources().getColor(R.color.md_red_600));
                        }
                        break;
                    default:
                        primarytv.setText(crnttitle);
                        break;
                }

                switch (crntdesc){
                    case "a":
                        secondarytv.setText("Please wait while we are verifying your purchase");
                        break;
                    case "b":
                        secondarytv.setText("Your purchase has been verified. Payment will be initiated within 7-10 business days");
                        break;
                    case "c":
                        secondarytv.setText("There is some delay for your payment. It will be initiated soon");
                        break;
                    case "done":
                        secondarytv.setText("Please check your mail for more details");

                        break;
                    case "x":
                        secondarytv.setText("Please verify your purchase from below. More details in the email");
                        break;
                    case "z":
                        secondarytv.setText("You have not made any purchase. If amount is deducted from your account, please contact Google Play Support");

                        break;
                    default:
                        secondarytv.setText(crntdesc);
                        break;
                }

                amountPaidtv.setText(amtpaid);
                datetv.setText(date);
                trIdtv.setText(trid);


                prg.setVisibility(View.INVISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
                offlineview.setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CurrentStatus.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                prg.setVisibility(View.INVISIBLE);
                relativeLayout.setVisibility(View.GONE);
                offlineview.setVisibility(View.VISIBLE);
                txterr.setText("An error occurred. Please check network or try again later");
                imgerr.setImageDrawable(getResources().getDrawable(R.drawable.ic_warning_black_24dp));
                Log.e(TAG,databaseError.getDetails());
            }
        });
copyIv.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        clipData = ClipData.newPlainText("text", trid);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(CurrentStatus.this, "Copied Successfully!", Toast.LENGTH_SHORT).show();
    }
});
copyIv.setOnLongClickListener(new View.OnLongClickListener() {
    @Override
    public boolean onLongClick(View view) {
        Toast.makeText(CurrentStatus.this, "Copy Transaction Id to clipboard", Toast.LENGTH_SHORT).show();
        return false;
    }
});


       reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               String payname = dataSnapshot.child(dbupaymethod).getValue(String.class);
               String paydet = dataSnapshot.child(dbupaydetails).getValue(String.class);

               paynametv.setText(payname+" ACCOUNT");
               paydetailtv.setText(paydet);
               if (payname != null) {
                   if (payname.contains("Paytm")) {
                       Picasso.get().load("https://imgur.com/a/VezGLSg.png").into(payiconiv);
                   } else if (payname.contains("PayPal")) {
                       Picasso.get().load("https://imgur.com/a/zIQR50P.png").into(payiconiv);
                   } else if (payname.contains("Google Pay")) {
                       Picasso.get().load("https://imgur.com/a/4tudtvH.png").into(payiconiv);
                   } else if (payname.contains("PhonePe")) {
                       Picasso.get().load("https://imgur.com/a/aMfVZ9V.png").into(payiconiv);
                   } else if (payname.contains("UPI")) {
                       Picasso.get().load(R.drawable.upiic).into(payiconiv);
                   }
               }else{
                   Picasso.get().load(R.drawable.ic_error_grey_500_48dp).into(payiconiv);

               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {
               Toast.makeText(CurrentStatus.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
               prg.setVisibility(View.INVISIBLE);
               relativeLayout.setVisibility(View.GONE);
               offlineview.setVisibility(View.VISIBLE);
               txterr.setText("An error occurred. Please check network or try again later");
               imgerr.setImageDrawable(getResources().getDrawable(R.drawable.ic_warning_black_24dp));
               Log.e(TAG,databaseError.getDetails());
           }
       });

    }


}
   /* MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


         AdLoader adLoader = new AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        // Show the ad.



                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // Handle the failure by logging, altering the UI, and so on.
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();



        adLoader.loadAds(new AdRequest.Builder().build(), 2);
        if (adLoader.isLoading()) {
            // The AdLoader is still loading ads.
            // Expect more adLoaded or onAdFailedToLoad callbacks.
        } else {
            // The AdLoader has finished loading ads.
        }*/




