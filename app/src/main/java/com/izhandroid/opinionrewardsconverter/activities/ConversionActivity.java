/*
 * Copyright (c) 2019. Izhan Ali. Accessing/leaking/modifying source codes is prohibited. Legal action will be taken for if you do such things.
 */

package com.izhandroid.opinionrewardsconverter.activities;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.izhandroid.opinionrewardsconverter.adapters.DatabaseDetails;
import com.izhandroid.opinionrewardsconverter.R;
import com.izhandroid.opinionrewardsconverter.utils.Constants;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.izhandroid.opinionrewardsconverter.utils.Constants.MY_PREFS_NAME;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.PREFPURCH;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.Urlpaymentdets;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.Urlrequestdets;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.datePREFPURCH;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.dbcrnt;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.dbsrno;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.dbuemail;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.dbupaydetails;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.dbupaymethod;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.dbuser;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.dbuserlist;

import static com.izhandroid.opinionrewardsconverter.utils.Constants.isOnline;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.itemPREFPURCH;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.showSnack;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.tridPREFPURCH;


public class ConversionActivity extends AppCompatActivity implements PurchasesUpdatedListener {
    private static final String TAG = "InAppBilling";
    Toolbar toolbar;
    Activity activity;
    ChipGroup chipGroup;
    String devpayload, orderid;

    ProgressDialog progressDialog;
    ProgressBar progressBar;
    String datetime, trno, emailuser, status;

    RequestQueue queue;
    TextView dc;
    long srno;
    SharedPreferences pref, purchsp;
    DatabaseReference databaseReference;
    TextView pay, paydetails, payprice;
    String purchasetoken, payname, paydet;
    private String r30 = "rs.30";
    private String r50 = "rs.50";
    private String r100 = "rs.100";
    private String r150 = "rs.150";
    private String r200 = "rs.200";
    private String r300 = "rs.300";
    private String r400 = "rs.400";
    private String r500 = "rs.500";
    private String r700 = "rs.700";
    private String r1000 = "rs.1000";
    private String r5000 = "rs.5000";
    Button mBuyButton, placerequest;
    private Chip chip30;
    private Chip chip50;
    private Chip chip100;
    private Chip chip150;
    private Chip chip200;
    private Chip chip300;
    private Chip chip400;
    private Chip chip500;
    private Chip chip700;
    private Chip chip1000;
    private Chip chip5000;
    private SharedPreferences mSharedPreferences;
    private BillingClient mBillingClient;
    Dialog fullscreenDialog;
    String purchaseitem;
    long newvalue;
    ImageView paylogo;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database2;
    SharedPreferences.Editor editor;
    DatabaseReference referencesr;

