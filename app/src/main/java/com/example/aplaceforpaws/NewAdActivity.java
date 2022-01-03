package com.example.aplaceforpaws;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class NewAdActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText petName, petAge, petDescription;
    Spinner petType;
    String choice;
    Member member;
    Button done;
    Button addPicture;
    private ImageView image;

    ActivityResultLauncher<Intent> activityResultLauncher;

    private ProgressBar progressBar;

    private Uri imageUri;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_ad_page);

        petName = findViewById(R.id.newAdPetName);
        petAge = findViewById(R.id.newAdPetAge);
        petDescription = findViewById(R.id.newAdPetDescription);
        petType = findViewById(R.id.petTypeChoiceBox);
        image = findViewById(R.id.petImg);

        List<String> categories = new ArrayList<>();

        categories.add(0, "Type");
        categories.add("Cat");
        categories.add("Dog");
        categories.add("Fish");
        categories.add("Parrot");
        categories.add("Hedgehog");

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class)); // sa te duca la pagina principala fiindca nu esti logat
            finish();
        }

        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        petType.setAdapter(adapter);
        petType.setOnItemSelectedListener(this);
        member = new Member();

        Button backButton = findViewById(R.id.newAdBackButton);
        backButton.setOnClickListener(v -> backToIntermediate());

        addPicture = findViewById(R.id.addImage);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == RESULT_OK && result.getData() != null)
            {
                Intent data = result.getData();
                imageUri = data.getData();
                image.setImageURI(imageUri);
            }
        });
        addPicture.setOnClickListener(this::addPicture);

        done = findViewById(R.id.done);
        done.setOnClickListener(v -> saveValue(choice));

    }

    private void addPicture(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        activityResultLauncher.launch(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getItemAtPosition(position).equals("Type")) {
            //nothing
        } else {

            String text = parent.getItemAtPosition(position).toString();
            Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
            choice = petType.getSelectedItem().toString();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



    private void saveValue(String choice) {
        if (choice.equals("Type")) {
            Toast.makeText(this, "Please select a pet type", Toast.LENGTH_SHORT).show();
            return;
        } else {
            member.setPetType(choice);
            //databaseReference.setValue(choice);
            Toast.makeText(this, "Value saved !", Toast.LENGTH_SHORT).show();
        }
    }


    private void backToIntermediate() {
        Intent intent = new Intent(this, IntermediateActivity.class);
        startActivity(intent);
        finish();
    }

}
