package com.example.aplaceforpaws;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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
import java.util.Locale;

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
    Spinner petLocation;
    Member member;
    LocationRequest locationRequest;
    LocationCallback locationCallBack;
    FusedLocationProviderClient fusedLocationProviderClient;
    String address;
    public static final int FAST_UPDATE = 5;
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    private int pos1, pos2, pos3;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_page);


        locationRequest = LocationRequest.create()
                .setInterval(100)
                .setFastestInterval(1000 * FAST_UPDATE)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(100);

        locationCallBack = new LocationCallback() {

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                try {
                    address = makeLocation(locationResult.getLastLocation());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };

        updateGPS();
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, Looper.getMainLooper()); // era null inainte


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#f9a895"));
        setSupportActionBar(toolbar);

        petType = findViewById(R.id.spinner_browse);
        petFilter = findViewById(R.id.spinner_filter_by_type);
        petLocation = findViewById(R.id.location);

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

        setupFilterByType();

        setupLocationFilter();

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

    private void setupSort(){ //pos1 e pentru asta
       pos2=0;
       pos3=0;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        petType.setAdapter(adapter);
        petType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                switch(position) {
                    case 1: {
                        sortByName();
                        break;
                    }
                    case 2: {
                        sortByAge();
                        break;
                    }

                    default: mRecyclerView.setAdapter(mAdapter);
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
                switch(position) {

                    case 1 : {
                        showCats();
                        break;
                    }
                    case 2 : {
                        showDogs();
                        break;
                    }

                    case 3 : {
                        showFish();
                        break;
                    }

                    case 4 : {
                        showParrots();
                        break;
                    }

                    case 5 : {
                        showHedgehogs();
                        break;
                    }
                    default : mRecyclerView.setAdapter(mAdapter);
                }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateGPS();
            } else {
                Toast.makeText(this, "The app requires permission to be granted in order to work", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void updateGPS() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(BrowseActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //user provided permission
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                try {
                    address = makeLocation(location);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            //permission not granted
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }


    private String makeLocation(Location location) throws IOException {
        Geocoder geocoder = new Geocoder(BrowseActivity.this, Locale.getDefault());
        String address = "";
        List<Address> addressList;
        try {
            addressList = geocoder.getFromLocation(
                    location.getLatitude(), location.getLongitude(), 1
            );
            address = addressList.get(0).getSubAdminArea();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(BrowseActivity.this, "Adress is :" + address, Toast.LENGTH_SHORT).show();
        return address;
    }
    private void setupLocationFilter(){

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.filter_location, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        petLocation.setAdapter(adapter);
        petLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if(position == 1) filterByLocation();
                else mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void filterByLocation(){

        ImageAdapter mAdapterAux;
        List<Upload> mUploadsAux;
        String location;
        mUploadsAux = new ArrayList<>();
        mAdapterAux = new ImageAdapter(BrowseActivity.this,mUploadsAux);
        for(Upload u : mUploads )  {
            location=u.getAddress();
            if (address.compareTo(location)==0) {
                mUploadsAux.add(u);
            }
        }
        mRecyclerView.setAdapter(mAdapterAux);
        mAdapterAux.notifyDataSetChanged();

    }

}