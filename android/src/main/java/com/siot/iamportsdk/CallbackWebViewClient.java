package com.siot.iamportsdk;

import android.app.Activity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jeongjuwon.iamport.UrlLoadingCallBack;

/**
 * Created by jang on 2018. 5. 31..
 */

public class CallbackWebViewClient extends WebViewClient {

    private Activity activity;
    UrlLoadingCallBack mCallBack;

    public CallbackWebViewClient(Activity activity, WebView target, UrlLoadingCallBack callBack) {
        this.activity = activity;
        this.mCallBack = callBack;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.i("iamport", "CallbackWebViewClient.shouldOverrideUrlLoading: " + url);
        mCallBack.shouldOverrideUrlLoadingCallBack(url);

        return super.shouldOverrideUrlLoading(view, url);
    }
}