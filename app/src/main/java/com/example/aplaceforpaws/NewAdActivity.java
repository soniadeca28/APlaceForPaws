package com.example.aplaceforpaws;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class NewAdActivity extends AppCompatActivity {

    EditText petName, petAge, petDescription;
    Spinner petType;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_ad_page);

        petName = findViewById(R.id.newAdPetName);
        petAge = findViewById(R.id.newAdPetAge);
        petDescription = findViewById(R.id.newAdPetDescription);
        petType = findViewById(R.id.petTypeChoiceBox);

        Button backButton = findViewById(R.id.newAdBackButton);
        backButton.setOnClickListener(v -> backToIntermediate());

        Button addPicture = findViewById(R.id.addImage);
        addPicture.setOnClickListener(v -> addPicture());

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class)); // sa te duca la pagina principala fiindca nu esti logat
            finish();
        }

    }

    private void backToIntermediate() {
        Intent intent = new Intent(this, IntermediateActivity.class);
        startActivity(intent);
        finish();
    }

    private void addPicture() {
    }
}
