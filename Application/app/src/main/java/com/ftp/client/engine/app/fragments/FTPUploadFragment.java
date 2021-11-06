package com.ftp.client.engine.app.fragments;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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
import java.net.URISyntaxException;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import com.ftp.client.R;
import com.ftp.client.engine.app.adapters.UploadAdapter;
import com.ftp.client.engine.app.models.UploadItem;
import com.ftp.client.engine.app.listeners.OnRecyclerViewListener;
import com.ftp.client.engine.app.listeners.ItemTouchHelperClass;
import com.ftp.client.engine.app.preview.MimeTypes;
import com.ftp.client.engine.app.settings.Settings;
import com.ftp.client.engine.app.utils.Utils;
import com.ftp.client.engine.widget.EmptyRecyclerView;
import com.ftp.client.engine.widget.FancyButton;
import com.ftp.client.application.RemoteWebActivity;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;

public class FTPUploadFragment extends Fragment {

    public static String TAG = FTPUploadFragment.class.getSimpleName();
    private static final String ARG_EXTRA_URL = "ARG_EXTRA_URL";
    
    private static final int READ_REQUEST_CODE = 1337;

    public static FTPUploadFragment createFor(String text) {
        FTPUploadFragment fragment = new FTPUploadFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EXTRA_URL, text);
        fragment.setArguments(args);
        return fragment;
    }

    private Activity mActivity;
    private Context mContext;
    private String mSearchText;

    private RelativeLayout mRelativeLayout;
    private LinearLayout mButtonLayout;
    private EmptyRecyclerView mEmptyRecyclerView;
    private UploadAdapter mUploadAdapter;
    private LinearLayout mEmptyLayout;
    private FancyButton mBtnAddFiles;
    private FancyButton mBtnUpload;
    private ArrayList<UploadItem> uploadList;
    public ItemTouchHelper itemTouchHelper;
    private OnRecyclerViewListener mOnRecyclerViewListener;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSearchText = getArguments().getString(ARG_EXTRA_URL);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ftp_upload, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mContext = getActivity();
        mActivity = (Activity)getActivity();
        mRelativeLayout = (RelativeLayout) view.findViewById(R.id.root_view);
        mButtonLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
        
        mEmptyRecyclerView = (EmptyRecyclerView)view.findViewById(R.id.file_list_upload);
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
        mOnRecyclerViewListener = new OnRecyclerViewListener() {
            @Override
            public void show() {

                mButtonLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }

            @Override
            public void hide() {

                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mButtonLayout.getLayoutParams();
                int fabMargin = lp.bottomMargin;
                mButtonLayout.animate().translationY(mButtonLayout.getHeight() + fabMargin).setInterpolator(new AccelerateInterpolator(2.0f)).start();
            }
        };
        mEmptyRecyclerView.addOnScrollListener(mOnRecyclerViewListener);


        
        
        uploadList = new ArrayList<UploadItem>();
        mUploadAdapter = new UploadAdapter(getActivity(), mRelativeLayout, uploadList);
        
        ItemTouchHelper.Callback callback = new ItemTouchHelperClass(mUploadAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mEmptyRecyclerView);
        mEmptyRecyclerView.setAdapter(mUploadAdapter);
        
        mBtnAddFiles = (FancyButton)view.findViewById(R.id.btn_add);
        mBtnAddFiles.setIconResource(R.drawable.ic_plus_circle);
        mBtnAddFiles.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    getFilePicker(mContext);
                }
            });
        mBtnUpload = (FancyButton)view.findViewById(R.id.btn_upload);
        mBtnUpload.setIconResource(R.drawable.ic_ftp_upload);
        mBtnUpload.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    
                }
            });     
    }

    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void getFilePicker(Context c) {

        //Create a DialogProperties object.
        final DialogProperties properties = new DialogProperties();

        //Instantiate FilePickerDialog with Context and DialogProperties.
        FilePickerDialog dialog = new FilePickerDialog(c, properties);
        dialog.setTitle("Select a File");
        dialog.setPositiveBtnName("Select");
        dialog.setNegativeBtnName("Cancel");
        //Method handle selected files.
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
                @Override
                public void onSelectedFilePaths(String[] files) {
                    //files is the array of paths selected by the App User.
                    //listItem.clear();
                    for (String path:files) {
                        File file = new File(path);
                        if(uploadList.size()<=20) {
                            mUploadAdapter.addFiles(uploadList, file.getAbsolutePath());
                        }
                        else
                            Toast.makeText(getActivity(), "Max limit is 20", Toast.LENGTH_LONG).show();
                    }                                
                }
            });
        properties.selection_mode = DialogConfigs.MULTI_MODE;
        //Setting selection type to files.
        properties.selection_type = DialogConfigs.FILE_SELECT;
        //Setting Parent Directory to Default SDCARD.
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File("/mnt");
        //Set new properties of dialog.
        dialog.setProperties(properties);

        //Showing dialog when Show Dialog button is clicked.
        dialog.show();
    }
    
}
