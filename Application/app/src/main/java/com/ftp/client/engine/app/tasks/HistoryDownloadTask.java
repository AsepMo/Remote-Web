package com.ftp.client.engine.app.tasks;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.io.File;

import com.ftp.client.R;
import com.ftp.client.engine.app.models.UploadItem;
import com.ftp.client.engine.app.preview.MimeTypes;
import com.ftp.client.engine.app.utils.Utils;

public class HistoryDownloadTask extends AsyncTask<File, Void, ArrayList<UploadItem>> {

   
    public static String TAG = HistoryDownloadTask.class.getSimpleName();
    private Context mContext;
    private String[] extensions;
    private ArrayList<UploadItem> fileList;
    private OnHistoryTaskListener mOnTaskListener;
    public HistoryDownloadTask(Context context, String[] extensions) {
        this.mContext = context; 
        this.extensions = extensions;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute(); 
        if(mOnTaskListener != null){
            mOnTaskListener.onPreExecute();
        }
    }

    @Override
    protected ArrayList<UploadItem> doInBackground(File...params) {
        fileList = new ArrayList<UploadItem>();
        File MP4 = new File(params[0].getAbsolutePath());
        if (MP4.exists())
        {
            listOfFile(MP4);
        }
        return fileList;
    }
   
    @Override
    protected void onPostExecute(ArrayList<UploadItem> result) {
        super.onPostExecute(result);
        if (result.size() < 1) {
            if(mOnTaskListener != null){
                mOnTaskListener.isEmpty();
            }
        } else {
            if(mOnTaskListener != null){
                mOnTaskListener.onSuccess(result);
            }
        }
    }
    
    private void listOfFile(File dir)
    {
        File[] list = dir.listFiles();

        for (File file : list)
        {
            if (file.isDirectory())
            {
                if (!new File(file, ".nomedia").exists() && !file.getName().startsWith("."))
                {
                    Log.w("LOG", "IS DIR " + file);
                    listOfFile(file);
                }
            }
            else
            {
                String path = file.getAbsolutePath();
                //String[] extensions = new String[]{".mp4"};
                for (String ext : extensions)
                {
                    if (path.endsWith(ext))
                    {
                        UploadItem item = new UploadItem();     
                        String fileName = path;
                        String[] split = path.split("/");
                        fileName = split[split.length - 1];                        
                        item.setFileName(fileName);
                        item.setFilePath(path);
                        item.setFileThumbnail(path);
                        item.setFilePath(path);
                        item.setFileSize(FileUtils.byteCountToDisplaySize(file.length()));
                        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy  hh:mm a");
                        String date = format.format(file.lastModified());
                        final String type = FilenameUtils.getExtension(path); 
                        if (Arrays.asList(MimeTypes.MIME_VIDEO).contains(type)) {  
                            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                            retriever.setDataSource(file.getAbsolutePath());
                            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                            item.setVideoDuration(Utils.timeConversion(Long.parseLong(duration)));                     
                        } else {
                            item.setVideoDuration("0");                   
                        }
                        item.setFileLastModified(date);
                        fileList.add(item);
                        //Log.i("LOG", "ADD " + videoInfo.videoTitle + " " + videoInfo.videoThumb);
                    }
                }
            }
        }
        Log.d("LOG", fileList.size() + " DONE");
    }
    
    public void setOnHistoryTaskListener(OnHistoryTaskListener mOnTaskListener){
        this.mOnTaskListener = mOnTaskListener;
    }

    public interface OnHistoryTaskListener{
        void onPreExecute();
        void onSuccess(ArrayList<UploadItem> result);
        void onFailed();
        void isEmpty();
    }
}
