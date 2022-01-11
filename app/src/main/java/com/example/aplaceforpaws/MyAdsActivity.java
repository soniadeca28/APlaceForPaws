package com.example.aplaceforpaws;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyAdsActivity extends AppCompatActivity {

    FirebaseAuth auth;
    private MyAdsImageAdapter mAdapter;
    private List<Upload> mUploads;
    FirebaseFirestore db;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads);

        Button back = findViewById(R.id.myAdsBackButton);
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

        RecyclerView mRecyclerView = findViewById(R.id.recycler_view_my_ads);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        mUploads = new ArrayList<>();
        mAdapter = new MyAdsImageAdapter(MyAdsActivity.this, mUploads);


        mRecyclerView.setAdapter(mAdapter);

        EventChangeListener();

        if(mUploads.size() == 0 )
        {
            if(progressDialog.isShowing())
                progressDialog.dismiss();
        }


    }

    private void EventChangeListener() {
        db.collection("uploads")
                .addSnapshotListener((value, error) -> {

                    if (error != null) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Log.e("Firestore error", error.getMessage());
                        return;
                    }
                    assert value != null;
                    for (DocumentChange dc : value.getDocumentChanges()) {

                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            if (dc.getDocument().toObject(Upload.class).getCurrentUser().equals(Objects.requireNonNull(auth.getCurrentUser()).getEmail())) {
                                mUploads.add(dc.getDocument().toObject(Upload.class));
                            }
                        }

                        mAdapter.notifyDataSetChanged();

                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }

                });
    }

    private void backToIntermediate() {
        Intent intent = new Intent(this, IntermediateActivity.class);
        startActivity(intent);
        finish();
    }
}
