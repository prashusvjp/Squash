package com.example.squash.models;

import android.content.Context;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Files {
    public File encodedFile, binFile;
    public String fileName, password, fileType,url1,url2,id;
    public Uri uri;
    public boolean checked;

    public Files(Uri uri, String fileName, String fileType)  {
        this.uri = uri;
        this.fileName = fileName;
        this.fileType =fileType;
    }

    public Files(String id,String url1, String  url2,String fileName) {
        this.id=id;
        this.url1 = url1;
        this.url2 = url2;
        this.fileName = fileName;
        this.checked = false;
    }

    public Files(Context context) {
        encodedFile = new File(context.getFilesDir(),"encoded.txt");
        binFile = new File(context.getFilesDir(),"mappings.txt");
    }

    //    public Files(File encodedFile, File decodedFile, String password) {
//        this.encodedFile = encodedFile;
//        this.decodedFile = decodedFile;
//        this.password = password;
//    }
}
