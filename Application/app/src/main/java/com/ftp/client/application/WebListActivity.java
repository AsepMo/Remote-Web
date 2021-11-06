package com.ftp.client.application;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import java.util.Arrays;

import com.ftp.client.R;
import com.ftp.client.engine.app.fragments.WebListFragment;
import com.ftp.client.engine.app.utils.Utils;

public class WebListActivity extends AppCompatActivity {
    
    public static String TAG = WebListActivity.class.getSimpleName();
    public static void start(Context c) {
        Intent mIntent = new Intent(c, WebListActivity.class);
        c.startActivity(mIntent);
    }
    
    public static final int SELECT_FILE_CODE = 121;
    /** An intent for launching the system settings. */
    private static final Intent sSettingsIntent =
    new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        boolean isRunning = Utils.isRemoteServiceRunning(this);
        if(isRunning){
            
        } 
        showFragment(new WebListFragment());
    }
    
    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.content_frame, fragment)
            .commit();
    }
    
}
