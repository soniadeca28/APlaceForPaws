package com.example.aplaceforpaws;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button login = findViewById(R.id.loginPageButton);
        login.setOnClickListener(v -> openLoginPageActivity());
    }

    private void openLoginPageActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}