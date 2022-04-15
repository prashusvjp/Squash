package com.example.squash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {
    TextView newUserTxt;
    TextInputEditText emailEditTxt, passwordEditTxt;
    Button loginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        newUserTxt.setOnClickListener(new LoginEvents().newUserOnClick);
        loginBtn.setOnClickListener(new LoginEvents().login);
    }

    private void initializeViews() {
        newUserTxt = findViewById(R.id.newUserTxtView);
        emailEditTxt = findViewById(R.id.emailEditTxt);
        passwordEditTxt = findViewById(R.id.passwordEditTxt);
        loginBtn = findViewById(R.id.loginButton);
    }

    class LoginEvents{
        View.OnClickListener newUserOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        };

       View.OnClickListener login = new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String email = emailEditTxt.getText().toString();
               String password = passwordEditTxt.getText().toString();

               if(TextUtils.isEmpty(email)){
                   emailEditTxt.setError("Email cannot be empty");
                   emailEditTxt.requestFocus();
               }else if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                   emailEditTxt.setError("Enter a valid email ID");
                   emailEditTxt.requestFocus();
               }else if(TextUtils.isEmpty(password)){
                   passwordEditTxt.setError("Password cannot be empty");
                   emailEditTxt.requestFocus();
               }else {
                   FirebaseAuth mAuth = FirebaseAuth.getInstance();
                   mAuth.signInWithEmailAndPassword(email,password)
                           .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(LoginActivity.this,
                                        "Login successful",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, NavigationActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                            }else{
                                try{
                                    throw task.getException();
                                }catch (FirebaseAuthInvalidUserException ivalidEmail){
                                    emailEditTxt.setError("User doesn't exists");
                                    emailEditTxt.requestFocus();
                                }catch (FirebaseAuthInvalidCredentialsException invalidCredentialsException){
                                    passwordEditTxt.setError("Invalid username/password");
                                    passwordEditTxt.requestFocus();
                                }catch (Exception e){
                                    Toast.makeText(LoginActivity.this,
                                            "Sorry! Something went wrong, Please try again later.", Toast.LENGTH_SHORT).show();
                                }
                            }
                       }
                   });
               }
           }
       };

    }
}