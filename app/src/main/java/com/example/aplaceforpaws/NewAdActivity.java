package com.example.aplaceforpaws;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    FirebaseFirestore fStore;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private StorageTask storageTask;

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

        fStore = FirebaseFirestore.getInstance();

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

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        addPicture = findViewById(R.id.addImage);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Intent data = result.getData();
                imageUri = data.getData();
                image.setImageURI(imageUri);
            }
        });
        addPicture.setOnClickListener(this::addPicture);

        done = findViewById(R.id.done);
        done.setOnClickListener(v -> {
            if (storageTask != null && storageTask.isInProgress()) {
                Toast.makeText(NewAdActivity.this, "Upload in progress\n", Toast.LENGTH_SHORT).show();
            } else {
                saveEntry(choice);
            }
        });

    }

    private void addPicture(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        activityResultLauncher.launch(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (!parent.getItemAtPosition(position).equals("Type")) {

            String text = parent.getItemAtPosition(position).toString();
            Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
            choice = petType.getSelectedItem().toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private String getImageExtension(Uri img) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(img));
    }

    private void saveEntry(String choice) {

        final String nm = petName.getText().toString().trim();
        final String ag = petAge.getText().toString().trim();
        final String dc = petDescription.getText().toString().trim();

        if (TextUtils.isEmpty(nm)) {
            petName.setError("name required\n");
            Toast.makeText(this, "Please select the pet's name\n", Toast.LENGTH_SHORT).show();
            return;
        }

        if(choice != null) {
            member.setPetType(choice);
        }
        else
        {
            Toast.makeText(this, "Please select a pet type\n", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(ag)) {
            petAge.setError("age required\n");
            Toast.makeText(this, "Please select the pet's age\n", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(dc)) {
            petDescription.setError("description required\n");
            Toast.makeText(this, "Please provide a description\n", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getImageExtension(imageUri));
            storageTask = fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                //update progressBar !!!!!!!!!!!!!!!! https://www.youtube.com/watch?v=lPfQN-Sfnjw 13:36
                Toast.makeText(NewAdActivity.this, "Ad saved successfully\n", Toast.LENGTH_SHORT).show();
                Upload upload = new Upload(petName.getText().toString().trim(),
                        member.getPetType().trim(),
                        petAge.getText().toString().trim(),
                        petDescription.getText().toString().trim(),
                        Objects.requireNonNull(Objects.requireNonNull(taskSnapshot.getMetadata()).getReference()).getDownloadUrl().toString(),
                        fileReference.getName(),
                        Objects.requireNonNull(auth.getCurrentUser()).getEmail()
                );
                String uploadId = databaseReference.push().getKey();

                assert uploadId != null;
                DocumentReference documentReference = fStore.collection("uploads").document(uploadId);

                Map<String, String> upl = new HashMap<>();
                upl.put("petName", upload.getPetName());
                upl.put("petType", upload.getPetType());
                upl.put("petAge", upload.getPetAge());
                upl.put("petDescription", upload.getDescription());
                upl.put("downloadUrl", upload.getPetImage());
                upl.put("imgName",upload.getImgName());
                upl.put("currentUser",upload.getCurrentUser());
                documentReference.set(upl).addOnSuccessListener(unused -> Log.d("TAG", "onSuccess: entry is created for : " + uploadId + "\n")).addOnFailureListener(e -> Log.d("TAG", "onFailure: " + e.toString()));
                startActivity(new Intent(getApplicationContext(), NewAdActivity.class));
                finish();
            }).addOnFailureListener(e -> Toast.makeText(NewAdActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show()).addOnProgressListener(snapshot -> {
                double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                //update progressbar !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! progressBar.setProgress((int)progress);
            });
        } else {
            Toast.makeText(this, "Please select an image\n", Toast.LENGTH_SHORT).show();
        }
    }

    private void backToIntermediate() {
        Intent intent = new Intent(this, IntermediateActivity.class);
        startActivity(intent);
        finish();
    }

}
