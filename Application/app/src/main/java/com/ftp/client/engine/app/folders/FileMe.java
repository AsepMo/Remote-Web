package com.ftp.client.engine.app.folders;

import android.annotation.TargetApi;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v4.provider.DocumentFile;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

import com.ftp.client.R;
import com.ftp.client.AppController;

public class FileMe {

    public static final String APK = ".apk", MP4 = ".mp4", MP3 = ".mp3", JPG = ".jpg", JPEG = ".jpeg", PNG = ".png", DOC = ".doc", DOCX = ".docx", XLS = ".xls", XLSX = ".xlsx", PDF = ".pdf";
    private static volatile FileMe Instance = null;
    private Context context;

    private FileMe(Context context){
        this.context = context;
    }

    public static FileMe getInstance() {
        FileMe localInstance = Instance;
        if (localInstance == null) {
            synchronized (FileMe.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new FileMe(AppController.getContext());
                }
            }
        }
        return localInstance;
    }

    public static FileMe with(Context context) {
        return new FileMe(context);
    }

    public void FileMe(Context c, String folder, String file, String message){
        File fileToEdit = new File(folder, file);
        try {
            fileToEdit.getParentFile().mkdirs();

            FileOutputStream fos = new FileOutputStream(fileToEdit);
            Writer w = new BufferedWriter(new OutputStreamWriter(fos));
            try {
                w.write(message + "\n");
                w.flush();
                fos.getFD().sync();
            }
            finally {
                w.close();
            }
        }
        catch (IOException e) {
            Log.e(c.getClass().getSimpleName(), "Exception writing file", e);
        }
    }
}

