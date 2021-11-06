package com.ftp.client.engine.app.models;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.net.HttpURLConnection;
import java.net.URL;

import com.ftp.client.engine.app.folders.FolderMe;

public class VideoData implements Serializable {

    public static String TAG = VideoData.class.getSimpleName();
    public static String FOLDER = FolderMe.ZFOLDER_VIDEO;
    public static String FILENAME = "video_data.json";

    private static Context mContext;
    private static String mFileName;

    public int mId;
    public String mVideoTitle;
    public String mVideoPath;
    public String mVideoThumbnail;
    public Drawable mThumbnail;
    public String mVideoSize;
    public String mVideoDuration;  
    public String mVideoDate;   
    public String mVideoId;
    public boolean isSelected;
    public Uri mVideoUri; 

    //add description
    public static final String ID = "id";
    public static final String TITLE = "video_title";
    public static final String PATH = "video_path";
    public static final String THUMBNAILS = "video_thumbnail";
    public static final String SIZE = "video_size";
    public static final String DURATION = "video_duration";
    public static final String DATE = "video_update";
    public static final String VIDEOID = "video_id";

    public VideoData(Context activity)
    {
        this.mContext = activity;
    }

    public VideoData(Context context, String filename)
    {
        mContext = context;
        mFileName = filename;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public int getId() {
        return mId;
    }

    public void setVideoId(String mVideoId) {
        this.mVideoId = mVideoId;
    }

    public String getVideoId() {
        return mVideoId;
    }

    public void setVideoTitle(String mVideoTitle) {
        this.mVideoTitle = mVideoTitle;
    }

    public String getVideoTitle() {
        return mVideoTitle;
    }

    public void setVideoThumbnail(String mVideoThumbnail) {
        this.mVideoThumbnail = mVideoThumbnail;
    }

    public void setThumbnail(Drawable mVideoThumbnail) {
        this.mThumbnail = mVideoThumbnail;
    }

    public Drawable getApkThumbnail(){
        return mThumbnail;
    }

    public String getVideoThumbnail() {
        return mVideoThumbnail;
    }

    public Uri getVideoUri() {
        return mVideoUri;
    }

    public void setVideoUri(Uri mVideoUri) {
        this.mVideoUri = mVideoUri;
    }

    public void setVideoPath(String mVideoPath) {
        this.mVideoPath = mVideoPath;
    }

    public String getVideoPath() {
        return mVideoPath;
    }

    public void setVideoSize(String mVideoSize) {
        this.mVideoSize = mVideoSize;
    }

    public String getVideoSize() {
        return mVideoSize;
    }

    public void setVideoDuration(String mVideoDuration) {
        this.mVideoDuration = mVideoDuration;
    }

    public String getVideoDuration() {
        return mVideoDuration;
    }

    public void setVideoDate(String mVideoDate) {
        this.mVideoDate = mVideoDate;
    }

    public String getVideoDate() {
        return mVideoDate;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void initialise(VideoData video)
    {
        try
        {
            JSONObject json = new JSONObject();
            json.put("id", video.getId());
            json.put("video_id", video.getVideoId());
            json.put("video_title", video.getVideoTitle());
            json.put("video_path", video.getVideoPath());
            json.put("video_thumbnail", video.getVideoThumbnail());    
            json.put("video_size", video.getVideoSize());
            json.put("video_duration", video.getVideoDuration());
            json.put("video_date", video.getVideoDate());

            String filePath = FOLDER + "/video_data_initialise.json";
            File file = new File(filePath);
            //file.getParentFile().mkdirs();
            FileUtils.writeStringToFile(file, json.toString());
        }
        catch (IOException | JSONException e)
        {
            e.printStackTrace();
        }
    }

    public VideoData(JSONObject jsonObject) throws JSONException
    {
        mId = jsonObject.getInt(ID);
        mVideoId = jsonObject.getString(VIDEOID);
        mVideoTitle = jsonObject.getString(TITLE);
        mVideoPath = jsonObject.getString(PATH);
        mVideoThumbnail = jsonObject.getString(THUMBNAILS);
        mVideoSize = jsonObject.getString(SIZE);
        mVideoDuration = jsonObject.getString(DURATION);
        mVideoDate = jsonObject.getString(DATE);
    }

    public JSONObject toJSON() throws JSONException
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(ID, mId);
        jsonObject.put(TITLE, mVideoTitle);
        jsonObject.put(PATH, mVideoPath);      
        jsonObject.put(THUMBNAILS, mVideoThumbnail);
        jsonObject.put(SIZE, mVideoSize);
        jsonObject.put(DURATION, mVideoDuration);
        jsonObject.put(DATE, mVideoDate);
        jsonObject.put(VIDEOID, mVideoId);
        return jsonObject;
    }


    public static JSONArray toJSONArray(ArrayList<VideoData> items) throws JSONException
    {
        JSONArray jsonArray = new JSONArray();
        for (VideoData item : items)
        {
            JSONObject jsonObject = item.toJSON();
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    public void saveToFile(ArrayList<VideoData> items) throws JSONException, IOException
    {

        File file = new File(FOLDER, mFileName);
        file.getParentFile().mkdirs();
        try
        {
            //FileOutputStream fos = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(toJSONArray(items).toString());
            osw.write('\n');
            osw.flush();
            fos.flush();
            fos.getFD().sync();
            fos.close();

            Log.d(TAG, toJSONArray(items).toString());
        }
        catch (IOException e)
        {
            Log.e(TAG, "Exception writing to file", e);
        }
    }

    public void saveToFile(String file, ArrayList<VideoData> items) throws JSONException, IOException
    {

        try
        {
            FileOutputStream fos = mContext.openFileOutput(file, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(toJSONArray(items).toString());
            osw.write('\n');
            osw.flush();
            fos.flush();
            fos.getFD().sync();
            fos.close();

            Log.d(TAG, toJSONArray(items).toString());
        }
        catch (IOException e)
        {
            Log.e(TAG, "Exception writing to file", e);
        }
    }

    public ArrayList<VideoData> loadFromFile() throws IOException, JSONException
    {
        ArrayList<VideoData> items = new ArrayList<VideoData>();
        BufferedReader bufferedReader = null;
        FileInputStream fileInputStream = null;
        File file = new File(FOLDER, mFileName);
        try
        {
            fileInputStream = new FileInputStream(file);
            StringBuilder builder = new StringBuilder();
            String line;
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            while ((line = bufferedReader.readLine()) != null)
            {
                builder.append(line);
            }

            JSONArray jsonArray = (JSONArray) new JSONTokener(builder.toString()).nextValue();
            for (int i = 0; i < jsonArray.length(); i++)
            {
                VideoData item = new VideoData(jsonArray.getJSONObject(i));
                items.add(item);
            }


        }
        catch (FileNotFoundException fnfe)
        {
            //do nothing about it
            //file won't exist first time app is run
        }
        finally
        {
            if (bufferedReader != null)
            {
                bufferedReader.close();
            }
            if (fileInputStream != null)
            {
                fileInputStream.close();
            }

        }
        return items;
    }

    public static ArrayList<VideoData> loadFromFile(String file) throws IOException, JSONException
    {
        ArrayList<VideoData> items = new ArrayList<VideoData>();
        BufferedReader bufferedReader = null;
        FileInputStream fileInputStream = null;
        File files = new File(file);
        try
        {
            fileInputStream = new FileInputStream(files);
            StringBuilder builder = new StringBuilder();
            String line;
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            while ((line = bufferedReader.readLine()) != null)
            {
                builder.append(line);
            }

            JSONArray jsonArray = (JSONArray) new JSONTokener(builder.toString()).nextValue();
            for (int i = 0; i < jsonArray.length(); i++)
            {
                VideoData item = new VideoData(jsonArray.getJSONObject(i));
                items.add(item);
            }


        }
        catch (FileNotFoundException fnfe)
        {
            //do nothing about it
            //file won't exist first time app is run
        }
        finally
        {
            if (bufferedReader != null)
            {
                bufferedReader.close();
            }
            if (fileInputStream != null)
            {
                fileInputStream.close();
            }

        }
        return items;
    }

    public static void saveData(Context context, int id, String title,  String path, String thumbnail, String size, String duration, String date) 
    {
        VideoData storeData = new VideoData(context, "video_data_.json");

        ArrayList<VideoData> items = new ArrayList<VideoData>();
        VideoData displaylist = new VideoData(context);
        displaylist.setId(id);
        displaylist.setVideoTitle(title);
        displaylist.setVideoPath(path);
        displaylist.setVideoThumbnail(thumbnail);
        displaylist.setVideoSize(size);
        displaylist.setVideoDate(date);
        items.add(displaylist);
        try
        {
            storeData.saveToFile(items);
        }
        catch (JSONException | IOException e)
        {
            e.printStackTrace();
        }
    }

    public static ArrayList<VideoData> getLocallyStoredData(Context c)
    {
        ArrayList<VideoData> items = null;
        VideoData storeData = new VideoData(c, FILENAME);

        try
        {
            items = storeData.loadFromFile();

        }
        catch (IOException | JSONException e)
        {
            e.printStackTrace();
        }

        if (items == null)
        {
            items = new ArrayList<>();
        }
        return items;
    }

    public static String getVideoPosition()
    {
        try
        {
            File infoFile = new File(FOLDER + "/video_data.json");
            JSONObject json = new JSONObject(FileUtils.readFileToString(infoFile));
            return json.getString("id");
        }
        catch (IOException | JSONException e)
        {
            return null;
        }    
    }

    public static String getVideoID()
    {
        try
        {
            File infoFile = new File(FOLDER + "/video_data_initialise.json");
            JSONObject json = new JSONObject(FileUtils.readFileToString(infoFile));
            return json.getString("video_id");
        }
        catch (IOException | JSONException e)
        {
            return null;
        }    
    }

    public static String getTitle()
    {
        try
        {
            File infoFile = new File(FOLDER + "/video_data_initialise.json");
            JSONObject json = new JSONObject(FileUtils.readFileToString(infoFile));
            return json.getString("video_title");
        }
        catch (IOException | JSONException e)
        {
            return null;
        }    
    }

    public static String getPath()
    {
        try
        {
            File infoFile = new File(FOLDER + "/video_data_initialise.json");
            JSONObject json = new JSONObject(FileUtils.readFileToString(infoFile));
            return json.getString("video_path");
        }
        catch (IOException | JSONException e)
        {
            return null;
        }    
    }

    public static String getThumbnail()
    {
        try
        {
            File infoFile = new File(FOLDER + "/video_data_initialise.json");
            JSONObject json = new JSONObject(FileUtils.readFileToString(infoFile));
            return json.getString("video_thumbnail");
        }
        catch (IOException | JSONException e)
        {
            return null;
        }    
    }

    public static String getSize()
    {
        try
        {
            File infoFile = new File(FOLDER + "/video_data_initialise.json");
            JSONObject json = new JSONObject(FileUtils.readFileToString(infoFile));
            return json.getString("video_size");
        }
        catch (IOException | JSONException e)
        {
            return null;
        }    
    }

    public static String getDuration()
    {
        try
        {
            File infoFile = new File(FOLDER + "/video_data_initialise.json");
            JSONObject json = new JSONObject(FileUtils.readFileToString(infoFile));
            return json.getString("video_duration");
        }
        catch (IOException | JSONException e)
        {
            return null;
        }    
    }

    public static String getDate()
    {
        try
        {
            File infoFile = new File(FOLDER + "/video_data_initialise.json");
            JSONObject json = new JSONObject(FileUtils.readFileToString(infoFile));
            return json.getString("video_date");
        }
        catch (IOException | JSONException e)
        {
            return null;
        }    
    }
}


