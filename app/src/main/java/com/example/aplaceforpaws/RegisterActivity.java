package com.example.aplaceforpaws;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    EditText email, password;
    Button registerButton;
    FirebaseAuth auth;
   ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);

        Button back = findViewById(R.id.backButton);
        back.setOnClickListener(v -> backToMainActivity());
        email = findViewById(R.id.registerEmail);
        password = findViewById(R.id.registerPassword);
        registerButton = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.registerProgressBar);

        auth = FirebaseAuth.getInstance();

       /* if(auth.getCurrentUser() != null)
        {
            startActivity(new Intent(getApplicationContext(),MainActivity.class)); // sa te duca la pagina la care vei fi cand esti logat
            finish();
        }*/

        registerButton.setOnClickListener(v -> {
            String eml = email.getText().toString().trim();
            String psw = password.getText().toString().trim();

            if(TextUtils.isEmpty(eml))
            {
                email.setError("Username required");
                return;
            }

            if(TextUtils.isEmpty(psw))
            {
                password.setError("Please provide a password");
                return;
            }

            if(password.length() < 6)
            {
                password.setError("The password needs to be at least 6 characters");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            auth.createUserWithEmailAndPassword(eml,psw).addOnCompleteListener(task -> {
                if(task.isSuccessful())
                {
                    Toast.makeText(RegisterActivity.this,"Account created successfully",Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                    finish();
                }
                else
                {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(RegisterActivity.this,"Could not create the account" + Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        });
    }

    private void backToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}