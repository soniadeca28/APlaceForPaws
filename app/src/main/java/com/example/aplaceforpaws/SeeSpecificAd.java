package com.example.aplaceforpaws;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class SeeSpecificAd extends AppCompatActivity {

    private String dlUrl, pName, pDescr;
    private StorageReference storageReference;
    private SpecificAdImageAdapter mAdapter;
    FirebaseFirestore db;
    private RecyclerView mRecyclerView;

    private List<Upload> mUploads;

    TextView petName, petDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_specific_ad);

        Bundle info = getIntent().getExtras();
        dlUrl = info.getString("dlUrl");

        Button back = findViewById(R.id.specAdBackButton);
        back.setOnClickListener(v -> backToBrowse());

        mRecyclerView = findViewById(R.id.recycler_view_spec_ad);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        mUploads = new ArrayList<>();
        mAdapter = new SpecificAdImageAdapter(SeeSpecificAd.this, mUploads);

        mRecyclerView.setAdapter(mAdapter);

        EventChangeListener();

    }

    private void EventChangeListener() {

        db.collection("uploads")
                .addSnapshotListener((value, error) -> {

                    if (error != null) {
                        Log.e("Firestore error", error.getMessage());
                        return;
                    }
                    assert value != null;
                    for (DocumentChange dc : value.getDocumentChanges()) {

                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            if (dc.getDocument().toObject(Upload.class).getDownloadUrl().equals(dlUrl)) {
                                mUploads.add(dc.getDocument().toObject(Upload.class));
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }

                });
    }

    private void backToBrowse() {
        Intent intent = new Intent(this, BrowseActivity.class);
        startActivity(intent);
        finish();
    }
}