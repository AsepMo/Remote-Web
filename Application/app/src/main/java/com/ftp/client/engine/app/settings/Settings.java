package com.ftp.client.engine.app.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.ftp.client.engine.app.config.Constants;

public class Settings {
    Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public Settings(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences("Local_Preference", Activity.MODE_PRIVATE);
    }

    public boolean saveDetals(String name, String email, String password, String age, String site_address, Integer gender, String imgpath)
    {
        editor = preferences.edit();
        editor.putString("name",name);
        editor.putString("email",email);
        editor.putString("password",password);
        editor.putInt("gender",gender);
        editor.putString("age", age);
        editor.putString("site_address", site_address);
        editor.putString("imgpath",imgpath);

        return editor.commit();
    }

    public String getName()
    {
        return preferences.getString("name","0");
    }
    
    public String getEmail()
    {
        return preferences.getString("email","0");
    }
    
    public String getPassword()
    {
        return preferences.getString("password","0");
    }
    
    public String getAge()
    {
        return preferences.getString("age","0");
    }
    
    public int getGender()
    {
        return preferences.getInt("gender", 0);
    }
    
    public String getImgPath()
    {
        return preferences.getString("imgpath","0");
    }

    public String getSiteAddress()
    {
        return preferences.getString("site_address","0");
    }
    
    
    public boolean setLoginStatus(boolean status)
    {
        editor = preferences.edit();
        editor.putBoolean("status",status);
        return editor.commit();
    }

    public boolean getLoginStatus()
    {
        return preferences.getBoolean("status",false);
    }

    public boolean setChannelId(String id){
        editor = preferences.edit();
        editor.putString("channelID",id);
        return editor.commit();
    }

    public String getChannelID()
    {
        return preferences.getString("channelID",Constants.CHANNEL_ID);
    }
    
    public boolean showThumbnail() {
        return preferences.getBoolean("showpreview", true);
    }
    
    public boolean setShowThumbnail(boolean status)
    {
        editor = preferences.edit();
        editor.putBoolean("showpreview",status);
        return editor.commit();
    }
}

