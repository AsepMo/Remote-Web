package com.ftp.client.engine.app.fragments;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.annotation.Nullable;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.Toast;

import java.util.Arrays;
import java.util.ArrayList;
import java.io.File;


import com.ftp.client.R;
import com.ftp.client.engine.app.adapters.UploadAdapter;
import com.ftp.client.engine.app.models.UploadItem;
import com.ftp.client.engine.widget.EmptyRecyclerView;
import com.ftp.client.engine.app.tasks.HistoryDownloadTask;
import com.ftp.client.engine.app.folders.FolderMe;
import android.os.AsyncTask;

public class HistoryDownloadFragment extends Fragment {

    public static String TAG = HistoryDownloadFragment.class.getSimpleName();
    private static final String ARG_EXTRA_URL = "ARG_EXTRA_URL";

    public static HistoryDownloadFragment createFor(String text) {
        HistoryDownloadFragment fragment = new HistoryDownloadFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EXTRA_URL, text);
        fragment.setArguments(args);
        return fragment;
    }

    private Activity mActivity;
    private Context mContext;
    private String mSearchText;

    private EmptyRecyclerView mEmptyRecyclerView;
    private UploadAdapter mUploadAdapter;
    private RelativeLayout mRelativeLayout;
    private LinearLayout mLoadingLayout;
    private LinearLayout mEmptyLayout;
    private SwipeRefreshLayout refreshLayout;
    Integer shortAnimDuration;
    private ArrayList<UploadItem> uploadList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSearchText = getArguments().getString(ARG_EXTRA_URL);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_download, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mContext = getActivity();
        mActivity = (Activity)getActivity();
        shortAnimDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mRelativeLayout = (RelativeLayout) view.findViewById(R.id.container);
        mLoadingLayout = (LinearLayout) view.findViewById(R.id.loading_view);
        mEmptyLayout = (LinearLayout)view.findViewById(R.id.empty_layout);

        mEmptyRecyclerView = (EmptyRecyclerView)view.findViewById(R.id.history_item);  
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        //newest to oldest order (database stores from oldest to newest)
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);

        mEmptyRecyclerView.setLayoutManager(llm);
        uploadList = new ArrayList<UploadItem>();
        
        rerunHistoryLoader();
    }

    private void rerunHistoryLoader() {
        HistoryDownloadTask historyLoaderTwo = new HistoryDownloadTask(getActivity(), new String[]{"mp3", ".mp4", ".apk"});
        historyLoaderTwo.setOnHistoryTaskListener(new HistoryDownloadTask.OnHistoryTaskListener(){
                @Override
                public void onPreExecute() {
                    crossFade(mEmptyRecyclerView, mLoadingLayout);
                    mEmptyLayout.setVisibility(View.GONE);
                    
                }
                @Override
                public void onSuccess(ArrayList<UploadItem> result) {
                    crossFade(mLoadingLayout, mEmptyRecyclerView);
                    mEmptyLayout.setVisibility(View.GONE);
                   // mUploadAdapter = new UploadAdapter(mContext, mRelativeLayout, result);
                  //  mEmptyRecyclerView.setAdapter(mUploadAdapter);
                    
                }

                @Override
                public void onFailed() {
                    crossFade(mLoadingLayout, mEmptyLayout);
                    mEmptyRecyclerView.setVisibility(View.GONE);

                } 

                @Override
                public void isEmpty() {                 
                    crossFade(mLoadingLayout, mEmptyLayout);     
                    mEmptyRecyclerView.setVisibility(View.GONE);  

                } 
            });

        historyLoaderTwo.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new File(FolderMe.EXTERNAL_DIR));
    }

    private void crossFade(final View toHide, View toShow) {

        toShow.setAlpha(0f);
        toShow.setVisibility(View.VISIBLE);

        toShow.animate()
            .alpha(1f)
            .setDuration(shortAnimDuration)
            .setListener(null);

        toHide.animate()
            .alpha(0f)
            .setDuration(shortAnimDuration)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    toHide.setVisibility(View.GONE);
                }
            });
    }

}
