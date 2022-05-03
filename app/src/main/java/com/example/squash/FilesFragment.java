package com.example.squash;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.squash.firebase.FirebaseInstances;
import com.example.squash.models.Files;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FilesFragment extends Fragment {
    ArrayList<Files> fileDataArrayList;
    LinearLayout noFilesFoundLayout;
    RecyclerView filesRecyclerView;
    FileAdapters fileAdapters;
     @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_files, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        filesRecyclerView = view.findViewById(R.id.filesRecyclerView);
        noFilesFoundLayout = view.findViewById(R.id.noFilesFoundLayout);
        getData();

    }

    private void getData() {
        FirebaseInstances.getDatabaseReference("Users/"+FirebaseInstances.getUid()+"/Files")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            fileDataArrayList = new ArrayList<>();
                            noFilesFoundLayout.setVisibility(View.GONE);
                            filesRecyclerView.setVisibility(View.VISIBLE);
                            for (DataSnapshot i : snapshot.getChildren()) {
                                fileDataArrayList.add(0,new Files(
                                        i.getKey(),
                                        i.child("url1").getValue().toString(),
                                        i.child("url2").getValue().toString(),
                                        i.child("fileName").getValue().toString()
                                ));
                            }

                            fileAdapters = new FileAdapters(fileDataArrayList,getActivity());
                            filesRecyclerView.setAdapter(fileAdapters);
                            filesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        }else {
                            filesRecyclerView.setVisibility(View.GONE);
                            noFilesFoundLayout.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}