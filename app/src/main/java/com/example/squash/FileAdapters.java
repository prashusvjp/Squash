package com.example.squash;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.squash.firebase.FirebaseInstances;
import com.example.squash.models.Files;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageMetadata;

import java.util.ArrayList;

public class FileAdapters extends RecyclerView.Adapter<FileAdapters.ViewHolder>{
    ArrayList<Files> arrayList;
    Context context;
    public FileAdapters(ArrayList<Files> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }
    @NonNull
    @Override
    public FileAdapters.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.file_display, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull FileAdapters.ViewHolder holder, int position) {
        Files file = arrayList.get(holder.getAdapterPosition());
        holder.fileNameTextView.setText(file.fileName);
        holder.layout.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                if(event.getAction() == DragEvent.ACTION_DRAG_ENDED){
                    if(holder.optionsLayout.getVisibility() == View.VISIBLE)
                        holder.optionsLayout.setVisibility(View.GONE);
                    else
                        holder.optionsLayout.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.optionsLayout.getVisibility() == View.VISIBLE)
                    holder.optionsLayout.setVisibility(View.GONE);
                else
                    holder.optionsLayout.setVisibility(View.VISIBLE);
            }
        });


        holder.deleteFileImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder
                        = new AlertDialog
                        .Builder(context);
                builder.setMessage("Are you sure you want to delete the file?");

                builder.setTitle("Alert !");

                builder.setCancelable(false);

                builder
                        .setPositiveButton(
                                "Yes",
                                new DialogInterface
                                        .OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which)
                                    {
                                        String uid=FirebaseInstances.getUid();
                                        FirebaseInstances.getDatabaseReference("Users/"+uid+"/Files")
                                                .child(file.id).removeValue();
                                        FirebaseInstances.getStorageReference().child(uid)
                                                .child(file.fileName).child("encoded.txt").delete();
                                        FirebaseInstances.getStorageReference().child(uid)
                                                .child(file.fileName).child("mappings.txt").delete();
                                    }
                                });

                builder
                        .setNegativeButton(
                                "Cancel",
                                new DialogInterface
                                        .OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which)
                                    {

                                        // If user click no
                                        // then dialog box is canceled.
                                        dialog.cancel();
                                    }
                                });

                // Create the Alert dialog
                AlertDialog alertDialog = builder.create();
                // Show the Alert Dialog box
                alertDialog.show();
            }
        });

        holder.shareFileImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseInstances.getStorageReferenceFromURL(file.url1)
                        .getMetadata()
                        .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        if(storageMetadata!=null){
                            String link =
                                    FirebaseInstances
                                            .getDatabaseReference("Users/"+FirebaseInstances.getUid()+"/Files")
                                            .child(file.id).toString();
                            String message = "Hey, I have attached the link for "+file.fileName+".\n" +
                                    "Please install Squash app to view and download the attachment.\n\n"+
                                    "Link: "+link+"\nPasscode: "+storageMetadata.getCustomMetadata("passcode");
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Squash");
                            intent.putExtra(android.content.Intent.EXTRA_TEXT, message);
                            context.startActivity(Intent.createChooser(intent, "Send link via"));
                        }else{
                            Toast.makeText(context, "Sorry, something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView fileNameTextView;
        public LinearLayout optionsLayout;
        CardView layout;
        ImageView deleteFileImgView, shareFileImgView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileNameTextView = itemView.findViewById(R.id.fileNameTxtView);
            layout = itemView.findViewById(R.id.filesLayout);
            optionsLayout = itemView.findViewById(R.id.optionsLayout);
            deleteFileImgView= itemView.findViewById(R.id.deleteFileImgView);
            shareFileImgView = itemView.findViewById(R.id.shareFileImgView);

        }
    }
}
