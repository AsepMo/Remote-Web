package com.ftp.client.engine.app.folders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.text.TextUtils;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;

import com.ftp.client.R;
import com.ftp.client.AppController;

public class FolderMe {

    /** Note that this is a symlink on the Android M preview. */
    @SuppressLint("SdCardPath")
    public static String EXTERNAL_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static String EVOLUTION_FOLDER = AppController.getContext().getString(R.string.app_name);

    public static String ZFOLDER = getDefaultDir().getAbsolutePath();
    public static String ZFOLDER_APK = ZFOLDER + "/1.Apk";
    public static String ZFOLDER_IMG = ZFOLDER + "/2.Image";
    public static String ZFOLDER_AUDIO = ZFOLDER + "/3.Audio";
    public static String ZFOLDER_AUDIO_RECORDER = ZFOLDER_AUDIO + "/Recorder";
    public static String ZFOLDER_AUDIO_DOWNLOAD = ZFOLDER_AUDIO + "/Download";
    public static String ZFOLDER_AUDIO_CONVERT = ZFOLDER_AUDIO + "/Convert";
    public static String ZFOLDER_VIDEO = ZFOLDER + "/4.Video";
    public static String ZFOLDER_VIDEO_RECORDER = ZFOLDER_VIDEO + "/Recorder";
    public static String ZFOLDER_VIDEO_DOWNLOAD = ZFOLDER_VIDEO + "/Download";
    public static String ZFOLDER_VIDEO_CONVERTED = ZFOLDER_VIDEO + "/Trimmer";
    public static String ZFOLDER_YOUTUBE = ZFOLDER_VIDEO + "/Youtube";
    public static String ZFOLDER_YOUTUBE_ANALYTICS = ZFOLDER_YOUTUBE + "/Analytics";
    public static String ZFOLDER_YOUTUBE_DOWNLOAD = ZFOLDER_YOUTUBE + "/Download";
    public static String ZFOLDER_EBOOK = ZFOLDER + "/5.Ebook";
    public static String ZFOLDER_SCRIPTME = ZFOLDER + "/6.ScriptMe";
    public static String ZFOLDER_ARCHIVE = ZFOLDER + "/7.Archive";
    public static String ZFOLDER_ARCHIVE_ARCHIVES = ZFOLDER + "Archives";
    public static String ZFOLDER_ARCHIVE_EXTRACTED = ZFOLDER + "Extracted";

    public static File getDefaultDir(){
        File file = new File(EXTERNAL_DIR + "/" + AppController.getContext().getString(R.string.app_name));
        return file;
    }

    private static FolderMe mInstance;
    public static FolderMe getFolder() {
        if (mInstance == null) {
            mInstance = new FolderMe(new Builder(AppController.getContext()));
        }
        return mInstance;
    }

    public static void initFolder(FolderMe engine) {
        mInstance = engine;
    }

    public static void initVideoBox(Context context) {
        initFolder(new FolderMe.Builder(context)
                   .setDefaultFolder(FolderMe.ZFOLDER)
                   .setFolderApk(true) 
                   .setFolderImage(true)
                   .setFolderAudio(true)                                        
                   .setFolderArchive(true)
                   .setFolderEbook(true)
                   .setFolderImage(true)
                   .setFolderVideo(true)                                       
                   .setFolderScriptMe(true)
                   .setExternalFileDir(FolderMe.EVOLUTION_FOLDER)
                   .build()); 

    }

    private final String mFolder;
    private final boolean isFolderMe;

    protected FolderMe(Builder builder) {
        mFolder = builder.mFolder;
        isFolderMe = builder.isFolder;
    }

    public String getFolderMe() {
        return mFolder;
    }

    public boolean isFolderMe() {
        return isFolderMe;
    }

    public static class Builder {
        private boolean isFolder = false;
        private String mFolder = null;
        private Context mContext;

        public Builder(Context context) {
            this.mContext = context;
        }

        public String getHomeDir() {
            return mContext.getFilesDir().getAbsolutePath() + "/home";
        }

        public String getUserDir() {
            return mContext.getFilesDir().getAbsolutePath() + "/user";
        }

        public String getExternalCacheDir() {
            return mContext.getExternalCacheDir().getAbsolutePath();
        }

        public String getInternalCacheDir() {
            return mContext.getCacheDir().getAbsolutePath();
        }

        public String getExternalFileDir(String folder) {
            return mContext.getExternalFilesDir(folder).getAbsolutePath();
        }

        public String getInternalFileDir() {
            return mContext.getFilesDir().getAbsolutePath();
        }

        public Builder setDefaultFolder(String Folder) {

            File nomedia = new File(Folder, ".nomedia");
            nomedia.getParentFile().mkdirs();
            if(!nomedia.exists()) {
                try {
                    nomedia.createNewFile();
                } catch (IOException io) {
                    io.getMessage();
                }
            }

            String home = getHomeDir();
            File mHome = new File(home);
            mHome.getParentFile().mkdirs();
            if (!mHome.exists()) {
                mHome.mkdirs();
            }

            String user = getUserDir();
            File mUser = new File(user);
            mUser.getParentFile().mkdirs();
            if (!mUser.exists()) {
                mUser.mkdirs();
            }
            return this;
        }

