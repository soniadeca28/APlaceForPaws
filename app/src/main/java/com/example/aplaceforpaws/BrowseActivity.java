package com.example.aplaceforpaws;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BrowseActivity extends AppCompatActivity {

    FirebaseAuth auth;
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private List<Upload> mUploads;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    private ImageView imageViewPet;
    Spinner petType;
    Member member;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar) ;
        toolbar.setTitleTextColor(Color.parseColor("#f9a895"));
        setSupportActionBar(toolbar);

        petType = findViewById(R.id.spinner_browse);



        Button back = findViewById(R.id.browseBackButton);
        back.setOnClickListener(v -> backToIntermediate());



        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class)); // sa te duca la pagina principala fiindca nu esti logat
            finish();
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        imageViewPet = findViewById(R.id.imageViewPet);

        mRecyclerView = findViewById(R.id.recycler_view_browse);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        mUploads = new ArrayList<>();
        mAdapter = new ImageAdapter(BrowseActivity.this,mUploads);
        mRecyclerView.setAdapter(mAdapter);

        EventChangeListener();

        setupSort();

    }

    private void EventChangeListener() {

        db.collection("uploads")
                .addSnapshotListener((value, error) -> {

                    if(error != null){
                        if(progressDialog.isShowing())
                            progressDialog.dismiss();
                        Log.e("Firestore error",error.getMessage());
                        return;
                    }
                    assert value != null;
                    for(DocumentChange dc : value.getDocumentChanges()){

                        if(dc.getType() == DocumentChange.Type.ADDED){
                            mUploads.add(dc.getDocument().toObject(Upload.class));
                        }
                        mAdapter.notifyDataSetChanged();
                        if(progressDialog.isShowing())
                            progressDialog.dismiss();
                    }

                });
    }




    private void backToIntermediate() {
        Intent intent = new Intent(this, IntermediateActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupSort(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        petType.setAdapter(adapter);
        petType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if(position == 1){
                    sortByName();
                }
                    else if(position == 2){
                        sortByAge();
                }

                    mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        member = new Member();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sortByName(){
        Collections.sort(mUploads, Comparator.comparing(Upload::getPetName));
    }

    private void sortByType(){

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sortByAge(){
        Collections.sort(mUploads, Comparator.comparing(Upload::getPetAge));
     /*   Collections.sort(mUploads, (p1,p2)->{
            if(p1.getPetAge() > p2.getPetAge()){
                return 1;
            }else if(p1.getPetAge() < p2.getPetAge()){
                return -1;
            }
            else{
                return 0;
            }
                });*/
    }


}