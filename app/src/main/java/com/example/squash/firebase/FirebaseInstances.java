package com.example.squash.firebase;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseInstances {
    static FirebaseDatabase database = FirebaseDatabase
            .getInstance("https://squash-8089f-default-rtdb.asia-southeast1.firebasedatabase.app/");
    public static DatabaseReference getDataBaseReference(String path){
        return database.getReference(path);
    }

    public static FirebaseAuth getAuthInstance(){
        return FirebaseAuth.getInstance();
    }

    public static String getUid(){
        return getAuthInstance().getCurrentUser().getUid();
    }

    public static DataSnapshot getDataFromDB(String path){
        final DataSnapshot[] dataSnapshot = {null};
        getDataBaseReference(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataSnapshot[0]=snapshot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return dataSnapshot[0];
    }
}