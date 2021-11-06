package com.ftp.client.engine.app.preview;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;

/**
 * LinkPreview
 */

public class LinkPreview {
    private LinkPreviewHandler listener;
    private String init_link;
    private Context context;
    private enum OPTIONS{GET_TITLE, GET_DESCRIPTION, GET_FAVICON, GET_IMAGE}
    private ArrayList<OPTIONS> optionsList;
    private boolean skipReadCache = false;
    private boolean skipStoreCache = false;
    public LinkPreview(Context context){
        this.context = context;
        optionsList = new ArrayList<>();
        try {
            for (OPTIONS option : OPTIONS.values()) {
                if (!optionsList.contains(option)) {
                    optionsList.add(option);
                }
            }
        }catch(Exception ex){}
        PreviewCache previewCache = new PreviewCache();
        previewCache.sortCacheClear();
    }

    public LinkPreview skipReadFromCache(boolean skip){
        this.skipReadCache = skip;
        return this;
    }

    public LinkPreview skipStoreToCache(boolean skip){
        this.skipStoreCache = skip;
        return this;
    }

    private LinkPreview setOptions(OPTIONS... options){
        if(options!=null){
            optionsList.clear();
            try{
                for(int i=0; i<options.length; i++){
                    OPTIONS option = options[i];
                    if(option!=null){
                        if(!optionsList.contains(option)) {
                            optionsList.add(option);
                        }
                    }
                }
            }catch(Exception ex){}
        }
        return this;
    }

    public void preview(String link, LinkPreviewHandler listener){
        this.init_link = link;
        this.listener = listener;
        CookieHandler.setDefault( new CookieManager( null, CookiePolicy.ACCEPT_ALL ) ); //ENABLING COOKIES
        Connection connection = new Connection();
        connection.get(this.init_link);
    }


    private String getFinalURL(String _url) throws IOException {
        URL url = new URL(_url);
        HttpURLConnection http_conn = (HttpURLConnection) url.openConnection();
        http_conn.setInstanceFollowRedirects(false);
        http_conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
        //http_conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");//en-GB
        http_conn.addRequestProperty("Referer", "https://www.google.com/");
        int resCode = http_conn.getResponseCode();
        if (resCode == HttpURLConnection.HTTP_SEE_OTHER || resCode == HttpURLConnection.HTTP_MOVED_PERM || resCode == HttpURLConnection.HTTP_MOVED_TEMP) {
            String location = http_conn.getHeaderField("Location");
            if (location.startsWith("/")) {
                location = url.getProtocol() + "://" + url.getHost() + location;
            }
            return getFinalURL(location);
        }
        return _url;
    }

    private class HTMLPage {
        private String title;
        private String faviconLink;
        private ArrayList<String> imageList;
        private String description;
        private String finalLink;

        public HTMLPage() {
            title = "";
            imageList = new ArrayList<>();
        }

