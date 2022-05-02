package com.example.squash.models;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Files {
    public File encodedFile, decodeFile, binFile;
    public String fileName, password, fileType,url1,url2;
    public Uri uri;
    public boolean checked;

    public Files(Uri uri, String fileName, String fileType)  {
        this.uri = uri;
        this.fileName = fileName;
        this.fileType =fileType;
    }

    public Files(String url1, String  url2,String fileName) {
        this.url1 = url1;
        this.url2 = url2;
        this.fileName = fileName;
        this.checked = false;
    }

    //    public Files(File encodedFile, File decodedFile, String password) {
//        this.encodedFile = encodedFile;
//        this.decodedFile = decodedFile;
//        this.password = password;
//    }
}
