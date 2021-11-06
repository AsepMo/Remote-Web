package com.ftp.client.engine.app.config;

import android.content.Context;
import com.ftp.client.engine.app.settings.Settings;

public class Constants {

    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static final String PASSWORD_PATTERN = "^(?=.*?\\p{Lu})(?=.*?[\\p{L}&&[^\\p{Lu}]])(?=.*?\\d)(?=.*?[`~!@#$%^&*()\\-_=+\\\\\\|\\[{\\]};:'\",<.>/?]).*$";

    public static final String GOOGLE_YOUTUBE_API_KEY = "AIzaSyAdDix7i7a3an-gyXiquTV_14cIsr8-DZg";

    public static final String CHANNEL_ID = "UCETIWfzg3mjhqBf9ImGmK1Q";

    
    public static String getChannelURL(Context context)
    {
        return "https://www.googleapis.com/youtube/v3/search?part=snippet&order=date&channelId=" + new Settings(context).getChannelID() + "&maxResults=10&key=" + GOOGLE_YOUTUBE_API_KEY + "";
    }
    
    public static final String YOUTUBE = "http://www.youtube.com";
    
    public static final String HTTP = "http://";

    public static final String HTTPS = "https://";

    public static final String FTP = "ftp://";
    
    public static final String FILE = "file://";
    
}

