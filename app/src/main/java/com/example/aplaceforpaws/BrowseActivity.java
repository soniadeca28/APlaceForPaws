package com.example.aplaceforpaws;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
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
    Spinner petFilter;
    Member member;
   // private List<Upload> mUploadsAux;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#f9a895"));
        setSupportActionBar(toolbar);

        petType = findViewById(R.id.spinner_browse);
        petFilter = findViewById(R.id.spinner_filter_by_type);

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

        if(mUploads.size() == 0 )
        {
            if(progressDialog.isShowing())
                progressDialog.dismiss();
        }

        setupSort();

        setupFilterByType();

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
                        petFilter.setSelection(0);
                        petFilter.setSelected(true);
                }
                    else mRecyclerView.setAdapter(mAdapter);

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
        mRecyclerView.setAdapter(mAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sortByAge(){
        Collections.sort(mUploads, Comparator.comparing(Upload::getPetAge));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setupFilterByType(){

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.pet_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        petFilter.setAdapter(adapter);
        petFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if(position == 1) showCats();

                else if(position == 2) showDogs();

                else if(position == 3) showFish();

                else if(position == 4) showParrots();

                else if(position == 5) showHedgehogs();

                else mRecyclerView.setAdapter(mAdapter);


            }



            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });
        member = new Member();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showCats() {
        ImageAdapter mAdapterAux;
        List<Upload> mUploadsAux;
        mUploadsAux = new ArrayList<>();
        mAdapterAux = new ImageAdapter(BrowseActivity.this,mUploadsAux);
          for(Upload u : mUploads )  {
                if (u.getPetType().contains("Cat")) {
                    mUploadsAux.add(u);
                }
            }
        mRecyclerView.setAdapter(mAdapterAux);
        mAdapterAux.notifyDataSetChanged();

    }

    private void showDogs() {
        ImageAdapter mAdapterAux;
        List<Upload> mUploadsAux;
        mUploadsAux = new ArrayList<>();
        mAdapterAux = new ImageAdapter(BrowseActivity.this,mUploadsAux);

            for(Upload u : mUploads )  {
                if (u.getPetType().contains("Dog")) {
                    mUploadsAux.add(u);
                }
            }

        mRecyclerView.setAdapter(mAdapterAux);
        mAdapterAux.notifyDataSetChanged();
    }

    private void showFish() {
        ImageAdapter mAdapterAux;
        List<Upload> mUploadsAux;
        mUploadsAux = new ArrayList<>();
        mAdapterAux = new ImageAdapter(BrowseActivity.this,mUploadsAux);

            for(Upload u : mUploads )  {
                if (u.getPetType().contains("Fish")) {
                    mUploadsAux.add(u);
                }
            }

        mRecyclerView.setAdapter(mAdapterAux);
        mAdapterAux.notifyDataSetChanged();
    }

    private void showParrots() {
        ImageAdapter mAdapterAux;
        List<Upload> mUploadsAux;
        mUploadsAux = new ArrayList<>();
        mAdapterAux = new ImageAdapter(BrowseActivity.this,mUploadsAux);

            for(Upload u : mUploads )  {
                if (u.getPetType().contains("Parrot")) {
                    mUploadsAux.add(u);
                }
            }

        mRecyclerView.setAdapter(mAdapterAux);
        mAdapterAux.notifyDataSetChanged();
    }

    private void showHedgehogs() {
        ImageAdapter mAdapterAux;
        List<Upload> mUploadsAux;
        mUploadsAux = new ArrayList<>();
        mAdapterAux = new ImageAdapter(BrowseActivity.this,mUploadsAux);

            for(Upload u : mUploads )  {
                if (u.getPetType().contains("Hedgehog")) {
                    mUploadsAux.add(u);
                }
            }

        mRecyclerView.setAdapter(mAdapterAux);
        mAdapterAux.notifyDataSetChanged();
    }


}