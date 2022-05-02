package com.example.squash;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.squash.models.Files;

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
        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(arrayList.get(holder.getAdapterPosition()).checked){
                    holder.selectCheckBox.setVisibility(View.VISIBLE);
                }else{
                    holder.selectCheckBox.setVisibility(View.GONE);
                }
                arrayList.get(holder.getAdapterPosition()).checked =
                        !arrayList.get(holder.getAdapterPosition()).checked;
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView fileNameTextView;
        public LinearLayout layout;
        public CheckBox selectCheckBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileNameTextView = itemView.findViewById(R.id.fileNameTxtView);
            layout = itemView.findViewById(R.id.filesLayout);
            selectCheckBox = itemView.findViewById(R.id.selectCheckBox);
        }
    }
}
