package com.example.aplaceforpaws;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class BrowseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_page);
        Button back = findViewById(R.id.browseBackButton);
        back.setOnClickListener(v -> backToIntermediate());
    }

    private void backToIntermediate() {
        Intent intent = new Intent(this, IntermediateActivity.class);
        startActivity(intent);
        finish();
    }
}
