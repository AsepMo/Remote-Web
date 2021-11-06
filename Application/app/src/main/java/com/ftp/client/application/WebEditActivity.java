package com.ftp.client.application;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Arrays;

import com.ftp.client.R;
import android.view.Menu;
import android.view.MenuItem;
import com.ftp.client.engine.app.fragments.AddWebsiteFragment;

public class WebEditActivity extends AppCompatActivity {

    public static String TAG = WebEditActivity.class.getSimpleName();
    public static void start(Context c) {
        Intent mIntent = new Intent(c, WebEditActivity.class);
        c.startActivity(mIntent);
    }
    
    public static final String EXTRA_WEBSITE = "EXTRA_WEBSITE";
    public static final String EXTRA_SERVER_USERNAME = "EXTRA_SERVER_USERNAME";
    public static final String EXTRA_SERVER_IP = "EXTRA_SERVER_IP";
    public static final String EXTRA_SERVER_PORT = "EXTRA_SERVER_PORT";
    public static final String EXTRA_SERVER_PASSWORD = "EXTRA_SERVER_PASSWORD";
    public static final String EXTRA_WEB_ADDRESS = "EXTRA_WEB_ADDRESS";
    
    private String website;
    private String vUser;
    private String vHost;
    private int vPort;
    private String vPass;
    private String siteAddress;
    private AddWebsiteFragment mAddWebsite;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAddWebsite = new AddWebsiteFragment();
        showFragment(mAddWebsite);
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.content_frame, fragment)
            .commit();
    }
    
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Add Website")
        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item){
                website = mAddWebsite.getWebsite();
                vUser = mAddWebsite.getUserName();
                vHost = mAddWebsite.getServerIP();
                vPort = Integer.valueOf(mAddWebsite.getServerPort());
                vPass = mAddWebsite.getServerPassword();
                siteAddress = mAddWebsite.getSiteAddress();
                returnData(website, vUser, vHost, vPort, vPass, siteAddress);
                return true;
            }
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }*/
 
    public void returnData(String webSite, String user, String ip, int port, String password, String address){
        Intent mIntent = new Intent();
        mIntent.putExtra(EXTRA_WEBSITE, webSite);
        mIntent.putExtra(EXTRA_SERVER_USERNAME, user);
        mIntent.putExtra(EXTRA_SERVER_IP, ip);
        mIntent.putExtra(EXTRA_SERVER_PORT, port);
        mIntent.putExtra(EXTRA_SERVER_PASSWORD, password);
        mIntent.putExtra(EXTRA_WEB_ADDRESS, address);
        setResult(RESULT_OK, mIntent);
        finish();
    }
}