        public Builder setFolder(String folder) {
            this.isFolder = !TextUtils.isEmpty(folder);
            this.mFolder = folder;
            File mFolderMe = new File(folder);
            if (!mFolderMe.exists()) {
                mFolderMe.mkdirs();
            }
            return this;
        }

        public Builder setFolderApk(boolean isFolder) {
            String folder = ZFOLDER_APK;
            this.isFolder = !TextUtils.isEmpty(folder);

            File mFolderMe = new File(folder);
            if (!mFolderMe.exists()) {
                mFolderMe.mkdirs();
            }

            return this;
        }

        public Builder setFolderImage(boolean isFolder) {
            String folder = ZFOLDER_IMG;
            this.isFolder = !TextUtils.isEmpty(folder);

            File mFolderMe = new File(folder);
            if (!mFolderMe.exists()) {
                mFolderMe.mkdirs();
            }
            return this;
        }

        public Builder setFolderScriptMe(boolean isFolder) {
            String folder = ZFOLDER_SCRIPTME;
            this.isFolder = !TextUtils.isEmpty(folder);

            File mFolderMe = new File(folder);
            if (!mFolderMe.exists()) {
                mFolderMe.mkdirs();
            }

            return this;
        }

        public Builder setFolderAudio(boolean isFolder) {
            String folder = ZFOLDER_AUDIO;
            this.isFolder = !TextUtils.isEmpty(folder);

            File mFolderMe = new File(folder);
            if (!mFolderMe.exists()) {
                mFolderMe.mkdirs();
            }

            return this;
        }

        public Builder setFolderAudio_Converted(boolean isFolder) {
            String folder = ZFOLDER_AUDIO_CONVERT;
            this.isFolder = !TextUtils.isEmpty(folder);

            File mFolderMe = new File(folder);
            if (!mFolderMe.exists()) {
                mFolderMe.mkdirs();
            }

            return this;
        }

        public Builder setFolderVideo(boolean isFolder) {
            String folder = ZFOLDER_VIDEO;
            this.isFolder = !TextUtils.isEmpty(folder);

            File mFolderMe = new File(folder);
            if (!mFolderMe.exists()) {
                mFolderMe.mkdirs();
            }

            return this;
        }

        public Builder setFolderVideoConverted(boolean isFolder) {
            String folder = ZFOLDER_VIDEO_CONVERTED;
            this.isFolder = !TextUtils.isEmpty(folder);

            File mFolderMe = new File(folder);
            if (!mFolderMe.exists()) {
                mFolderMe.mkdirs();
            }
            return this;
        }

        public Builder setFolderYoutube(boolean isFolder) {
            String folder = ZFOLDER_YOUTUBE;
            this.isFolder = !TextUtils.isEmpty(folder);

            File mFolderMe = new File(folder);
            if (!mFolderMe.exists()) {
                mFolderMe.mkdirs();
            }

            return this;
        }

        public Builder setFolderYoutube_Analytics(boolean isFolder) {
            String folder = ZFOLDER_YOUTUBE_ANALYTICS;
            this.isFolder = !TextUtils.isEmpty(folder);

            File mFolderMe = new File(folder);
            if (!mFolderMe.exists()) {
                mFolderMe.mkdirs();
            }
            return this;
        }

        public Builder setFolderYoutube_Download(boolean isFolder) {
            String folder = ZFOLDER_YOUTUBE_DOWNLOAD;
            this.isFolder = !TextUtils.isEmpty(folder);

            File mFolderMe = new File(folder);
            if (!mFolderMe.exists()) {
                mFolderMe.mkdirs();
            }
            return this;
        }


        public Builder setFolderEbook(boolean isFolder) {
            String folder = ZFOLDER_EBOOK;
            this.isFolder = !TextUtils.isEmpty(folder);

            File mFolderMe = new File(folder);
            if (!mFolderMe.exists()) {
                mFolderMe.mkdirs();
            }
            return this;
        }

        public Builder setFolderArchive(boolean isFolder) {
            String folder = ZFOLDER_ARCHIVE;
            this.isFolder = !TextUtils.isEmpty(folder);

            File mFolderMe = new File(folder);
            if (!mFolderMe.exists()) {
                mFolderMe.mkdirs();
            }
            return this;
        }


        public Builder setExternalFileDir(String folder) {
            Context c = AppController.getContext();
            File file = new File(c.getExternalFilesDir(folder).getAbsolutePath());
            file.mkdirs();
            return this;
        }

        public FolderMe build() {
            this.isFolder = !TextUtils.isEmpty(mFolder);
            return new FolderMe(this);
        }
    }
}