        public HTMLPage process(String html_page, String _link) {
            setFinalLink(_link);
            taskExecutor.publishProgress("final_link", getFinalLink());
            title = "";
            description = "";
            faviconLink = "";
            imageList = new ArrayList<>();

            html_page = html_page.replaceAll("<\\s*(script|style)(.*?)>(.*?)</\\s*(script|style)\\s*>", "");

            Pattern pattern;
            Matcher matcher;
            if(optionsList.contains(OPTIONS.GET_TITLE)) {
                pattern = Pattern.compile("<\\s*title\\s*[^<>,{}]*?>(.*?)</\\s*title\\s*>");
                matcher = pattern.matcher(html_page);
                if (matcher.find()) {
                    String title = matcher.group().replaceAll("<\\s*title\\s*[^<>,{}]*?>", "").replaceAll("</\\s*title\\s*>", "");
                    setTitle(title);
                    taskExecutor.publishProgress("title", getTitle());
                } else {
                    setTitle("");
                    taskExecutor.publishProgress("title", getTitle());
                }
            }

            if(optionsList.contains(OPTIONS.GET_DESCRIPTION)) {
                pattern = Pattern.compile("<\\s*meta name=\"description\" \\s*content=\"(.*?)\"\\s*(/*?)>");
                matcher = pattern.matcher(html_page);
                if (matcher.find()) {
                    String paragraph = matcher.group().replaceAll("<\\s*meta name=\"description\" \\s*content=\"", "").replaceAll("\"\\s*(/*?)>", "");
                    String _description = getDescription();
                    paragraph = paragraph.trim();
                    _description += paragraph;
                    setDescription(_description);
                    taskExecutor.publishProgress("description", getDescription());
                } else {
                    setDescription("");
                    taskExecutor.publishProgress("description", getDescription());
                }
            }

            if(optionsList.contains(OPTIONS.GET_FAVICON)) {
                pattern = Pattern.compile("<\\s*link rel=\"[^<>,{}\"]*?icon\"\\s*[^<>,{}]*?\\s*href=\"[^<>,{}\"]*?.(png|jpg|jpeg|gif)\"\\s*[^<>,{}]*?>");
                matcher = pattern.matcher(html_page);
                if (matcher.find()) {
                    String favicon_url = matcher.group().replaceAll("<\\s*link rel=\"[^<>,{}\"]*?icon\"\\s*[^<>,{}]*?\\s*href=\"", "").replaceAll("\"\\s*[^<>,{}]*?>", "");
                    if (!favicon_url.startsWith("http") && !favicon_url.startsWith("https")) {
                        favicon_url = completeImageLink(_link, favicon_url);
                    }
                    try {
                        favicon_url = URLDecoder.decode(favicon_url, "UTF-8");
                        favicon_url = favicon_url.replace(" ", "%20");
                    } catch (Exception ex) {
                    }
                    try {
                        favicon_url = getFinalURL(favicon_url);
                    } catch (Exception ex) {
                    }
                    setFaviconLink(favicon_url);
                    taskExecutor.publishProgress("favicon", favicon_url);
                } else {
                    setFaviconLink("");
                    taskExecutor.publishProgress("favicon", "");
                }
            }
            if(optionsList.contains(OPTIONS.GET_IMAGE)) {
                if (imageList.size() == 0) {
                    pattern = Pattern.compile("data-url(=)[^<>,{}]*?.(png|jpg|jpeg|gif)(.*?)\"", Pattern.CASE_INSENSITIVE);
                    matcher = pattern.matcher(html_page);
                    if (matcher.find()) { //TO JUST CAPTURE THE FIRST IMAGE 'while' WAS REPLACED WITH 'if'
                        String image_url = matcher.group().replaceAll("data-url(=)", "").replaceAll("\"", "");
                        if (!image_url.startsWith("http") && !image_url.startsWith("https")) {
                            image_url = completeImageLink(_link, image_url);
                        }
                        try {
                            image_url = URLDecoder.decode(image_url, "UTF-8");
                            image_url = image_url.replace(" ", "%20");
                        } catch (Exception ex) {
                        }
                        try {
                            image_url = getFinalURL(image_url);
                        } catch (Exception ex) {
                        }
                        imageList.add(image_url);
                        taskExecutor.publishProgress("imageLink", image_url);
                    }
                }

                if (imageList.size() == 0) {
                    pattern = Pattern.compile("media(=)[^<>,{}]*?.(png|jpg|jpeg|gif)(.*?)\"", Pattern.CASE_INSENSITIVE);
                    matcher = pattern.matcher(html_page);
                    if (matcher.find()) { //TO JUST CAPTURE THE FIRST IMAGE 'while' WAS REPLACED WITH 'if'
                        String image_url = matcher.group().replaceAll("media(=)", "").replaceAll("\"", "");
                        if (!image_url.startsWith("http") && !image_url.startsWith("https")) {
                            image_url = completeImageLink(_link, image_url);
                        }
                        try {
                            image_url = URLDecoder.decode(image_url, "UTF-8");
                            image_url = image_url.replace(" ", "%20");
                        } catch (Exception ex) {
                        }
                        try {
                            image_url = getFinalURL(image_url);
                        } catch (Exception ex) {
                        }
                        imageList.add(image_url);
                        taskExecutor.publishProgress("imageLink", image_url);
                    }
                }

                if (imageList.size() == 0) {
                    pattern = Pattern.compile("content(=)[^<>,{}]*?.(png|jpg|jpeg|gif)(.*?)\"", Pattern.CASE_INSENSITIVE);
                    matcher = pattern.matcher(html_page);
                    if (matcher.find()) { //TO JUST CAPTURE THE FIRST IMAGE 'while' WAS REPLACED WITH 'if'
                        String image_url = matcher.group().replaceAll("content(=)", "").replaceAll("\"", "");
                        if (!image_url.startsWith("http") && !image_url.startsWith("https")) {
                            image_url = completeImageLink(_link, image_url);
                        }
                        try {
                            image_url = URLDecoder.decode(image_url, "UTF-8");
                            image_url = image_url.replace(" ", "%20");
                        } catch (Exception ex) {
                        }
                        try {
                            image_url = getFinalURL(image_url);
                        } catch (Exception ex) {
                        }
                        imageList.add(image_url);
                        taskExecutor.publishProgress("imageLink", image_url);
                    }
                }

                if (imageList.size() == 0) {
                    pattern = Pattern.compile("data-src(=)[^<>,{}]*?.(png|jpg|jpeg|gif)(.*?)\"", Pattern.CASE_INSENSITIVE);
                    matcher = pattern.matcher(html_page);
                    if (matcher.find()) { //TO JUST CAPTURE THE FIRST IMAGE 'while' WAS REPLACED WITH 'if'
                        String image_url = matcher.group().replaceAll("data-src(=)", "").replaceAll("\"", "");
                        if (!image_url.startsWith("http") && !image_url.startsWith("https")) {
                            image_url = completeImageLink(_link, image_url);
                        }
                        try {
                            image_url = URLDecoder.decode(image_url, "UTF-8");
                            image_url = image_url.replace(" ", "%20");
                        } catch (Exception ex) {
                        }
                        try {
                            image_url = getFinalURL(image_url);
                        } catch (Exception ex) {
                        }
                        imageList.add(image_url);
                        taskExecutor.publishProgress("imageLink", image_url);
                    }
                }

                if (imageList.size() == 0) {
                    pattern = Pattern.compile("srcset(=)[^<>,{}]*?.(png|jpg|jpeg|gif)(.*?)\"", Pattern.CASE_INSENSITIVE);
                    matcher = pattern.matcher(html_page);
                    if (matcher.find()) { //TO JUST CAPTURE THE FIRST IMAGE 'while' WAS REPLACED WITH 'if'
                        String image_url = matcher.group().replaceAll("srcset(=)", "").replaceAll("\"", "");
                        if (!image_url.startsWith("http") && !image_url.startsWith("https")) {
                            image_url = completeImageLink(_link, image_url);
                        }
                        try {
                            image_url = URLDecoder.decode(image_url, "UTF-8");
                            image_url = image_url.replace(" ", "%20");
                        } catch (Exception ex) {
                        }
                        try {
                            image_url = getFinalURL(image_url);
                        } catch (Exception ex) {
                        }
                        imageList.add(image_url);
                        taskExecutor.publishProgress("imageLink", image_url);
                    }
                }

                if (imageList.size() == 0) {
                    pattern = Pattern.compile("src(=)[^<>,{}]*?.(png|jpg|jpeg|gif)(.*?)\"", Pattern.CASE_INSENSITIVE);
                    matcher = pattern.matcher(html_page);
                    if (matcher.find()) { //TO JUST CAPTURE THE FIRST IMAGE 'while' WAS REPLACED WITH 'if'
                        String image_url = matcher.group().replaceAll("src(=)", "").replaceAll("\"", "");
                        if (!image_url.startsWith("http") && !image_url.startsWith("https")) {
                            image_url = completeImageLink(_link, image_url);
                        }
                        try {
                            image_url = URLDecoder.decode(image_url, "UTF-8");
                            image_url = image_url.replace(" ", "%20");
                        } catch (Exception ex) {
                        }
                        try {
                            image_url = getFinalURL(image_url);
                        } catch (Exception ex) {
                        }
                        imageList.add(image_url);
                        taskExecutor.publishProgress("imageLink", image_url);
                    }
                }

                if (imageList.size() == 0) {
                    pattern = Pattern.compile("http(s?):[^<>\",{}]*?.(png|jpg|jpeg|gif)(.*?)\"", Pattern.CASE_INSENSITIVE);
                    matcher = pattern.matcher(html_page);
                    if (matcher.find()) { //TO JUST CAPTURE THE FIRST IMAGE 'while' WAS REPLACED WITH 'if'
                        String image_url = matcher.group();
                        try {
                            image_url = URLDecoder.decode(image_url, "UTF-8");
                            image_url = image_url.replace(" ", "%20");
                        } catch (Exception ex) {
                        }
                        try {
                            image_url = getFinalURL(image_url);
                        } catch (Exception ex) {
                        }
                        imageList.add(image_url);
                        taskExecutor.publishProgress("imageLink", image_url);
                    }
                }

                if (imageList.size() == 0) {
                    taskExecutor.publishProgress("imageLink", "");
                }
            }
            return this;
        }

