package com.example.aplaceforpaws;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class IntermediateActivity extends AppCompatActivity {

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intermediate_page);

        Button logout = findViewById(R.id.logout);
        logout.setOnClickListener(v -> logout());

        Button browse = findViewById(R.id.browse);
        browse.setOnClickListener(v -> browse());

        Button newAd = findViewById(R.id.new_ad);
        newAd.setOnClickListener(v -> newAd());

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class)); // sa te duca la pagina principala fiindca nu esti logat
            finish();
        }

    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void browse() {
        Intent intent = new Intent(this, BrowseActivity.class);
        startActivity(intent);
        finish();
    }

    private void newAd() {
        Intent intent = new Intent(this, NewAdActivity.class);
        startActivity(intent);
        finish();
    }
}
