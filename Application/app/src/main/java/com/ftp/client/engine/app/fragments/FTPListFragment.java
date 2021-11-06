package com.ftp.client.engine.app.fragments;

import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import com.ftp.client.R;

public class FTPListFragment extends Fragment {
    
    public static String TAG = FTPListFragment.class.getSimpleName();
    private static final String ARG_EXTRA_URL = "ARG_EXTRA_URL";

    public static FTPListFragment createFor(String text) {
        FTPListFragment fragment = new FTPListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EXTRA_URL, text);
        fragment.setArguments(args);
        return fragment;
    }
    
    private Activity mActivity;
    private Context mContext;
    private String mSearchText;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSearchText = getArguments().getString(ARG_EXTRA_URL);

        setHasOptionsMenu(true);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web_client, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mContext = getActivity();
        mActivity = (Activity)getActivity();
    }
    
}
