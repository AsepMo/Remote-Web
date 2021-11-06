package com.ftp.client.application;

import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.Arrays;

import com.ftp.client.R;
import com.ftp.client.engine.app.config.Constants;
import com.ftp.client.engine.app.preview.IconPreview;
import com.ftp.client.engine.app.adapters.DrawerAdapter;
import com.ftp.client.engine.app.models.DrawerItem;
import com.ftp.client.engine.app.models.SimpleItem;
import com.ftp.client.engine.app.models.SpaceItem;
import com.ftp.client.engine.app.fragments.WebClientFragment;
import com.ftp.client.engine.app.fragments.FTPUploadFragment;
import com.ftp.client.engine.app.fragments.FTPListFragment;
import com.ftp.client.engine.app.fragments.HistoryDownloadFragment;
import com.ftp.client.engine.widget.SlidingRootNav;
import com.ftp.client.engine.widget.SlidingRootNavBuilder;

public class RemoteWebActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {

    public static String TAG = RemoteWebActivity.class.getSimpleName();
    public static void start(Context c) {
        Intent mIntent = new Intent(c, RemoteWebActivity.class);
        c.startActivity(mIntent);
    }
    private static final int POS_WEB_CLIENT = 0;
    private static final int POS_FTP_UPLOAD = 1;
    private static final int POS_FTP_LIST = 2;
    private static final int POS_FTP_DOWNLOAD = 3;
    private static final int POS_BACK_TO_HOME = 5;

    public static final String EXTRA_WEBSITE = "EXTRA_WEBSITE";
    public static final String EXTRA_USER = "EXTRA_USER";
    public static final String EXTRA_HOST = "EXTRA_HOST";
    public static final String EXTRA_PORT = "EXTRA_PORT";
    public static final String EXTRA_PASSWORD = "EXTRA_PASSWORD";
    public static final String EXTRA_SITE_ADDRESS = "EXTRA_SITE_ADDRESS";
    public static final int SELECT_FILE_CODE = 121;

    private String[] screenTitles;
    private Drawable[] screenIcons;

    private DrawerAdapter mDrawerAdapter;
    private SlidingRootNav slidingRootNav;
    private String mWebsite;
    private String mUserName;
    private String mServerIP;
    private String mServerPort;
    private String mServerPassword;
    private String mSiteAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        slidingRootNav = new SlidingRootNavBuilder(this)
            .withToolbarMenuToggle(toolbar)
            .withMenuOpened(false)
            .withContentClickableWhenMenuOpened(false)
            .withSavedState(savedInstanceState)
            .withMenuLayout(R.layout.menu_left_drawer)
            .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        mDrawerAdapter = new DrawerAdapter(Arrays.asList(
                                               createItemFor(POS_WEB_CLIENT).setChecked(true),
                                               createItemFor(POS_FTP_UPLOAD),
                                               createItemFor(POS_FTP_LIST),
                                               createItemFor(POS_FTP_DOWNLOAD),
                                               new SpaceItem(48),
                                               createItemFor(POS_BACK_TO_HOME)));
        mDrawerAdapter.setListener(this);

        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(mDrawerAdapter);
        mDrawerAdapter.setSelected(POS_WEB_CLIENT);
        if (!TextUtils.isEmpty(getIntent().getStringExtra(EXTRA_WEBSITE))) {
            mWebsite = getIntent().getStringExtra(EXTRA_WEBSITE);
        }
        if (!TextUtils.isEmpty(getIntent().getStringExtra(EXTRA_USER))) {
            mUserName = getIntent().getStringExtra(EXTRA_USER);
        }
        if (!TextUtils.isEmpty(getIntent().getStringExtra(EXTRA_HOST))) {
            mServerIP = getIntent().getStringExtra(EXTRA_HOST);
        }
        if (!TextUtils.isEmpty(getIntent().getStringExtra(EXTRA_PORT))) {
            mServerPort = getIntent().getStringExtra(EXTRA_PORT);  
        }
        if (!TextUtils.isEmpty(getIntent().getStringExtra(EXTRA_PASSWORD))) {        
            mServerPassword = getIntent().getStringExtra(EXTRA_PASSWORD);
        }
        if (!TextUtils.isEmpty(getIntent().getStringExtra(EXTRA_SITE_ADDRESS))) {
            mSiteAddress = getIntent().getStringExtra(EXTRA_SITE_ADDRESS);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(mWebsite);
        sb.append("\n");
        sb.append(mUserName);
        sb.append("\n");
        sb.append(mServerIP);
        sb.append("\n");
        sb.append(mServerPort);
        sb.append("\n");
        sb.append(mServerPassword);
        sb.append("\n");
        sb.append(mSiteAddress);
       // Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();

        //toolbar.setSubtitle(mUserName + "@" + mServerIP + ":" + mServerPort);

        // start IconPreview class to get thumbnails if BrowserListAdapter
        // request them
        new IconPreview(this);
    }

    @Override
    public void onItemSelected(int position) {
        if (position == POS_WEB_CLIENT) {
            showFragment(WebClientFragment.createFor(mSiteAddress));
        }
        if (position == POS_FTP_UPLOAD) {
            showFragment(FTPUploadFragment.createFor("Upload"));
        }
        if (position == POS_FTP_LIST) {
            showFragment(FTPListFragment.createFor("File List"));
        }
        if (position == POS_FTP_DOWNLOAD) {
            showFragment(HistoryDownloadFragment.createFor("Folder Download"));
        }
        if (position == POS_BACK_TO_HOME) {
            finish();
        }
        slidingRootNav.closeMenu();
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.content_frame, fragment)
            .commit();
    }

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
            .withIconTint(color(R.color.textColorSecondary))
            .withTextTint(color(R.color.textColorPrimary))
            .withSelectedIconTint(color(R.color.colorAccent))
            .withSelectedTextTint(color(R.color.colorAccent));
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }

}
