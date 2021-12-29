package com.example.aplaceforpaws;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button loginButton;
    ProgressBar progressBar;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        progressBar = findViewById(R.id.loginProgressBar);
        Button back = findViewById(R.id.backButton);
        back.setOnClickListener(v -> backToMainActivity());
        loginButton = findViewById(R.id.loginButton);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), IntermediateActivity.class)); // sa te duca la pagina la care vei fi cand esti logat
            finish();
        }

        loginButton.setOnClickListener(v -> {
            final String eml = email.getText().toString().trim();
            final String psw = password.getText().toString().trim();

            if (TextUtils.isEmpty(eml)) {
                email.setError("Username required");
                return;
            }

            if (TextUtils.isEmpty(psw)) {
                password.setError("Please provide a password");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            //auth:

            auth.signInWithEmailAndPassword(eml, psw).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login successful! \n", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), IntermediateActivity.class));
                    finish();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this, "Login could not be completed! \n" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        });

    }

    private void backToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
