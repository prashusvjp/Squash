package com.example.squash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class RegisterActivity extends AppCompatActivity {
    TextInputEditText emailEditTxt, passwordEditTxt,confPasswordEditTxt;
    Button registerBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializeViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBtn.setOnClickListener(new RegisterEvents().register);
    }

    private void initializeViews() {
        emailEditTxt = findViewById(R.id.emailEditTxt);
        passwordEditTxt = findViewById(R.id.passwordEditTxt);
        registerBtn = findViewById(R.id.signUpButton);
        confPasswordEditTxt = findViewById(R.id.confPasswordEditTxt);
    }

    class RegisterEvents{

        View.OnClickListener register = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditTxt.getText().toString();
                String password = passwordEditTxt.getText().toString();
                String confPassword = confPasswordEditTxt.getText().toString();
                if(TextUtils.isEmpty(email)){
                    emailEditTxt.setError("Email cannot be empty");
                    emailEditTxt.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailEditTxt.setError("Enter a valid email id");
                    emailEditTxt.requestFocus();
                }else if(doesUserExists(email)) {
                    emailEditTxt.setError("User already exists");
                    emailEditTxt.requestFocus();
                }else if(TextUtils.isEmpty(password)){
                    passwordEditTxt.setError("Password cannot be empty");
                    passwordEditTxt.requestFocus();
                }else if(!confPassword.equals(password)) {
                    confPasswordEditTxt.setError("Passwords do not match");
                    confPasswordEditTxt.requestFocus();
                }else{
                    FirebaseAuth mAuth= FirebaseAuth.getInstance();
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                   if(task.isSuccessful()){
                                       Toast.makeText(RegisterActivity.this,
                                               "User register. Welcome!", Toast.LENGTH_SHORT).show();
                                       startActivity(new Intent(RegisterActivity.this,AddUserActivity.class)
                                               .setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                                   }else{
                                       Toast.makeText(RegisterActivity.this,
                                               "Sorry, something went wrong. Please try again later", Toast.LENGTH_SHORT).show();
                                   }
                                }
                            });
                }
            }
        };

        private boolean doesUserExists(String email) {
            final boolean[] result = {true};
            FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            result[0] = task.getResult().getSignInMethods().isEmpty();
                        }
                    });
            return !result[0];
        }

    }

}