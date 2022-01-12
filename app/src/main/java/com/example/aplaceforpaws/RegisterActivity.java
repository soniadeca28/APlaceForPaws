package com.example.aplaceforpaws;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    public static final int FAST_UPDATE = 5;
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    String address;
    EditText email, password, name, phone;
    Button registerButton;
    FirebaseAuth auth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userId;
    public static final String TAG = "TAG";
    LocationRequest locationRequest; //contains quality params for requests to FusedLocationProviderClient
    LocationCallback locationCallBack; //for receiving notifications from FusedLocationProviderClient
    FusedLocationProviderClient fusedLocationProviderClient;
    //google's api for location services

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);


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

        email = findViewById(R.id.registerEmail);
        password = findViewById(R.id.registerPassword);
        name = findViewById(R.id.registerName);
        phone = findViewById(R.id.registerPhone);
        Button back = findViewById(R.id.backButton);
        back.setOnClickListener(v -> backToMainActivity());
        progressBar = findViewById(R.id.registerProgressBar);
        registerButton = findViewById(R.id.registerButton);

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), IntermediateActivity.class)); // sa te duca la pagina la care vei fi cand esti logat
            finish();
        }

        registerButton.setOnClickListener(v -> {
            final String eml = email.getText().toString().trim();
            final String psw = password.getText().toString().trim();
            final String nm = name.getText().toString();
            final String phn = phone.getText().toString();

            if (TextUtils.isEmpty(eml)) {
                email.setError("Username required\n");
                return;
            }

            if (TextUtils.isEmpty(psw)) {
                password.setError("Please provide a password\n");
                return;
            }

            if (password.length() < 6) {
                password.setError("The password needs to be at least 6 characters\n");
                return;
            }


            progressBar.setVisibility(View.VISIBLE);


            //create user and store their info:

            auth.createUserWithEmailAndPassword(eml, psw).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Account created successfully! \n", Toast.LENGTH_SHORT).show();
                    // store the other data here
                    userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
                    DocumentReference documentReference = fStore.collection("users").document(userId);
                    Map<String, String> user = new HashMap<>();
                    user.put("name", nm);
                    user.put("email", eml);
                    user.put("phone number", phn);
                    user.put("address",address);
                    documentReference.set(user).addOnSuccessListener(unused -> Log.d(TAG, "onSuccess: user Profile is created for : " + userId)).addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.toString()));
                    startActivity(new Intent(getApplicationContext(), IntermediateActivity.class));
                    finish();
                    finish();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(RegisterActivity.this, "Could not create the account! \n" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        });

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
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(RegisterActivity.this);
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
        Geocoder geocoder = new Geocoder(RegisterActivity.this, Locale.getDefault());
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
        //Toast.makeText(RegisterActivity.this, "Adress is :" + address, Toast.LENGTH_SHORT).show();
        return address;
    }

    private void backToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}