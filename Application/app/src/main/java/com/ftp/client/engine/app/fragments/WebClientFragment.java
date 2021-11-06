package com.ftp.client.engine.app.fragments;

import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Environment;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.ValueCallback;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.StringReader;

import com.ftp.client.R;
import com.ftp.client.AppController;
import com.ftp.client.application.RemoteWebActivity;
import com.ftp.client.engine.app.config.Constants;
import com.ftp.client.engine.app.preview.LinkPreview;
import com.ftp.client.engine.app.preview.LinkPreviewHandler;
import com.ftp.client.engine.app.preview.HttpResponseCode;
import com.ftp.client.engine.widget.AdvancedWebView;
import com.ftp.client.engine.widget.AdvancedWebView.Listener;


import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

import com.ftp.client.R;
import com.ftp.client.engine.app.config.Constants;
import com.ftp.client.engine.widget.AdvancedWebView;
import android.text.TextUtils;

public class WebClientFragment extends Fragment implements Listener {

    public static String TAG = WebClientFragment.class.getSimpleName();
    private static final String ARG_EXTRA_URL = "ARG_EXTRA_URL";

    public static WebClientFragment createFor(String text) {
        WebClientFragment fragment = new WebClientFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EXTRA_URL, text);
        fragment.setArguments(args);
        return fragment;
    }


    private Context mContext;
    private AdvancedWebView mCurrentView;
    private String mSearchText;
    private ProgressBar progressBar;

    private SwipeRefreshLayout refreshLayout;
    private RelativeLayout webContainer;
    private FrameLayout progressBarContainer;
    private LinearLayout firstLoadingView;

    private View mCustomView;
    private int mOriginalSystemUiVisibility;
    private int mOriginalOrientation;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    protected FrameLayout mFullscreenContainer;

    Integer shortAnimDuration;

    Integer previsionThemeColor = Color.parseColor("#FF8B14");

    SharedPreferences sharedPreferences;

    private Activity mActivity;
    private HttpResponseCode responseCode;

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
        previsionThemeColor = Color.parseColor("#FF8B14");
        shortAnimDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mCurrentView = (AdvancedWebView)view.findViewById(R.id.webview);

        refreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.refresh_layout);
        progressBar = (ProgressBar)view.findViewById(R.id.main_progress_bar);

        webContainer = (RelativeLayout)view.findViewById(R.id.web_container);
        firstLoadingView = (LinearLayout)view.findViewById(R.id.first_loading_view);
        progressBarContainer = (FrameLayout)view.findViewById(R.id.main_progress_bar_container);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        responseCode = new HttpResponseCode(mContext, true);

        mCurrentView.setListener(mActivity, this);
        mCurrentView.setGeolocationEnabled(false);
        mCurrentView.setMixedContentAllowed(true);
        mCurrentView.setCookiesEnabled(true);
        mCurrentView.setThirdPartyCookiesEnabled(true);
        mCurrentView.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageFinished(WebView view, String url) {
                    Toast.makeText(mContext, "Finished loading", Toast.LENGTH_SHORT).show();
                }

            });
        mCurrentView.setWebChromeClient(chromeClient);
        mCurrentView.addHttpHeader("X-Requested-With", "");

        // Add Javascript Interface, this will expose "window.NotificationBind"
        // in Javascript
        //mCurrentView.addJavascriptInterface(new NotificationBindObject(getActivity().getApplicationContext()), "NotificationBind");    
        mCurrentView.loadUrl(mSearchText); 
        LinkPreview linkPreview = new LinkPreview(mContext);
        linkPreview.skipReadFromCache(true).preview(mSearchText, new LinkPreviewHandler() {

                @Override
                public void onStart() {

                }

                @Override
                public void onGetLinkRedirectedTo(String link_redirected_to) {

                }

                @Override
                public void onGetTitle(String title) {

                }

                @Override
                public void onGetDescription(String description) {

                }

                @Override
                public void onGetFavicon(String faviconLink) {

                }

                @Override
                public void onGetImageLink(String imageLink) {

                }

                @Override
                public void onFail(String response, String error) {

                }


                @Override
                public void onComplete() {

                }
            });
        new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(1000);
                    // Now change the color back. Needs to be done on the UI thread
                    mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (firstLoadingView.getVisibility() == View.VISIBLE) {
                                    crossFade(firstLoadingView, webContainer);                            
                                }
                            }
                        });
                }
            }).start();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mCurrentView.reload();                     
                }
            });
    }

    /**
     * This is a little bit of trickery to make the background color of the UI
     * the same as the anticipated UI background color of the web-app.
     *
     * @param bgColor
     */
    private void preventBGColorFlicker(int bgColor) {
        ((ViewGroup) getActivity().findViewById(R.id.container)).setBackgroundColor(bgColor);
        mCurrentView.setBackgroundColor(bgColor);
    }

    /**
     * This method is designed to hide how Javascript is injected into
     * the WebView.
     *
     * In KitKat the new evaluateJavascript method has the ability to
     * give you access to any return values via the ValueCallback object.
     *
     * The String passed into onReceiveValue() is a JSON string, so if you
     * execute a javascript method which return a javascript object, you can
     * parse it as valid JSON. If the method returns a primitive value, it
     * will be a valid JSON object, but you should use the setLenient method
     * to true and then you can use peek() to test what kind of object it is,
     *
     * @param javascript
     */
    public void loadJavascript(String javascript) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // In KitKat+ you should use the evaluateJavascript method
            mCurrentView.evaluateJavascript(javascript, new ValueCallback<String>() {
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                    @Override
                    public void onReceiveValue(String s) {
                        JsonReader reader = new JsonReader(new StringReader(s));

                        // Must set lenient to parse single values
                        reader.setLenient(true);

                        try {
                            if (reader.peek() != JsonToken.NULL) {
                                if (reader.peek() == JsonToken.STRING) {
                                    String msg = reader.nextString();
                                    if (msg != null) {
                                        Toast.makeText(getActivity().getApplicationContext(),
                                                       msg, Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        } catch (IOException e) {
                            Log.e("TAG", "MainActivity: IOException", e);
                        } finally {
                            try {
                                reader.close();
                            } catch (IOException e) {
                                // NOOP
                            }
                        }
                    }
                });
        } else {
            /**
             * For pre-KitKat+ you should use loadUrl("javascript:<JS Code Here>");
             * To then call back to Java you would need to use addJavascriptInterface()
             * and have your JS call the interface
             **/
            mCurrentView.loadUrl("javascript:" + javascript);
        }
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        mCurrentView.setVisibility(View.INVISIBLE);

        //Showing progress bar
        progressBarContainer.animate()
            .alpha(1f)
            .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    progressBarContainer.setVisibility(View.VISIBLE);
                }
            });

    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {

        // Load the local index.html file       
        if (errorCode == -2) {
            LayoutInflater factory = LayoutInflater.from(mContext);
            final View mErrorLayout = factory.inflate(R.layout.layout_dialog_error, null);
            
            WebView webError = (WebView)mErrorLayout.findViewById(R.id.webError);
            WebSettings webSettings = webError.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webError.setWebViewClient(new WebViewClient());

            String style = "<style type='text/css'>@font-face { font-family: 'roboto'; src: url('Roboto-Light.ttf');}@font-face { font-family: 'roboto-medium'; src: url('Roboto-Medium.ttf'); }" +
                "body{color:#666;font-family: 'roboto';padding: 0.3em;}";
            style += "a{color:" + String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(mContext, R.color.colorPrimaryDark))) + "}</style>";
            String customHtml = "<h1>Hello, User</h1><br/><h1>Server Sepertinya Mengalami Masalah.</h1><br/>" + "Dan Masalah Yang Muncul Dengan Kode : " +
                "<p color:#f44336;>" + description + "</p><br/>" +
                "Oleh Karena Itu, " + "<a href=//" + failingUrl + ">" + failingUrl + "</a><br/>" + "  Tidak Dapat Di Tampilkan.";
            String content = "<html><head>" + style + "</head><body'>" + customHtml + "</body></Html>";
            webError.loadData(content, "text/html", null);
            
            AlertDialog.Builder dialogError =  new AlertDialog.Builder(mContext);
            dialogError.setTitle(R.string.app_name);
            dialogError.setView(mErrorLayout);
            dialogError.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked OK so do some stuff */
                    }
                });
            dialogError.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked cancel so do some stuff */
                    }
                });
            AlertDialog alert = dialogError.create();
            alert.show();
        }

    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
        Toast.makeText(mContext, "onDownloadRequested(url = " + url + ",  suggestedFilename = " + suggestedFilename + ",  mimeType = " + mimeType + ",  contentLength = " + contentLength + ",  contentDisposition = " + contentDisposition + ",  userAgent = " + userAgent + ")", Toast.LENGTH_LONG).show();

        /*if (AdvancedWebView.handleDownload(this, url, suggestedFilename)) {
         // download successfully handled
         }
         else {
         // download couldn't be handled because user has disabled download manager app on the device
         }*/
    }

    @Override
    public void onExternalPageRequest(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    @Override
    public void onPageFinished(String url) {
        mCurrentView.setVisibility(View.VISIBLE);

        progressBarContainer.animate()
            .alpha(0f)
            .setDuration(getResources().getInteger(android.R.integer.config_longAnimTime))
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    progressBarContainer.setVisibility(View.GONE);
                }
            });

        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume:");
        mCurrentView.onResume();
    }

    @SuppressLint("NewApi")
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause:");
        mCurrentView.onPause();  
    }

    @SuppressLint("NewApi")
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onResume:");

        mCurrentView.onDestroy();         
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCurrentView.onActivityResult(requestCode, resultCode, data);
    }


    public boolean isRunning() {
        return mCurrentView.onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void changeUIColor(Integer color) {

        ValueAnimator anim = ValueAnimator.ofArgb(previsionThemeColor, color);
        anim.setEvaluator(new ArgbEvaluator());

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    progressBar.getProgressDrawable().setColorFilter(new LightingColorFilter(0xFF000000, (Integer) valueAnimator.getAnimatedValue()));
                    setSystemBarColor((Integer) valueAnimator.getAnimatedValue());

                }
            });

        anim.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
        anim.start();
        refreshLayout.setColorSchemeColors(color, color, color);

    }

    private void setProgressBarColor(Integer color) {

        ValueAnimator anim = ValueAnimator.ofArgb(previsionThemeColor, color);
        anim.setEvaluator(new ArgbEvaluator());

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    progressBar.getProgressDrawable().setColorFilter(new LightingColorFilter(0xFF000000, (Integer) valueAnimator.getAnimatedValue()));                 
                }
            });

        anim.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
        anim.start();
        refreshLayout.setColorSchemeColors(color, color, color);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setSystemBarColor(int color) {

        int clr;

        //this makes the color darker or uses nicer orange color

        if (color != Color.parseColor("#FF8B14")) {
            float[] hsv = new float[3];
            Color.colorToHSV(color, hsv);
            hsv[2] *= 0.8f;
            clr = Color.HSVToColor(hsv);
        } else {
            clr = Color.parseColor("#F47D20");
        }

        Window window = mActivity.getWindow();
        window.setStatusBarColor(clr);
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

    private WebChromeClient chromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int progress) {

            //update the progressbar value
            ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", progress);
            animation.setDuration(100); // 0.5 second
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();

        }

        @Override
        public Bitmap getDefaultVideoPoster() {
            if (getActivity() == null) {
                return null;
            }

            return BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.aweb_icon);

        }

        @Override
        public void onShowCustomView(View view,
                                     WebChromeClient.CustomViewCallback callback) {
            // if a view already exists then immediately terminate the new one
            if (mCustomView != null) {
                onHideCustomView();
                return;
            }

            // 1. Stash the current state
            mCustomView = view;
            mOriginalSystemUiVisibility = getActivity().getWindow().getDecorView().getSystemUiVisibility();
            mOriginalOrientation = getActivity().getRequestedOrientation();

            // 2. Stash the custom view callback
            mCustomViewCallback = callback;

            // 3. Add the custom view to the view hierarchy
            FrameLayout decor = (FrameLayout) getActivity().getWindow().getDecorView();
            decor.addView(mCustomView, new FrameLayout.LayoutParams(
                              ViewGroup.LayoutParams.MATCH_PARENT,
                              ViewGroup.LayoutParams.MATCH_PARENT));


            // 4. Change the state of the window
            getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_IMMERSIVE);
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        @Override
        public void onHideCustomView() {
            // 1. Remove the custom view
            FrameLayout decor = (FrameLayout) getActivity().getWindow().getDecorView();
            decor.removeView(mCustomView);
            mCustomView = null;

            // 2. Restore the state to it's original form
            getActivity().getWindow().getDecorView().setSystemUiVisibility(mOriginalSystemUiVisibility);
            getActivity().setRequestedOrientation(mOriginalOrientation);

            // 3. Call the custom view callback
            mCustomViewCallback.onCustomViewHidden();
            mCustomViewCallback = null;

        }
    };
}
