package com.example.aplaceforpaws;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SeeMyAdActivity extends AppCompatActivity {
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_my_ad);

        Button back = findViewById(R.id.myAdBackButton);
        back.setOnClickListener(v -> backToMyAds());

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class)); // sa te duca la pagina principala fiindca nu esti logat
            finish();
        }

    }
    private void backToMyAds() {
        Intent intent = new Intent(this, MyAdsActivity.class);
        startActivity(intent);
        finish();
    }
}
