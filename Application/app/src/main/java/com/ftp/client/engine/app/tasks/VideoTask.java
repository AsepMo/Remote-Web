package com.ftp.client.engine.app.tasks;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;

import com.ftp.client.R;
import com.ftp.client.engine.app.models.VideoData;

public class VideoTask extends AsyncTask<Void, Void, ArrayList<VideoData>> {

    private Context mContext;
    private VideoData mVideoData;
    private int mCount = 0; 
    private OnVideoTaskListener mOnVideoTaskListener;
    public VideoTask(Context context) {
        this.mContext = context; 
        mVideoData = new VideoData(context, VideoData.FILENAME);  
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute(); 
        if(mOnVideoTaskListener != null){
            mOnVideoTaskListener.onPreExecute();
        }
    }

    @Override
    protected void onProgressUpdate(Void[] values) {
        super.onProgressUpdate(values);

    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

    }

    @Override
    protected void onCancelled(ArrayList<VideoData> result) {
        super.onCancelled(result);
    }


    @Override
    protected ArrayList<VideoData> doInBackground(Void[] params) {
        ArrayList<VideoData> videoList = new ArrayList<VideoData>();
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        //looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                mCount++;
                String id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));                
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                String thumbnail = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                //String size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy  hh:mm a");
                File file = new File(data);

                final String date = format.format(file.lastModified());
                Integer number = (mCount);

                VideoData videoData  = new VideoData(mContext);
                videoData.setId(number);
                videoData.setVideoId(id);
                videoData.setVideoTitle(title);
                videoData.setVideoUri(Uri.parse(data));
                videoData.setVideoPath(data);
                videoData.setVideoThumbnail(thumbnail);
                videoData.setVideoSize(FileUtils.byteCountToDisplaySize(file.length()));
                videoData.setVideoDuration(timeConversion(Long.parseLong(duration)));                                   
                videoData.setVideoDate(date);
                videoList.add(videoData);

                try {
                    videoData.saveToFile(videoList);

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    if(mOnVideoTaskListener != null){
                        mOnVideoTaskListener.onFailed();
                    }
                }
            } while (cursor.moveToNext());
        }
        return videoList;
    }

    @Override
    protected void onPostExecute(ArrayList<VideoData> result) {
        super.onPostExecute(result);
        if (result.size() < 1) {
            if(mOnVideoTaskListener != null){
                mOnVideoTaskListener.isEmpty();
            }
        } else {
            VideoData video = result.get(0);
            video.initialise(video);
            if(mOnVideoTaskListener != null){
                mOnVideoTaskListener.onSuccess(result);
            }
        }
    }

    //time conversion
    public static String timeConversion(long value) {
        String songTime;
        int dur = (int) value;
        int hrs = (dur / 3600000);
        int mns = (dur / 60000) % 60000;
        int scs = dur % 60000 / 1000;

        if (hrs > 0) {
            songTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            songTime = String.format("%02d:%02d", mns, scs);
        }
        return songTime;
    }

    public void setOnVideoTaskListener(OnVideoTaskListener mOnVideoTaskListener){
        this.mOnVideoTaskListener = mOnVideoTaskListener;
    }

    public interface OnVideoTaskListener{
        void onPreExecute();
        void onSuccess(ArrayList<VideoData> result);
        void onFailed();
        void isEmpty();
    }
}



