package com.ftp.client.engine.app.fragments;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.ftp.client.R;
import com.ftp.client.application.WebEditActivity;
import com.ftp.client.application.AuthActivity;
import com.ftp.client.application.ProfileActivity;
import com.ftp.client.engine.app.db.DBHelper;
import com.ftp.client.engine.app.adapters.WebsiteAdapter;
import com.ftp.client.engine.app.models.WebsiteItem;
import com.ftp.client.engine.app.settings.Settings;
import com.ftp.client.engine.widget.EmptyRecyclerView;

public class WebListFragment extends Fragment {

    public static String TAG = WebListFragment.class.getSimpleName();
    public static final int SELECT_FILE_CODE = 121;

    public static WebClientFragment getWebList() {
        WebClientFragment fragment = new WebClientFragment();
        return fragment;
    }

    private Context mContext;
    private EmptyRecyclerView mEmptyRecyclerView;
    private WebsiteAdapter mWebsiteAdapter;
    private LinearLayout mEmptyLayout;
    private DBHelper mDatabase;
    private Button mBtnCreate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mContext = getActivity();
        mDatabase = new DBHelper(mContext);

        mEmptyRecyclerView = (EmptyRecyclerView)view.findViewById(R.id.weblist);
        mEmptyLayout = (LinearLayout)view.findViewById(R.id.empty_layout);  
        mEmptyRecyclerView.setEmptyView(mEmptyLayout);
        mEmptyRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        //newest to oldest order (database stores from oldest to newest)
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);

        mEmptyRecyclerView.setLayoutManager(llm);
        mEmptyRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mWebsiteAdapter = new WebsiteAdapter(getActivity(), llm);
        mEmptyRecyclerView.setAdapter(mWebsiteAdapter);
        mBtnCreate = (Button)view.findViewById(R.id.btn_create_web);
        mBtnCreate.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    addWebsite();
                }
            });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add("Add")
            .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    addWebsite();                                                           
                    return true;
                }
            }).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add("Profile")
            .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    startActivity(new Intent(getActivity(), ProfileActivity.class));

                    return true;
                }
            }).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

    }

    public void addWebsite() {
        Intent mIntent = new Intent(mContext, WebEditActivity.class);
        mIntent.putExtra(WebEditActivity.EXTRA_WEBSITE, "");
        mIntent.putExtra(WebEditActivity.EXTRA_SERVER_USERNAME, "");
        mIntent.putExtra(WebEditActivity.EXTRA_SERVER_IP, "");
        mIntent.putExtra(WebEditActivity.EXTRA_SERVER_PORT, "");
        mIntent.putExtra(WebEditActivity.EXTRA_SERVER_PASSWORD, "");
        mIntent.putExtra(WebEditActivity.EXTRA_WEB_ADDRESS, "");              
        startActivityForResult(mIntent, SELECT_FILE_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == SELECT_FILE_CODE) {
            String website = data.getStringExtra(WebEditActivity.EXTRA_WEBSITE);
            String user = data.getStringExtra(WebEditActivity.EXTRA_SERVER_USERNAME);
            String host = data.getStringExtra(WebEditActivity.EXTRA_SERVER_IP);
            String port = data.getStringExtra(WebEditActivity.EXTRA_SERVER_PORT);
            String password = data.getStringExtra(WebEditActivity.EXTRA_SERVER_PASSWORD);
            String webAddress = data.getStringExtra(WebEditActivity.EXTRA_WEB_ADDRESS);
            try {
                mDatabase.addRecording(website, user, host, port, password, webAddress);
            } catch (Exception e) {
                Log.e(TAG, "exception", e);
            }

        }
    }

}
