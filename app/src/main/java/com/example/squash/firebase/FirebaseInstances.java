package com.example.squash.firebase;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

    public static String getEmail(){
        return getAuthInstance().getCurrentUser().getEmail();
    }

    public static StorageReference getStorageReference(){
        return FirebaseStorage.getInstance().getReference();
    }
}
