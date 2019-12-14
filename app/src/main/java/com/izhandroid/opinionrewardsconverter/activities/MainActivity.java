/*
 * Copyright (c) 2019. Izhan Ali. Accessing/leaking/modifying source codes is prohibited. Legal action will be taken for if you do such things.
 */

package com.izhandroid.opinionrewardsconverter.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.izhandroid.opinionrewardsconverter.BuildConfig;
import com.izhandroid.opinionrewardsconverter.R;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.MY_PREFS_NAME;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.dbuemail;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.dbuser;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.isOnline;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.showSnack;

public class MainActivity extends AppCompatActivity {
    AccountHeader headerResult;
    Drawer result;
    Toolbar toolbar;
    FirebaseAuth auth;
    Integer vcode = 1;
    String revokemsg =null;
    FirebaseDatabase database;
    FirebaseUser user;
    String usernames, forced;
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    DatabaseReference databaseReferencename;
CoordinatorLayout coordinatorLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (auth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, Registration.class));
        }

       AdView mAdView = findViewById(R.id.mainadView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //drawer
        pref = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = pref.edit();

        showupdate();
        ExtendedFloatingActionButton fab = findViewById(R.id.extendedFloatingActionButtonmain);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(i);
            }
        });
        coordinatorLayout = findViewById(R.id.mainparent);
        usernames = pref.getString("name", null);
//DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(dbuser);
//        DatabaseReference reference = databaseReference.child(user.getPhoneNumber());
//        databaseReferencename = reference.child(dbuname);
//
//
//
//
//        databaseReferencename.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//               /// usernames = dataSnapshot.getValue(String.class);
//
//                Log.i("db", dataSnapshot.getValue(String.class));
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });



        new DrawerBuilder().withActivity(this).build();
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.get().load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.get().cancelRequest(imageView);
            }
        });
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final IProfile profile = new ProfileDrawerItem().withName(usernames)

                .withIdentifier(100);

        // Create the AccountHeader

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
// Adding profiles to header view
                        profile,
                        new ProfileSettingDrawerItem()
                                .withName("Sign Out")
                                // .withIcon(new IconicsDrawable(this, ).actionBar().paddingDp(5).colorRes(R.color.material_drawer_primary_text))
                                .withIcon(getResources().getDrawable(R.drawable.ic_open_in_browser_grey_900_18dp))
                                .withIdentifier(1),
                        new ProfileSettingDrawerItem()
                                .withName("Update Email")
                                // .withIcon(new IconicsDrawable(this, ).actionBar().paddingDp(5).colorRes(R.color.material_drawer_primary_text))
                                .withIcon(getResources().getDrawable(R.drawable.ic_border_color_grey_900_18dp))
                                .withIdentifier(2)

                )


                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        //sample usage of the onProfileChanged listener
                        //if the clicked item has the identifier 1 add a new profile ;)
                        if (profile instanceof IDrawerItem && profile.getIdentifier() == 1) {

                            auth.signOut();
                            finish();
                            Intent intent = new Intent(MainActivity.this, Registration.class);
                            startActivity(intent);
                        } else if (profile instanceof IDrawerItem && profile.getIdentifier() == 2) {

                            showChangeemail();
                        }


                        //false if you have not consumed the event and it should close the drawer
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();


        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.home).withIcon(R.drawable.ic_home_grey_900_24dp).withIdentifier(1).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.convert).withIcon(R.drawable.ic_convert_24dp).withIdentifier(2).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.rec).withIcon(R.drawable.ic_unarchive_grey_900_24dp).withIdentifier(3).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.trhistory).withIcon(R.drawable.ic_history_grey_900_24dp).withIdentifier(4).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.req).withIcon(R.drawable.ic_mode_comment_grey_900_24dp).withIdentifier(5).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.extended_fab_label).withIcon(R.drawable.ic_help_grey_900_24dp).withIdentifier(6).withSelectable(false),
                        new SectionDrawerItem().withName("Support our work! "),
                        new PrimaryDrawerItem().withName(R.string.rate).withIcon(R.drawable.ic_star_grey_900_24dp).withIdentifier(7).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.share).withIcon(R.drawable.ic_share_grey_900_24dp).withIdentifier(8).withSelectable(false),
                        new SectionDrawerItem().withName("App"),
                        new PrimaryDrawerItem().withName("Check for Update").withIcon(R.drawable.ic_get_app_grey_900_24dp).withIdentifier(9).withSelectable(false),
                        new PrimaryDrawerItem().withName("Privacy Policy").withIcon(R.drawable.ic_info_grey_900_24dp).withIdentifier(10).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.contact).withIcon(R.drawable.ic_contact_900_24dp).withIdentifier(11).withSelectable(false),

                        new ExpandableDrawerItem().withName("Follow us on").withIcon(R.drawable.ic_favorite_grey_900_24dp).withIdentifier(12).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem().withName("Facebook").withLevel(2).withIdentifier(2000),
                                new SecondaryDrawerItem().withName("Instagram").withLevel(2).withIdentifier(2001)
                        ),
                        new PrimaryDrawerItem().withName("More Apps").withIcon(R.drawable.ic_apps_grey_900_24dp).withIdentifier(13).withSelectable(false)
                ) // add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        //check if the drawerItem is set.
                        //there are different reasons for the drawerItem to be null
                        //--> click on the header
                        //--> click on the footer
                        //those items don't contain a drawerItem

                        if (drawerItem != null) {
                            Intent intent = null;
                            if (drawerItem.getIdentifier() == 1) {
                                //Home
                                result.closeDrawer();
                            } else if (drawerItem.getIdentifier() == 2) {

                                //Convert
                                if(revokemsg==null) {
                                    Toast.makeText(MainActivity.this, "loading data from the server, please wait..", Toast.LENGTH_SHORT).show();
                                }else if(revokemsg.equals("*")){
                                    if (pref.getBoolean("payment submitted", true)) {
                                         intent = new Intent(MainActivity.this, ConversionActivity.class);
                                        startActivity(intent);
                                    } else {
                                         intent = new Intent(MainActivity.this, PaymentDetails.class);
                                        startActivity(intent);
                                    }

                                }else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setTitle("Temporarily Unavailable");
                                    builder.setMessage(revokemsg);
                                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });
                                    builder.create();
                                    builder.show();
                                }
                            } else if (drawerItem.getIdentifier() == 3) {

                                //recent
                                if(isOnline()) {
                                    intent = new Intent(MainActivity.this, CurrentStatus.class);
                                }else {

                                        showSnack(coordinatorLayout, "No Internet Connection!");

                                }
                                //intent = new Intent(MainActivity.this, MainActivity.class);
                            } else if (drawerItem.getIdentifier() == 4) {

                                //History

                                if(isOnline()) {
                                    intent = new Intent(MainActivity.this, ConversionHistory.class);
                                }else {
                                    showSnack(coordinatorLayout, "No Internet Connection!");

                                }
                            } else if (drawerItem.getIdentifier() == 5) {
                               intent = new Intent(MainActivity.this, RequestPay.class);

                                //intent = new Intent(MainActivity.this, AdvancedActivity.class);
                            } else if (drawerItem.getIdentifier() == 6) {

                                intent = new Intent(MainActivity.this, HelpActivity.class);
                            } else if (drawerItem.getIdentifier() == 7) {

                                new AlertDialog.Builder(MainActivity.this)

                                        .setMessage("To prevent fraudulent transactions, it takes about 15-20 days to credit the payment. Please be patient before giving negative ratings on Play Store as we have made lot of efforts for this app")
                                        .setPositiveButton("Rate Now", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName())));
                                        Toast.makeText(MainActivity.this, "Please give a 5-star rating :)", Toast.LENGTH_LONG).show();

                                    }
                                }).show();

                               //intent = new Intent(MainActivity.this, MainActivity.class);
                            } else if (drawerItem.getIdentifier() == 8) {
                              Intent  iintent = new Intent();
                                iintent.setAction(Intent.ACTION_SEND);
                                iintent.putExtra(Intent.EXTRA_TEXT, "Hey, checkout this app to redeem your Google Opinion Rewards or Google Play Credits right into Paytm, UPI or Paypal \n \n https://play.google.com/store/apps/details?id="+getPackageName()); //TODO app url here

                                iintent.setType("text/plain");
                                startActivity(iintent);
                                // intent = new Intent(MainActivity.this, MainActivity.class);
                            } else if (drawerItem.getIdentifier() == 9) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName())));
                            } else if (drawerItem.getIdentifier() == 10) {
                                //TODO privacy policy
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://bit.ly/reward-privacy-policy")));


                            } else if (drawerItem.getIdentifier() == 11) {
                                Intent email = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
                                        "playrewardsconverter@gmail.com", null));

                                email.putExtra(Intent.EXTRA_EMAIL, "playrewardsconverter@gmail.com");
                                email.putExtra(Intent.EXTRA_SUBJECT, "src: "+getPackageName());
                                email.putExtra(Intent.EXTRA_TEXT, "Hello Team, \n");
                                startActivity(email);

                            } else if (drawerItem.getIdentifier() == 12) {


                            } else if (drawerItem.getIdentifier() == 13) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=6073004400609392754")));

                            } else if (drawerItem.getIdentifier() == 2000) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/izhandroid")));

                            } else if (drawerItem.getIdentifier() == 2001) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/izhandroid")));

                            }
                            if (intent != null) {
                                MainActivity.this.startActivity(intent);
                            }
                        }

                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();


        //only set the active selection or active profile if we do not recreate the activity
        if (savedInstanceState == null) {
            // set the selection to the item with the identifier 11
            result.setSelection(1);

            //set the active profile
            headerResult.setActiveProfile(profile);
        }


        Button btn = findViewById(R.id.convert);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if(revokemsg==null) {
                        Toast.makeText(MainActivity.this, "loading data from the server, please wait..", Toast.LENGTH_SHORT).show();
                    }else if(revokemsg.equals("*")){
                        if (pref.getBoolean("payment submitted", true)) {
                        Intent intent = new Intent(MainActivity.this, ConversionActivity.class);
                        startActivity(intent);
                        } else {
                            Intent i = new Intent(MainActivity.this, PaymentDetails.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }

                    }else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Temporarily Unavailable");
                        builder.setMessage(revokemsg);
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        builder.create();
                        builder.show();
                    }


            }
        });
        MaterialCardView cardView = findViewById(R.id.recentbtn);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOnline()) {
                    Intent i = new Intent(MainActivity.this, CurrentStatus.class);
                    startActivity(i);
                }else {
                 showSnack(coordinatorLayout, "No Internet Connection!");
                }
            }
        });

        MaterialCardView cardViewse = findViewById(R.id.transhistobtn);
        cardViewse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isOnline()) {
                    Intent  intento = new Intent(MainActivity.this, ConversionHistory.class);

                    startActivity(intento);
                }else {
                    showSnack(coordinatorLayout, "No Internet Connection!");
                }
            }
        });

    }

    private void showChangeemail() {
        LayoutInflater inflater= LayoutInflater.from(MainActivity.this);
        final View v = inflater.inflate(R.layout.email_update, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(v);

        EditText emailbox = v.findViewById(R.id.emailupdatetxt);
        TextInputLayout emaillay = v.findViewById(R.id.emaillayoy);
        Button btn = v.findViewById(R.id.submitnewmail);
        final AlertDialog alertDialog =builder.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                    if(emailbox.getText().toString().trim().matches(emailPattern) && emailbox.length() >= 4){
                        emaillay.setError(null);

                        submitnewmail();

                    }else {
                        emaillay.setError("Invalid Email!");
                    }

            }





            private void submitnewmail() {
                Toast.makeText(MainActivity.this, "updating...", Toast.LENGTH_LONG).show();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(dbuser);
                DatabaseReference reference = databaseReference.child(user.getPhoneNumber());
                reference.child(dbuemail).setValue(emailbox.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FancyToast.makeText(MainActivity.this,"Changed Successfully!", Toast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                            alertDialog.dismiss();
                        }else {
                            FancyToast.makeText(MainActivity.this,"An error occurred. Please retry later!", Toast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                            alertDialog.dismiss();
                        }
                    }
                });


            }
        });

    }

    private void showupdate(){
        TextView ot;
        ot = findViewById(R.id.onlinetxt);

        
        database = FirebaseDatabase.getInstance();
        DatabaseReference first = database.getReference("update");
        first.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
               String vcodes = String.valueOf(dataSnapshot.child("versioncode").getValue(Long.class));
               String force = dataSnapshot.child("force").getValue(String.class);
               String note = dataSnapshot.child("Notice").getValue(String.class);
               String revoke = dataSnapshot.child("close").getValue(String.class);
               if(note.contains("*")){
                   ot.setVisibility(View.GONE);
               }else {
                   ot.setVisibility(View.VISIBLE);
                   ot.setText(note);
               }


               revokemsg=revoke;
               forced = force;

               checkupdate(vcode, Integer.valueOf(vcodes));

            }



            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });




    }
    private void checkupdate( int vcode, int versionCodes ) {
        try {


            PackageInfo info = MainActivity.this.getPackageManager().getPackageInfo(MainActivity.this.getPackageName(), PackageManager.GET_ACTIVITIES);
            vcode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }




        if (vcode < versionCodes) {
            if (forced.contains("yes")){

                forcedialog().show();

            }else{

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(true);
                builder.setTitle("New Update Available!");
                builder.setMessage("Please install the latest version from Play Store \nUpdate now for better app experience");
                builder.setIcon(android.R.drawable.ic_dialog_alert);


                builder.setPositiveButton("Update Now", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName())));

                    }
                });

                builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.show();



            }

        }
    }
    private AlertDialog.Builder forcedialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Update required!");
        builder.setMessage("New update available \nApp will not work until you install the latest version from Play Store");
        builder.setIcon(android.R.drawable.ic_dialog_alert);


        builder.setPositiveButton("Install Now", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName())));

            }
        });

        builder.setNegativeButton("Close App", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        return builder;

    }
}
