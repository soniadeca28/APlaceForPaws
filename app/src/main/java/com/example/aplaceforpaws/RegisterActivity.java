package com.example.aplaceforpaws;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    EditText email, password, name, phone;
    Button registerButton;
    FirebaseAuth auth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userId;
    public static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);

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
                email.setError("Username required");
                return;
            }

            if (TextUtils.isEmpty(psw)) {
                password.setError("Please provide a password");
                return;
            }

            if (password.length() < 6) {
                password.setError("The password needs to be at least 6 characters");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            //create user and store their info:

            auth.createUserWithEmailAndPassword(eml, psw).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                    // store the other data here
                    userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
                    DocumentReference documentReference = fStore.collection("users").document(userId);
                    Map<String, String> user = new HashMap<>();
                    user.put("name", nm);
                    user.put("email", eml);
                    user.put("phone number", phn);
                    documentReference.set(user).addOnSuccessListener(unused -> Log.d(TAG, "onSuccess: user Profile is created for : " + userId)).addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.toString()));
                    startActivity(new Intent(getApplicationContext(), IntermediateActivity.class));
                    finish();
                    //FirebaseAuth.getInstance().signOut();
                    finish();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(RegisterActivity.this, "Could not create the account" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        });
    }

    private void backToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}