    Boolean purchasestate = false;
    CoordinatorLayout coordinatorLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conv_parent);
        toolbar = findViewById(R.id.toolbarconv);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity = this;

        coordinatorLayout = findViewById(R.id.convoparentlay);
        purchsp = getSharedPreferences(PREFPURCH, MODE_PRIVATE);
        orderid = "n";

        fetchSrno();

        dc = findViewById(R.id.dsc);
        chip30 = findViewById(R.id.c30);
        chip50 = findViewById(R.id.c50);
        chip100 = findViewById(R.id.c100);
        chip150 = findViewById(R.id.c150);
        chip200 = findViewById(R.id.c200);
        chip300 = findViewById(R.id.c300);
        chip400 = findViewById(R.id.c400);
        chip500 = findViewById(R.id.c500);
        chip700 = findViewById(R.id.c700);
        chip1000 = findViewById(R.id.c1000);
        chip5000 = findViewById(R.id.c5000);
        progressDialog = new ProgressDialog(ConversionActivity.this);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressDialog.create();
        }
        queue = Volley.newRequestQueue(ConversionActivity.this);

        /**Dialog**/
        fullscreenDialog = new Dialog(this, R.style.DialogFullscreen);
        fullscreenDialog.setCancelable(false);
        fullscreenDialog.setContentView(R.layout.confirm_pay);
        pay = fullscreenDialog.findViewById(R.id.nameofpay);
        paydetails = fullscreenDialog.findViewById(R.id.idofpay);
        paylogo = fullscreenDialog.findViewById(R.id.paymentlogo);

        progressBar = fullscreenDialog.findViewById(R.id.confirm_pay_prg);

        payprice = fullscreenDialog.findViewById(R.id.pricetextView);

        pref = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String usercountry = pref.getString("country", null);

        if (!usercountry.equals("India")) {
            dc.setText("You get ~60% of the amount you pay");
        }

        purchaseitem = "x";

        /**Initialising Auth and User**/
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (auth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, Registration.class));
        }
        editor = pref.edit();

        /*if (pref.getBoolean("request pending", true)) {
            showConfirmDialog();
            FancyToast.makeText(activity, "You have a payout request pending!", Toast.LENGTH_LONG, FancyToast.WARNING, false).show();
        }*/

        if(pref.getInt("req submitted", 0)==1){
            showConfirmDialog();
            FancyToast.makeText(activity, "You have a payout request pending!", Toast.LENGTH_LONG, FancyToast.WARNING, false).show();
        }

        ExtendedFloatingActionButton fab = findViewById(R.id.extendedFloatingActionButtonconv);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(ConversionActivity.this, HelpActivity.class);
                startActivity(i);
            }
        });
        //Edit Btn

        Button btn = fullscreenDialog.findViewById(R.id.editdetaisl);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ConversionActivity.this, EditPay.class);
                startActivity(i);
            }
        });
        placerequest = fullscreenDialog.findViewById(R.id.placeregbtn);
        placerequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isOnline()) {
                    postreqsDetail(payname, paydet, emailuser);
                    crntdbwrite();

                }else{
                    FancyToast.makeText(activity, "No Internet Available!", FancyToast.LENGTH_SHORT, FancyToast.ERROR,false).show();

                }

            }
        });
        Toolbar toolbar_full_screen_dialog = fullscreenDialog.findViewById(R.id.toolbar_full_screen_dialog);
        toolbar_full_screen_dialog.setNavigationOnClickListener(v -> {
            editor.putInt("req submitted", 1).apply();
            editor.commit();
            fullscreenDialog.dismiss();
            finish();

        });


        chipGroup = findViewById(R.id.chips);
        mBuyButton = findViewById(R.id.pay);
        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {

            }
        });

        //Fetching Data from Fb db
        retriveData();

        final List<String> skuList = new ArrayList<>();

        skuList.add(r30);
        skuList.add(r50);
        skuList.add(r100);
        skuList.add(r150);
        skuList.add(r200);
        skuList.add(r300);
        skuList.add(r400);
        skuList.add(r500);
        skuList.add(r700);
        skuList.add(r1000);
        skuList.add(r5000);
        mBillingClient = BillingClient.newBuilder(ConversionActivity.this).setListener(this).enablePendingPurchases().build();
        mBillingClient.startConnection(new BillingClientStateListener() {

            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                    mBillingClient.querySkuDetailsAsync(params.build(),
                            new SkuDetailsResponseListener() {
                                @Override
                                public void onSkuDetailsResponse(BillingResult billingResult, final List<SkuDetails> skuDetailsList) {
                                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                        Log.d(TAG, "Response code is ok");

                                        if (skuDetailsList != null) {

                                            for (Object skuDetailsObject : skuDetailsList) {
                                                SkuDetails skuDetails = (SkuDetails) skuDetailsObject;
                                                String sku = skuDetails.getSku();
                                                String price = skuDetails.getPrice();
                                                if (r30.equals(sku)) {

                                                    chip30.setText(price);
                                                }
                                                if (r50.equals(sku)) {
                                                    chip50.setText(price);
                                                }
                                                if (r100.equals(sku)) {
                                                    chip100.setText(price);
                                                }
                                                if (r150.equals(sku)) {
                                                    chip150.setText(price);
                                                }
                                                if (r200.equals(sku)) {
                                                    chip200.setText(price);
                                                }
                                                if (r300.equals(sku)) {
                                                    chip300.setText(price);
                                                }
                                                if (r400.equals(sku)) {
                                                    chip400.setText(price);
                                                }
                                                if (r500.equals(sku)) {
                                                    chip500.setText(price);
                                                }
                                                if (r700.equals(sku)) {
                                                    chip700.setText(price);
                                                }
                                                if (r1000.equals(sku)) {
                                                    chip1000.setText(price);
                                                }
                                                if (r5000.equals(sku)) {
                                                    chip5000.setText(price);
                                                }


                                            }
                                            mBuyButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    if (anyonechecked()) {

                                                        ProgressDialog progressDialog = Constants.DialogUtils.showprgdialog(activity);

                                                        if(isOnline()) {

                                                            progressDialog.dismiss();
                                                            if (chip30.isChecked()) {
                                                                BillingFlowParams flowParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetailsList.get(4)).build();
                                                                mBillingClient.launchBillingFlow(ConversionActivity.this, flowParams);
                                                                purchaseitem = skuDetailsList.get(4).getPrice();

                                                            } else if (chip50.isChecked()) {
                                                                BillingFlowParams flowParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetailsList.get(7)).build();
                                                                mBillingClient.launchBillingFlow(ConversionActivity.this, flowParams);
                                                                purchaseitem = skuDetailsList.get(7).getPrice();


                                                            } else if (chip100.isChecked()) {
                                                                BillingFlowParams flowParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetailsList.get(0)).build();
                                                                mBillingClient.launchBillingFlow(ConversionActivity.this, flowParams);
                                                                purchaseitem = skuDetailsList.get(0).getPrice();


                                                            } else if (chip150.isChecked()) {
                                                                BillingFlowParams flowParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetailsList.get(2)).build();
                                                                mBillingClient.launchBillingFlow(ConversionActivity.this, flowParams);
                                                                purchaseitem = skuDetailsList.get(2).getPrice();


                                                            } else if (chip200.isChecked()) {
                                                                BillingFlowParams flowParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetailsList.get(3)).build();
                                                                mBillingClient.launchBillingFlow(ConversionActivity.this, flowParams);
                                                                purchaseitem = skuDetailsList.get(3).getPrice();

                                                            } else if (chip300.isChecked()) {
                                                                BillingFlowParams flowParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetailsList.get(5)).build();
                                                                mBillingClient.launchBillingFlow(ConversionActivity.this, flowParams);
                                                                purchaseitem = skuDetailsList.get(5).getPrice();
                                                            } else if (chip400.isChecked()) {
                                                                BillingFlowParams flowParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetailsList.get(6)).build();
                                                                mBillingClient.launchBillingFlow(ConversionActivity.this, flowParams);
                                                                purchaseitem = skuDetailsList.get(6).getPrice();
                                                            } else if (chip500.isChecked()) {
                                                                BillingFlowParams flowParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetailsList.get(8)).build();
                                                                mBillingClient.launchBillingFlow(ConversionActivity.this, flowParams);
                                                                purchaseitem = skuDetailsList.get(8).getPrice();
                                                            } else if (chip700.isChecked()) {
                                                                BillingFlowParams flowParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetailsList.get(10)).build();
                                                                mBillingClient.launchBillingFlow(ConversionActivity.this, flowParams);
                                                                purchaseitem = skuDetailsList.get(10).getPrice();
                                                            } else if (chip1000.isChecked()) {
                                                                BillingFlowParams flowParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetailsList.get(1)).build();
                                                                mBillingClient.launchBillingFlow(ConversionActivity.this, flowParams);
                                                                purchaseitem = skuDetailsList.get(1).getPrice();
                                                            } else if (chip5000.isChecked()) {
                                                                BillingFlowParams flowParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetailsList.get(9)).build();
                                                                mBillingClient.launchBillingFlow(ConversionActivity.this, flowParams);
                                                                purchaseitem = skuDetailsList.get(9).getPrice();
                                                            }
                                                        }else {
                                                            progressDialog.dismiss();
                                                            showSnack(coordinatorLayout, "Unable to connect!");
                                                        }
                                                    } else {

                                                        FancyToast.makeText(ConversionActivity.this, "Please select amount first", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();



                                                    }

                                                }
                                            });


                                        } else {
                                            Log.d(TAG, "Sku is null");


                                            Log.d(TAG, String.valueOf(billingResult.getResponseCode()));
                                            Log.d(TAG, billingResult.getDebugMessage());


                                        }


                                    } else {
                                        Log.d(TAG, "other issue");
                                    }


                                }
                            });
                    Log.d(TAG, "Connection Ok");
                } else {
                    Log.d(TAG, "Not OK");
                }

            }

            @Override
            public void onBillingServiceDisconnected() {
                //TODO implement your own retry policy
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.


                Log.d(TAG, "Connection disconnect");
            }
        });


    }


    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }


        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Toast.makeText(activity, "You have canceled the purchase. Please retry", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "User Canceled" + billingResult.getResponseCode());
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {

            Toast.makeText(activity, "Hmmm, this is unexpected", Toast.LENGTH_SHORT).show();
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE) {

            Toast.makeText(activity, "An error occurred. Retry Later", Toast.LENGTH_SHORT).show();
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ERROR) {

            FancyToast.makeText(activity, "An error occurred. If persists contact us!", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "Other code" + billingResult.getResponseCode());
            // Handle any other error codes.
            FancyToast.makeText(ConversionActivity.this, "An unexpected error occurred, Please check connection or retry later", FancyToast.LENGTH_SHORT, FancyToast.CONFUSING, false).show();
        }
    }

    private void handlePurchase(Purchase purchase) {
        purchasetoken = purchase.getPurchaseToken();
        orderid = purchase.getOrderId();
        String lala = purchase.getSku();
        int nana = purchase.getPurchaseState();
        long mama = purchase.getPurchaseTime();

        purchasestate = true;
        devpayload = purchase.getDeveloperPayload();
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            // Grant entitlement to the user.
            /*ConsumeResponseListener listener = new ConsumeResponseListener() {
                @Override
                public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {

                }
            };*/


            /**This method writes in list*/
            writelistdb();
            postPaymentDetail(orderid, nana, lala, purchaseitem, nana, devpayload);
            // Showing Toast message after successfully data submit.


            mBuyButton.setText("Continue");

            if (mBuyButton.getText() == "Continue") {
                mBuyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        showConfirmDialog();
                    }
                });
            }
            ConsumeResponseListener listener = new ConsumeResponseListener() {
                @Override
                public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {


                }
            };

            ConsumeParams consumeParams =
                    ConsumeParams.newBuilder()
                            .setPurchaseToken(purchasetoken)
                            .setDeveloperPayload(devpayload)
                            .build();

            mBillingClient.consumeAsync(consumeParams, listener);

            // Acknowledge the purchase if it hasn't already been acknowledged.
            /*if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchas.getPurchaseToken())
                                .build();
                mBillingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
            }*/

        } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
            // Here you can confirm to the user that they've started the pending
            // purchase, and to complete it, they should follow instructions that
            // are given to them. You can also choose to remind the user in the
            // future to complete the purchase if you detect that it is still
            // pending.


        }




    }

    private void writelistdb() {
        /**This method writes in list**/

        progressDialog.show();
        fetchSrno();

        Date ddmm = new Date();
        SimpleDateFormat format2 = new SimpleDateFormat("MM");
        String month = format2.format(ddmm);


        Date ddmmd = new Date();
        SimpleDateFormat format2d = new SimpleDateFormat("dd");
        String date = format2d.format(ddmmd);


        Date hm = new Date();
        SimpleDateFormat format3 = new SimpleDateFormat("mm");
        String min = format3.format(hm);

        Date ss = new Date();
        SimpleDateFormat format4 = new SimpleDateFormat("yyyy");
        String year = format4.format(ss);

        Date ssg = new Date();
        SimpleDateFormat fv = new SimpleDateFormat("kk");
        String hr2 = fv.format(ssg);


        //08/08/2019  08:20
        gentTransactionNumberntime();


        DatabaseDetails writingListDb = new DatabaseDetails();

        writingListDb.setDatetime(datetime);

        writingListDb.setStatus("Pending");

        writingListDb.setOrderid(orderid);
        writingListDb.setProductprice(purchaseitem);

        writingListDb.setTrno(trno);
        // Getting the ID from firebase database.


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(dbuser);
        DatabaseReference reference = databaseReference.child(user.getPhoneNumber());
        DatabaseReference reference1 = reference.child(dbuserlist);
        String timestamp = year + month + date + hr2 + min;
        reference1.child(timestamp).setValue(writingListDb).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    editor.putInt("req submitted", 1).apply();
                    editor.commit();
                    showConfirmDialog();
                } else {
                    Toast.makeText(activity, "An error occured", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });


    }

    private void gentTransactionNumberntime() {
        /**Generates transaction id and time and saves it locally**/

        trno = null;
        String[] chars = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "P", "Q", "R", "S", "T", "U", "V", "Z"};
        String rndm = chars[(int) (Math.random() * 10)];
        Date ddmm = new Date();
        SimpleDateFormat format2 = new SimpleDateFormat("MMyydd");
        String daymont = format2.format(ddmm);


        Date hm = new Date();
        SimpleDateFormat format3 = new SimpleDateFormat("mmhh");
        String hrmin = format3.format(hm);

        Date ss = new Date();
        SimpleDateFormat format4 = new SimpleDateFormat("ss");
        String sec = format4.format(ss);


        if (trno == null) {
            trno = rndm + daymont + hrmin + srno + sec;
            newvalue = srno + 1;
            referencesr.setValue(newvalue);
        }
        Date ddmmw = new Date();
        SimpleDateFormat format2w = new SimpleDateFormat("MM");
        String month = format2w.format(ddmmw);


        Date ddmmdw = new Date();
        SimpleDateFormat format2dw = new SimpleDateFormat("dd");
        String date = format2dw.format(ddmmdw);

        Date ddmmds = new Date();
        SimpleDateFormat format2ds = new SimpleDateFormat("hh");
        String hr = format2ds.format(ddmmds);

        Date hmw = new Date();
        SimpleDateFormat format3w = new SimpleDateFormat("mm");
        String min = format3w.format(hmw);

        Date ssw = new Date();
        SimpleDateFormat format4w = new SimpleDateFormat("yyyy");
        String year = format4w.format(ssw);


        datetime = month + "/" + date + "/" + year + "  " + hr + ":" + min;
        // Z080454111057
        savepurchase();
        /**This method saves date, item, trid locally**/


    }

    private void savepurchase() {
        SharedPreferences.Editor editor = purchsp.edit();
        editor.putString(tridPREFPURCH, trno);
        editor.putString(datePREFPURCH, datetime);
        editor.putString(itemPREFPURCH, purchaseitem);
        editor.apply();
        /**SharedPrefs Applied here for trid, date, item**/


    }

    private void crntdbwrite() {
        /**This method fetches local DATE, TRID, ITEM and saves in current db**/

        String sppi = purchsp.getString(itemPREFPURCH, "");
        String spdt = purchsp.getString(datePREFPURCH, "");
        String sptr = purchsp.getString(tridPREFPURCH, "");

        DatabaseDetails writecrnt = new DatabaseDetails();

        writecrnt.setAmtpaid(sppi);
        writecrnt.setDate(spdt);
        writecrnt.setMainstatus("a");
        writecrnt.setPrimarystatus("a");
        writecrnt.setSecondarystatus("a");
        writecrnt.setTrid(sptr);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(dbuser);
        DatabaseReference reference = databaseReference.child(user.getPhoneNumber());
        reference.child(dbcrnt).setValue(writecrnt).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    editor = pref.edit();
                    editor.putInt("req submitted", 0).apply();
                    editor.commit();

                    purchasestate = false;
                    finish();

                    FancyToast.makeText(ConversionActivity.this, "Request Placed Successfully! You can track payment from Recent Conversion", Toast.LENGTH_LONG, FancyToast.SUCCESS, false).show();


                } else {

                    purchasestate = true;
                    finish();
                    editor.putInt("req submitted", 1).apply();
                    editor.commit();
                    Toast.makeText(activity, "An error occured please retry", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void retriveData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(dbuser);
        DatabaseReference reference = databaseReference.child(user.getPhoneNumber());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                payname = dataSnapshot.child(dbupaymethod).getValue(String.class);
                paydet = dataSnapshot.child(dbupaydetails).getValue(String.class);

                emailuser = dataSnapshot.child(dbuemail).getValue(String.class);
                pay.setText(payname + " ACCOUNT");
                paydetails.setText("linked to: " + paydet);
                if (payname != null) {
                    if (payname.contains("Paytm")) {
                        Picasso.get().load("https://i.imgur.com/wJzCkeR.png").into(paylogo);
                    } else if (payname.contains("PayPal")) {
                        Picasso.get().load("https://i.imgur.com/UWdmZGM.png").into(paylogo);
                    } else if (payname.contains("Google Pay")) {
                        Picasso.get().load("https://i.imgur.com/uNIHear.png").into(paylogo);
                    } else if (payname.contains("PhonePe")) {
                        Picasso.get().load("https://i.imgur.com/LMLfd0x.png").into(paylogo);
                    } else if (payname.contains("UPI")) {
                        Picasso.get().load(R.drawable.upiic).into(paylogo);
                    }
                } else {
                    Picasso.get().load(R.drawable.ic_error_grey_500_48dp).into(paylogo);

                }

                if (dataSnapshot.exists()) {
                    progressBar.setVisibility(View.GONE);
                    pay.setVisibility(View.VISIBLE);
                    paylogo.setVisibility(View.VISIBLE);
                    paydetails.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    pay.setVisibility(View.VISIBLE);
                    paylogo.setVisibility(View.VISIBLE);
                    paydetails.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                FancyToast.makeText(ConversionActivity.this, "An error occured", Toast.LENGTH_LONG, FancyToast.ERROR, false);
                progressBar.setVisibility(View.GONE);
                pay.setVisibility(View.VISIBLE);
                paylogo.setVisibility(View.VISIBLE);
                paydetails.setVisibility(View.VISIBLE);
                pay.setText("Unable to fetch");


            }
        });


    }

    private void fetchSrno() {
        database2 = FirebaseDatabase.getInstance();
        referencesr = database2.getReference(dbsrno);
        referencesr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long value = dataSnapshot.getValue(Long.class);
                srno = value;
                Log.e("DB", String.valueOf(value));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


                Log.i("DB", databaseError.getMessage());

            }
        });
    }


    public void showConfirmDialog() {


        progressDialog.dismiss();


        fullscreenDialog.show();

        retriveData();
        payprice.setText(purchsp.getString(itemPREFPURCH, ""));
        progressBar.setVisibility(View.VISIBLE);
        pay.setVisibility(View.INVISIBLE);
        paylogo.setVisibility(View.INVISIBLE);
        paydetails.setVisibility(View.INVISIBLE);


    }


    private boolean anyonechecked() {
        return chip30.isChecked() || chip50.isChecked() || chip100.isChecked() || chip150.isChecked() || chip200.isChecked() || chip300.isChecked() || chip400.isChecked() || chip500.isChecked() || chip700.isChecked() || chip1000.isChecked() || chip5000.isChecked();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }


    @Override
    protected void onResume() {
        retriveData();
        super.onResume();

    }

    private void postPaymentDetail(String purchorderid, long purchdatetime, String sku, String purchtoken, int purchstate, String devpayload) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Urlpaymentdets,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG", "Response: " + response);
                        if (response.length() > 0) {

                            Toast.makeText(activity, "Success!", Toast.LENGTH_SHORT).show();
                        } else {


                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Err", error.toString());

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("entry.1138306451", purchsp.getString(tridPREFPURCH, ""));// Transaction no
                params.put("entry.1853040092", auth.getCurrentUser().getPhoneNumber());//Ph Number
                params.put("entry.1198637740", purchorderid);//Order id user
                params.put("entry.275697739", purchsp.getString(itemPREFPURCH, ""));//Item price
                params.put("entry.396462814", String.valueOf(purchdatetime));//Date time
                params.put("entry.1054640668", sku);//Sku
                params.put("entry.993194774", purchtoken);//token
                params.put("entry.2053442681", String.valueOf(purchstate));//state of purchase
                params.put("entry.79356656", devpayload);

                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    private void postreqsDetail(String payac, String payinf, String mail) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Urlrequestdets,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG", "Response: " + response);
                        if (response.length() > 0) {

                        } else {


                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Err", error.toString());

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("entry.1785983389", "No");// Paid
                params.put("entry.1590736111", auth.getCurrentUser().getPhoneNumber());//Ph Number
                params.put("entry.844309361", purchsp.getString(tridPREFPURCH, ""));//Transaction Id
                params.put("entry.719493359", purchsp.getString(itemPREFPURCH, ""));//Item Amt
                params.put("entry.1361176745", payac);//Pay Account
                params.put("entry.1957318022", payinf);//Pay Info
                params.put("entry.1725437410", mail);//Email


                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }
}
