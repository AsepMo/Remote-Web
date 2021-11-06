package com.ftp.client;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import com.ftp.client.application.AuthActivity;
import com.ftp.client.engine.app.settings.Settings;
import com.ftp.client.application.WebListActivity;

public class SplashActivity extends AppCompatActivity {
    public static String TAG = SplashActivity.class.getSimpleName();

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(1000);
                    // Now change the color back. Needs to be done on the UI thread
                    runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(new Settings(getBaseContext()).getLoginStatus())
                                    WebListActivity.start(SplashActivity.this);
                                else
                                    AuthActivity.start(SplashActivity.this);
                                    
                                SplashActivity.this.finish();
                            }
                        });
                }
            }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    }
}

