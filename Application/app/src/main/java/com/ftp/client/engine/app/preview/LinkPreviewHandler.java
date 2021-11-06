package com.ftp.client.engine.app.preview;

public interface LinkPreviewHandler {
    void onStart();
    void onGetLinkRedirectedTo(String link_redirected_to);
    void onGetTitle(String title);
    void onGetDescription(String description);
    void onGetFavicon(String faviconLink);
    void onGetImageLink(String imageLink);
    void onFail(String response, String error);
    void onComplete();
}
