package com.ftp.client;

import android.app.Application;
import android.content.Context;

import com.singhajit.sherlock.core.Sherlock;
import com.singhajit.sherlock.core.investigation.AppInfo;
import com.singhajit.sherlock.core.investigation.AppInfoProvider;
import com.singhajit.sherlock.core.SherlockNotInitializedException;
import com.singhajit.sherlock.util.AppInfoUtil;

public class AppController extends Application {

    private static AppController isInstance;
    private static Context mContext;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        isInstance = this;
        mContext = this;
        
        try {

            Sherlock.init(this); //Initializing Sherlock
            Sherlock.setAppInfoProvider(new AppInfoProvider() {
                    @Override
                    public AppInfo getAppInfo() {
                        return new AppInfo.Builder()
                            .with("Version", AppInfoUtil.getAppVersion(getApplicationContext())) //You can get the actual version using "AppInfoUtil.getAppVersion(context)"
                            .with("BuildNumber", "221B")
                            .build();
                    }
                });

        } catch (SherlockNotInitializedException e) {
            e.printStackTrace();
        }
    }
    
    public static synchronized AppController getInstance() {
        return isInstance;
    }

    public static Context getContext() {
        return mContext;
    }
}


