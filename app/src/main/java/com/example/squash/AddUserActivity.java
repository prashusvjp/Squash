package com.example.squash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.squash.firebase.FirebaseInstances;
import com.example.squash.models.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

public class AddUserActivity extends AppCompatActivity {
    TextInputEditText fnameETxt, lnameETxt;
    Button proceedBtn;
    String fname, lname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        initializeViews();
    }

    private void initializeViews() {
        fnameETxt = findViewById(R.id.fnameEditTxt);
        lnameETxt = findViewById(R.id.lnameEditTxt);
        proceedBtn = findViewById(R.id.proceedBtn);
    }

    @Override
    protected void onStart() {
        super.onStart();
        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               fname= fnameETxt.getText().toString();
               lname= lnameETxt.getText().toString();
               if(TextUtils.isEmpty(fname)){
                   fnameETxt.setError("Field cannot be empty");
                   fnameETxt.requestFocus();
               }else {
                   FirebaseInstances.getDatabaseReference("Users")
                           .child(FirebaseInstances.getUid())
                           .setValue(new UserData(fname,lname))
                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if(task.isSuccessful()){
                               Toast.makeText(AddUserActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                               startActivity(new Intent(AddUserActivity.this,NavigationActivity.class)
                                       .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|
                                               Intent.FLAG_ACTIVITY_NEW_TASK));
                           }else{
                               Toast.makeText(AddUserActivity.this, "Sorry, something went wrong, please try again later!", Toast.LENGTH_SHORT).show();
                               FirebaseInstances.getAuthInstance().getCurrentUser().delete();
                               startActivity(new Intent(AddUserActivity.this,RegisterActivity.class)
                                       .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|
                                               Intent.FLAG_ACTIVITY_NEW_TASK));
                           }
                       }
                   });
               }
            }
        });
    }
}