        private String completeImageLink(String _source, String image_url){
            try {
                URL url = new URL(_source);
                String protocol = url.getProtocol();
                String host = url.getHost();
                if(image_url.startsWith("//")){
                    return protocol+ ":" + image_url;
                }else if(image_url.contains(host)){
                    return protocol + "://" + image_url;
                }
                return protocol + "://" + host + image_url;
            }catch (Exception ex){}
            return _source+image_url;
        }
        public String getFinalLink() {
            if (finalLink == null) {
                finalLink = "";
            }
            return finalLink;
        }

        public void setFaviconLink(String faviconLink) {
            this.faviconLink = faviconLink;
        }
        public String getFaviconLink() {
            if (faviconLink == null) {
                faviconLink = "";
            }
            return faviconLink;
        }

        public void setFinalLink(String finalLink) {
            this.finalLink = finalLink;
        }
        public String getTitle() {
            if (title == null) {
                title = "";
            }
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImageLink(){
            String imageLink = "";
            if(imageList.size()>0){
                imageLink = imageList.get(0);
            }
            return imageLink;
        }
        public ArrayList<String> getImageList() {
            return imageList;
        }

        public void setImageList(ArrayList<String> imageList) {
            this.imageList = imageList;
        }

        public String getDescription() {
            if (description == null) {
                description = "";
            }
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
    Connection.TaskExecutor taskExecutor;
    private class Connection{

        private String link;
        private int TIME_OUT = 30000; //30 seconds

        private int responseCode;
        private String response;
        private Boolean succeeded;
        private String error;

        private HTMLPage htmlPage;

        public Connection(){
            succeeded = false;
            response = "";
            error="";
            responseCode=0;
        }

        public void get(String url){
            this.link=url;
            htmlPage = new HTMLPage();
            taskExecutor = new TaskExecutor();
            taskExecutor.execute();

        }
        private void executeGETRequest()     {
            try{
                succeeded = false;
                response = "";
                error="";
                responseCode=0;

                URL url= new URL(link);
                HttpURLConnection http_conn = (HttpURLConnection)url.openConnection();
                http_conn.setUseCaches(false);
                http_conn.setInstanceFollowRedirects(true);
                http_conn.setConnectTimeout(TIME_OUT);

                http_conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
                //http_conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");//en-GB
                http_conn.setRequestProperty("Referer", "https://www.google.com/");

                responseCode = http_conn.getResponseCode();

                BufferedReader reader;
                if(responseCode == HttpsURLConnection.HTTP_OK || responseCode == HttpsURLConnection.HTTP_CREATED) {
                    reader = new BufferedReader(new InputStreamReader(http_conn.getInputStream()));
                    succeeded = true;
                }else {
                    error=http_conn.getResponseMessage();
                    reader = new BufferedReader(new InputStreamReader(http_conn.getErrorStream()));
                    succeeded = false;
                }
                String line;
                while((line=reader.readLine()) != null){
                    response += line;
                }

                reader.close();

            }catch (Exception ex) {
                succeeded = false;
                error = ex.toString();
            }
            try{
                if(succeeded) {
                    htmlPage.process(response, link);
                    PreviewCache previewCache = new PreviewCache();

                    if(!skipStoreCache) {
                        previewCache.addToCache(link, htmlPage.getFinalLink(), htmlPage.getTitle(), htmlPage.getFaviconLink(), htmlPage.getImageLink(), htmlPage.getDescription());
                        if (!init_link.equals(link)) {
                            previewCache.addToCache(init_link, htmlPage.getFinalLink(), htmlPage.getTitle(), htmlPage.getFaviconLink(), htmlPage.getImageLink(), htmlPage.getDescription());
                        }
                    }
                }
            }catch (Exception ex){}
        }



        private class TaskExecutor extends ConnAsyncTask {
            @Override
            public void onPreExecute() {
                super.onPreExecute();
                if(listener!=null){
                    listener.onStart();
                }
            }
            @Override
            public String doInBackground() {
                PreviewCache previewCache = new PreviewCache();
                JSONObject obj = previewCache.getFromCache(link);
                if(obj!=null && !skipReadCache){
                    htmlPage.setFinalLink(previewCache.getFinalLink(obj));
                    publishProgress("final_link", htmlPage.getFinalLink());
                    htmlPage.setTitle(previewCache.getTitle(obj));
                    publishProgress("title", htmlPage.getTitle());
                    htmlPage.setDescription(previewCache.getDescription(obj));
                    publishProgress("description", htmlPage.getDescription());
                    htmlPage.setFaviconLink(previewCache.getFavicon(obj));
                    publishProgress("favicon", htmlPage.getFaviconLink());
                    ArrayList<String> imageList = new ArrayList<>();
                    imageList.add(previewCache.getImage(obj));
                    htmlPage.setImageList(imageList);
                    publishProgress("imageLink", htmlPage.getImageLink());
                    succeeded = true;
                }else {
                    executeGETRequest();
                }
                if(!succeeded){
                    if(error.contains("java.lang.NullPointerException: lock == null")){
                        try {
                            link = getFinalURL(link);
                        }catch (Exception ex){
                            if(link.contains("http")){
                                link = link.replace("http", "https");
                            }else{
                                link = link.replace("https", "http");
                            }
                        }
                        obj = previewCache.getFromCache(link);
                        if(obj!=null && !skipReadCache){
                            htmlPage.setFinalLink(previewCache.getFinalLink(obj));
                            publishProgress("final_link", htmlPage.getFinalLink());
                            htmlPage.setTitle(previewCache.getTitle(obj));
                            publishProgress("title", htmlPage.getTitle());
                            htmlPage.setDescription(previewCache.getDescription(obj));
                            publishProgress("description", htmlPage.getDescription());
                            htmlPage.setFaviconLink(previewCache.getFavicon(obj));
                            publishProgress("favicon", htmlPage.getFaviconLink());
                            ArrayList<String> imageList = new ArrayList<>();
                            imageList.add(previewCache.getImage(obj));
                            htmlPage.setImageList(imageList);
                            publishProgress("imageLink", htmlPage.getImageLink());
                            succeeded = true;
                        }else {
                            executeGETRequest();
                        }
                    }
                }
                if(!succeeded){
                    if(error.contains("java.lang.NullPointerException: lock == null")){
                        if(link.contains("http")){
                            link = link.replace("http", "https");
                        }else{
                            link = link.replace("https", "http");
                        }
                        obj = previewCache.getFromCache(link);
                        if(obj!=null && !skipReadCache){
                            htmlPage.setFinalLink(previewCache.getFinalLink(obj));
                            publishProgress("final_link", htmlPage.getFinalLink());
                            htmlPage.setTitle(previewCache.getTitle(obj));
                            publishProgress("title", htmlPage.getTitle());
                            htmlPage.setDescription(previewCache.getDescription(obj));
                            publishProgress("description", htmlPage.getDescription());
                            htmlPage.setFaviconLink(previewCache.getFavicon(obj));
                            publishProgress("favicon", htmlPage.getFaviconLink());
                            ArrayList<String> imageList = new ArrayList<>();
                            imageList.add(previewCache.getImage(obj));
                            htmlPage.setImageList(imageList);
                            publishProgress("imageLink", htmlPage.getImageLink());
                            succeeded = true;
                        }else {
                            executeGETRequest();
                        }
                    }
                }
                return null;
            }

            @Override
            public void onProgressUpdate(Object... progress) {
                super.onProgressUpdate(progress);
                try{
                    String key = (String)progress[0];
                    String value = (String)progress[1];
                    switch (key){
                        case "final_link":
                            if(listener!=null){
                                listener.onGetLinkRedirectedTo(value);
                            }
                            break;
                        case "title":
                            if(listener!=null){
                                if(optionsList.contains(OPTIONS.GET_TITLE)) {
                                    listener.onGetTitle(value);
                                }
                            }
                            break;
                        case "description":
                            if(listener!=null){
                                if(optionsList.contains(OPTIONS.GET_DESCRIPTION)) {
                                    listener.onGetDescription(value);
                                }
                            }
                            break;
                        case "favicon":
                            if(listener!=null){
                                if(optionsList.contains(OPTIONS.GET_FAVICON)) {
                                    listener.onGetFavicon(value);
                                }
                            }
                            break;
                        case "imageLink":
                            if(listener!=null){
                                if(optionsList.contains(OPTIONS.GET_IMAGE)) {
                                    listener.onGetImageLink(value);
                                }
                            }
                            break;
                        default:
                    }
                }catch (Exception ex){}
            }

            @Override
            public void onPostExecute() {
                super.onPostExecute();
                if(succeeded){
                    if(listener!=null){
                        //listener.onSucceed(htmlPage.getTitle(), htmlPage.getImageLink(), htmlPage.getDescription(), htmlPage.getFinalLink());
                    }
                }else {
                    if(listener!=null){
                        listener.onFail(response, error);
                    }
                }
                if(listener!=null){
                    listener.onComplete();
                }
            }
        }
    }


    private class ConnAsyncTask implements Runnable{
        Thread thread;
        Handler handler;
        private volatile boolean execute=true;//volatile implies this variable may be modified in any Thread
        public ConnAsyncTask(){
            handler=new Handler(Looper.getMainLooper());
        }
        public void execute(){
            thread=new Thread(this);
            thread.start();
        }
        public void onPreExecute() {

        }
        public String doInBackground() {

            return null;
        }
        public void onPostExecute() {

        }
        public void publishProgress(final Object... progress){
            update(progress);//invokes onProgressUpdate from the main thread
        }
        public void onProgressUpdate(final Object... progress) {//should not be called directly from an extending class

        }

        public void shutdown() {
            handler.post(new Runnable(){
                @Override
                public void run(){
                    execute = false;
                }
            });
        }
        public boolean isUpdating(){
            return execute;
        }

        @Override
        public void run() {
            // Moves the current Thread into the background
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            handler.post(new Runnable(){
                @Override
                public void run(){
                    onPreExecute();
                }
            });
            runBackgroundTask();
            handler.postDelayed(new Runnable(){
                @Override
                public void run(){
                    onPostExecute();
                }
            },100);
        }
        private void update(final Object... progress){
            if(execute) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onProgressUpdate(progress);
                    }
                });
            }
        }
        private void runBackgroundTask(){
            doInBackground();
        }

    }

    private class PreviewCache{
        public void addToCache(String link, String final_link, String title, String favicon, String image, String description){
            JSONObject cache = getCache();
            try{
                JSONObject obj = new JSONObject();
                obj.put("final_link", final_link);
                obj.put("title", title);
                obj.put("favicon", favicon);
                obj.put("image", image);
                obj.put("description", description);

                cache.put(link, obj);
                updatePrefCache(cache.toString());
            }catch (Exception ex){}
        }
        public JSONObject getFromCache(String link){
            JSONObject cache = getCache();
            try{
                JSONObject obj = cache.getJSONObject(link);
                return obj;
            }catch(Exception ex){}
            return null;
        }
        public String getFinalLink(JSONObject obj){
            try{
                return obj.getString("final_link");
            }catch (Exception ex){}
            return "";
        }
        public String getTitle(JSONObject obj){
            try{
                return obj.getString("title");
            }catch (Exception ex){}
            return "";
        }
        public String getFavicon(JSONObject obj){
            try{
                return obj.getString("favicon");
            }catch (Exception ex){}
            return "";
        }
        public String getImage(JSONObject obj){
            try{
                return obj.getString("image");
            }catch (Exception ex){}
            return "";
        }
        public String getDescription(JSONObject obj){
            try{
                return obj.getString("description");
            }catch (Exception ex){}
            return "";
        }
        private void updatePrefCache(String cache){
            try {
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString("eukaprotech_link_preview_cache", cache);
                editor.commit();
            }catch(OutOfMemoryError err){
                clearCache();
            }
        }
        private String getPrefCache(){
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPrefs.getString("eukaprotech_link_preview_cache", "");
        }
        private JSONObject getCache(){
            String response = getPrefCache();
            if(TextUtils.isEmpty(response)){
                return new JSONObject();
            }
            try {
                JSONObject obj=new JSONObject(response);
                return obj;
            } catch (Exception e){
                return new JSONObject();
            }
        }
        public void sortCacheClear(){
            String response = getPrefCache();
            long size = 0L;
            try{
                size = response.getBytes().length;
            }catch (Exception ex){}
            long LIMIT_MBS = 30L * 1024L * 1024L;
            if(size >= LIMIT_MBS){
                clearCache();
            }
        }
        public void clearCache(){
            updatePrefCache("");
        }

    }

    public static void clearCache(Context context){
        try {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString("eukaprotech_link_preview_cache", "");
            editor.commit();
        }catch(OutOfMemoryError err){}
    }
}
