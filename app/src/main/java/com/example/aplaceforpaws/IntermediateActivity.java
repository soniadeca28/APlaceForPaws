package com.example.aplaceforpaws;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class IntermediateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intermediate_page);

        Button logout = findViewById(R.id.logout);
        logout.setOnClickListener(v -> logout());

        Button browse = findViewById(R.id.browse);
        browse.setOnClickListener(v -> browse());
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void browse() {
        Intent intent = new Intent(this, BrowseActivity.class);
        startActivity(intent);
    }
}
