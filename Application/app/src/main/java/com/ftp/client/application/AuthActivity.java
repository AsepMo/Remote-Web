package com.ftp.client.application;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ftp.client.R;
import com.ftp.client.engine.app.fragments.Login;
import com.ftp.client.engine.app.fragments.Register;
import com.ftp.client.engine.widget.SmartTabLayout;
import com.ftp.client.engine.widget.v4.FragmentPagerItemAdapter;
import com.ftp.client.engine.widget.v4.FragmentPagerItems;

public class AuthActivity extends AppCompatActivity {
    public static void start(Context c) {
        Intent mIntent = new Intent(c, AuthActivity.class);
        c.startActivity(mIntent);
    }
    private SmartTabLayout viewPagerTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Login", Login.class)
                .add("Register", Register.class)
                .create());
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(adapter);


        viewPagerTab = (SmartTabLayout) findViewById(R.id.viewPageTab);
        viewPagerTab.setViewPager(viewPager);
    }
}
