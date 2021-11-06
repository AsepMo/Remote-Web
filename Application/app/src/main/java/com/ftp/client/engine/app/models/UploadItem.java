package com.ftp.client.engine.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class UploadItem implements Parcelable {
    
    private int mId; //id in database
    private String mFileThumbnail; // file thumbnail
    private String mFileName; // file name
    private String mFilePath; //file path 
    private String mFileSize; // file size
    private String mVideoDuration; // video duration
    private String mFileLastModified; // file last modified
    
    public UploadItem()
    {
    }

    public UploadItem(Parcel in) {
        mFileName = in.readString();
        mFilePath = in.readString();
        mFileSize = in.readString();
        mVideoDuration = in.readString();
        mFileLastModified = in.readString();
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }
    
    public String getFileThumbnail() {
        return mFileThumbnail;
    }

    public void setFileThumbnail(String mFileThumbnail) {
        this.mFileThumbnail = mFileThumbnail;
    }
    
    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String mFileName) {
        this.mFileName = mFileName;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String mFilePath) {
        this.mFilePath = mFilePath;
    }

    public String getFileSize() {
        return mFileSize;
    }

    public void setFileSize(String mFileSize) {
        this.mFileSize = mFileSize;
    }

    public String getVideoDuration() {
        return mVideoDuration;
    }

    public void setVideoDuration(String mVideoDuration) {
        this.mVideoDuration = mVideoDuration;
    }
    
    public String getFileLastModified() {
        return mFileLastModified;
    }

    public void setFileLastModified(String mFileLastModified) {
        this.mFileLastModified = mFileLastModified;
    }

    public static final Parcelable.Creator<UploadItem> CREATOR = new Parcelable.Creator<UploadItem>() {
        public UploadItem createFromParcel(Parcel in) {
            return new UploadItem(in);
        }

        public UploadItem[] newArray(int size) {
            return new UploadItem[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mFileName);
        dest.writeString(mFilePath);
        dest.writeString(mFileSize);
        dest.writeString(mVideoDuration);
        dest.writeString(mFileLastModified);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
