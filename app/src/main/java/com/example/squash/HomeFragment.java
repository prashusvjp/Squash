package com.example.squash;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.squash.firebase.FirebaseInstances;
import com.example.squash.models.Files;
import com.example.squash.models.encode_decode.Huffman;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;


public class HomeFragment extends Fragment {
    FloatingActionButton addFileFAB;
    TextInputEditText getFileEdtTxt;
    Button getFileBtn;
    String getFileTxt;
    LinearLayout noFilesFoundLayout;
    ArrayList<Files> fileDataArrayList = new ArrayList<Files>();
    RecyclerView filesRecyclerView;
    FileAdapters fileAdapters;
    Files files;
    Uri uri;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addFileFAB = view.findViewById(R.id.addFileFAB);
        getFileBtn = view.findViewById(R.id.getFileBtn);
        getFileEdtTxt = view.findViewById(R.id.getFileEditTxt);
        noFilesFoundLayout = view.findViewById(R.id.noFilesFoundLayout);
        filesRecyclerView = view.findViewById(R.id.filesRecyclerView);
        getData();
        getFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFileTxt = getFileEdtTxt.getText().toString();
                if(TextUtils.isEmpty(getFileTxt)){
                    getFileEdtTxt.setError("Field should not be empty");
                    getFileEdtTxt.requestFocus();
                }else{
                    getFiles();
                }
            }
        });

        addFileFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");
                        intent = Intent.createChooser(intent, "Choose a file");
                        startActivityForResult(intent, 1);
                }else{
                    Toast.makeText(getActivity(), "Permission denied, to access media.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getFiles() {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            if(data==null){
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
            uri = data.getData();
            encodeFile(uri);
        }
    }

    private void uploadFiles(Files files) {
        final String[] url1 = new String[2];
        try {
            StorageReference storageReference =  FirebaseInstances.getStorageReference()
                    .child(FirebaseInstances.getUid())
                    .child(files.fileName);
            InputStream inputStream1 = getActivity().getContentResolver()
                    .openInputStream(Uri.fromFile(files.encodedFile));
            UploadTask uploadTask =storageReference.child("encoded.txt").putStream(inputStream1);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(getActivity(),
                            "Something, went wrong please try again later.\n"+exception.toString(),
                            Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    try {
                        storageReference.child("encoded.txt").getDownloadUrl()
                                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if(task.isSuccessful()){
                                    url1[0] =task.getResult().toString();
                                }
                            }
                        });
                        InputStream inputStream2 = getActivity().getContentResolver()
                                .openInputStream(Uri.fromFile(files.binFile));
                        UploadTask uploadTask1 = storageReference.child("mappings.txt")
                                .putStream(inputStream2);
                        uploadTask1.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(),
                                        "Something, went wrong please try again later.\n" + e.toString(),
                                        Toast.LENGTH_LONG).show();
                                storageReference.child("encoded.txt").delete();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                storageReference.child("mappings.txt").getDownloadUrl()
                                        .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if(task.isSuccessful()){
                                                    url1[1] =task.getResult().toString();
                                                    updateDB(url1[0],url1[1],files.fileName);
                                                }
                                            }
                                        });
                                StorageMetadata metadata = new StorageMetadata.Builder()
                                        .setCustomMetadata("fileType",files.fileType)
                                        .setCustomMetadata("passcode",files.password)
                                        .build();
                                storageReference.child("encoded.txt").updateMetadata(metadata);
                                Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();

                            }
                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "File not found", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } catch (Exception e){
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDB(String url1,String url2, String fileName) {
        class Data{
            public String url1,url2,fileName;

            public Data(String url1, String url2,String fileName) {
                this.fileName=fileName;
                this.url1 = url1;
                this.url2 = url2;
            }
        }
        FirebaseInstances.getDataBaseReference("Users/" + FirebaseInstances.getUid())
                .child("Files").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int key=1;
                if(snapshot.exists()){
                    for(DataSnapshot ds : snapshot.getChildren()){
                        key = Integer.parseInt(ds.getKey()) +1;
                    }
                }
                FirebaseInstances.getDataBaseReference("Users/" + FirebaseInstances.getUid())
                        .child("Files/"+key).setValue(new Data(url1,url2,fileName));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    public String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }

    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver()
                    .query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    private void encodeFile(Uri uri) {
        Files files = new Files(uri,getFileName(uri), getActivity().getContentResolver().getType(uri));
        Huffman encode = new Huffman(getActivity());
        try{
            encode.encodeFile(uri);
        }catch(IOException e){
            Toast.makeText(getActivity(), "174: "+e.toString(), Toast.LENGTH_SHORT).show();
        }


        files.encodedFile = new File(getActivity().getFilesDir(),"encoded.txt");
        files.binFile = new File(getActivity().getFilesDir(), "mappings.txt");
        files.password = getRandomNumberString();
        uploadFiles(files);
    }

    private void checkPermissions(){
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            requestStoragePermission();
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(getActivity())
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Toast.makeText(getActivity(), "Hello!", Toast.LENGTH_SHORT).show();
        if (requestCode == 1)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getData() {
        FirebaseInstances.getDataBaseReference("Users/"+FirebaseInstances.getUid()+"/Files")
                .limitToLast(3)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            noFilesFoundLayout.setVisibility(View.GONE);
                            for (DataSnapshot i : snapshot.getChildren()) {
                                fileDataArrayList.add(0,new Files(
                                        i.child("url1").getValue().toString(),
                                        i.child("url2").getValue().toString(),
                                        i.child("fileName").getValue().toString()
                                ));
                            }

                            fileAdapters = new FileAdapters(fileDataArrayList,getActivity());
                            filesRecyclerView.setAdapter(fileAdapters);
                            filesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        }else
                            noFilesFoundLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}