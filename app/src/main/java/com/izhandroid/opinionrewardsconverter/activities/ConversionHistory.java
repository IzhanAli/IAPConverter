/*
 * Copyright (c) 2019. Izhan Ali. Accessing/leaking/modifying source codes is prohibited. Legal action will be taken for if you do such things.
 */

package com.izhandroid.opinionrewardsconverter.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.izhandroid.opinionrewardsconverter.adapters.DatabaseDetails;
import com.izhandroid.opinionrewardsconverter.R;
import com.izhandroid.opinionrewardsconverter.adapters.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.izhandroid.opinionrewardsconverter.utils.Constants.dbuser;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.dbuserlist;
import static com.izhandroid.opinionrewardsconverter.utils.Constants.isOnline;

public class ConversionHistory extends AppCompatActivity {
    DatabaseReference databaseReference;

    ProgressBar progress;

    List<DatabaseDetails> list = new ArrayList<>();

    RecyclerView recyclerView;

    RecyclerView.Adapter adapter ;

    FirebaseAuth auth;
    FirebaseUser user;

    TextView convofftxt;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convertion);

        recyclerView =  findViewById(R.id.recyclerView);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        recyclerView.setHasFixedSize(true);

        convofftxt = findViewById(R.id.convoffline);
        progress = findViewById(R.id.convertionhist_prg);
        recyclerView.setLayoutManager(new LinearLayoutManager(ConversionHistory.this));


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(dbuser);
        DatabaseReference reference = databaseReference.child(user.getPhoneNumber());
        DatabaseReference  reference1 = reference.child(dbuserlist);


        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    DatabaseDetails studentDetails = dataSnapshot.getValue(DatabaseDetails.class);

                    list.add(studentDetails);
                    //
                }

                if(list.isEmpty()){
                    convofftxt.setVisibility(View.VISIBLE);
                    if(!isOnline()){
                        convofftxt.setVisibility(View.VISIBLE);
                        convofftxt.setText("No Internet Connection Available!");
                    }


                }

                adapter = new RecyclerViewAdapter(ConversionHistory.this, list);

                recyclerView.setAdapter(adapter);

                progress.setVisibility(View.GONE);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                convofftxt.setVisibility(View.VISIBLE);
                convofftxt.setText(databaseError.getMessage() +" .Please mail us if issue persists");
                progress.setVisibility(View.GONE);


            }
        });

    }
}
