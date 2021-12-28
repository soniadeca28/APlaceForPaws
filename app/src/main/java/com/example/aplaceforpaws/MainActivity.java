package com.example.aplaceforpaws;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), IntermediateActivity.class)); // sa te duca la pagina la care vei fi cand esti logat
            finish();
        }

        Button login = findViewById(R.id.loginPageButton);
        login.setOnClickListener(v -> openLoginPageActivity());

        Button register = findViewById(R.id.registerPageButton);
        register.setOnClickListener(v -> openRegisterPageActivity());
    }

    private void openLoginPageActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void openRegisterPageActivity() {
        Intent intent2 = new Intent(this, RegisterActivity.class);
        startActivity(intent2);
        finish();
    }
}