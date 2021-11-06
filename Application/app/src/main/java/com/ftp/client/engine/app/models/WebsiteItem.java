package com.ftp.client.engine.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class WebsiteItem implements Parcelable {
    
    private String mWebName; // file name
    private String mUserName; //file path
    private int mId; //id in database
    private String mServerIP; // Server IP
    private String mServerPort; // Server Port
    private String mServerPassword;
    private String mSiteAddress; // Situs Address

    public WebsiteItem()
    {
    }

    public WebsiteItem(Parcel in) {
        mWebName = in.readString();
        mUserName = in.readString();
        mServerIP = in.readString();
        mServerPort = in.readString();
        mServerPassword = in.readString();
        mSiteAddress = in.readString();
    }

    public String getWebsite() {
        return mWebName;
    }

    public void setWebsite(String mWebName) {
        this.mWebName = mWebName;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String mUser) {
        this.mUserName = mUser;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getServerIP() {
        return mServerIP;
    }

    public void setServerIP(String mHost) {
        this.mServerIP = mHost;
    }

    public String getServerPort() {
        return mServerPort;
    }

    public void setServerPort(String port) {
        mServerPort = port;
    }
    
    public String getServerPassword() {
        return mServerPassword;
    }

    public void setServerPassword(String password) {
        mServerPassword = password;
    }
    
    public String getSitus() {
        return mSiteAddress;
    }

    public void setSitus(String mSiteAddress) {
        this.mSiteAddress = mSiteAddress;
    }

    public static final Parcelable.Creator<WebsiteItem> CREATOR = new Parcelable.Creator<WebsiteItem>() {
        public WebsiteItem createFromParcel(Parcel in) {
            return new WebsiteItem(in);
        }

        public WebsiteItem[] newArray(int size) {
            return new WebsiteItem[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mWebName);
        dest.writeString(mUserName);
        dest.writeString(mServerIP);
        dest.writeString(mServerPort);
        dest.writeString(mServerPassword);
        dest.writeString(mSiteAddress);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
