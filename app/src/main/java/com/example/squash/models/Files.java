package com.example.squash.models;

import java.io.File;

public class Files {
    File file;
    String fileName, password;

    public Files(File file, String fileName, String password) {
        this.file = file;
        this.fileName = fileName;
        this.password = password;
    }
}
