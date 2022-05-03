package com.example.squash;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.squash.firebase.FirebaseInstances;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


public class ProfileFragment extends Fragment {
    TextView emailTxtView, userNameTxtView, logoutTxtView;
    ImageView shareImgView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emailTxtView = view.findViewById(R.id.emailTxtView);
        userNameTxtView = view.findViewById(R.id.nameTxtView);
        logoutTxtView = view.findViewById(R.id.logoutTxtView);
        shareImgView = view.findViewById(R.id.shareImgView);
        setData();
        logoutTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Logout Successful", Toast.LENGTH_SHORT).show();
                FirebaseInstances.getAuthInstance().signOut();
                startActivity(new Intent(getContext(), LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|
                                Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        shareImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Squash");
                String shareMessage= "\nLet me recommend you this application\n\n";
                shareMessage = shareMessage + "Final app link\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "Choose an app"));
            }
        });
    }

    private void setData() {
        FirebaseInstances.getDatabaseReference("Users/"+FirebaseInstances.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String email = emailTxtView.getText().toString() + FirebaseInstances.getEmail();
                            String name = userNameTxtView.getText().toString() +
                                    snapshot.child("firstName").getValue().toString() + " " +
                                    snapshot.child("lastName").getValue().toString();
                            emailTxtView.setText(email);
                            userNameTxtView.setText(name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}