package com.jeongjuwon.iamport;

import android.annotation.SuppressLint;

import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.webkit.JavascriptInterface;
import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.content.Intent;


import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;

class IAmPortWebView extends WebView implements LifecycleEventListener {

    private final IAmPortViewManager mViewManager;
    private final ThemedReactContext mReactContext;

    private Activity activity;

    private String appScheme = "appScheme";
    private String charset = "UTF-8";
    private String baseUrl = "file:///";
    private String injectedJavaScript = null;
    private boolean allowUrlRedirect = false;

    private class IAmPortWebViewBridge {

      IAmPortWebView mContext;

      IAmPortWebViewBridge(IAmPortWebView c) {
        mContext = c;
      }

      @JavascriptInterface
      public void receiveResult(String result, String imp_uid, String merchant_uid) {

        mContext.emitPaymentEvent(result, imp_uid, merchant_uid);
      }
    }

    public IAmPortWebView(IAmPortViewManager viewManager, ThemedReactContext reactContext) {

        super(reactContext);

        mViewManager = viewManager;
        mReactContext = reactContext;

        activity = reactContext.getCurrentActivity();

        this.getSettings().setJavaScriptEnabled(true);
        this.getSettings().setBuiltInZoomControls(false);
        this.getSettings().setDomStorageEnabled(true);
        this.getSettings().setGeolocationEnabled(false);
        this.getSettings().setPluginState(WebSettings.PluginState.ON);
        this.getSettings().setAllowFileAccess(true);
        this.getSettings().setAllowFileAccessFromFileURLs(true);
        this.getSettings().setAllowUniversalAccessFromFileURLs(true);
        this.getSettings().setLoadsImagesAutomatically(true);
        this.getSettings().setBlockNetworkImage(false);
        this.getSettings().setBlockNetworkLoads(false);

        reactContext.addLifecycleEventListener(this);

        this.addJavascriptInterface(new IAmPortWebViewBridge(this), "iamport");
    }

    public void setAppScheme(String appScheme){

        this.appScheme = appScheme;
    }

    public String getAppScheme(){

        return this.appScheme;
    }

    public void setBaseUrl(String baseUrl) {

        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {

        return this.baseUrl;
    }

    public String getCharset() {

        return this.charset;
    }

    public void emitPaymentEvent(String result, String imp_uid, String merchant_uid){

        WritableMap params = Arguments.createMap();
        params.putString("result", result);
        params.putString("imp_uid", imp_uid);
        params.putString("merchant_uid", merchant_uid);

        mReactContext.getJSModule(RCTDeviceEventEmitter.class).emit("paymentEvent", params);
    }

    @Override
    public void onHostResume() {

        Intent intent = activity.getIntent();

        Log.i("iamport", "onHostResume - IAmPortWebView");

        if ( intent != null ) {

            Uri intentData = intent.getData();

            if ( intentData != null ) {

                //카카오페이 인증 후 복귀했을 때 결제 후속조치
                String url = intentData.toString();

                Log.i("iamport", "receive URL - " + url);

                if ( url.startsWith(this.getAppScheme() + "://process") ) {

                    Log.i("iamport", "process");
                    this.loadUrl("javascript:IMP.communicate({result:'process'})");
                }
                else if ( url.startsWith(this.getAppScheme() + "://cancel") ) {

                    Log.i("iamport", "cancel");
                    this.loadUrl("javascript:IMP.communicate({result:'cancel'})");
                }
                else if ( url.startsWith(this.getAppScheme() + "://success") ) {

                    Log.i("iamport", "success");

                    Uri uri = Uri.parse(url);
                    String imp_uid = uri.getQueryParameter("imp_uid");
                    String merchant_uid = uri.getQueryParameter("merchant_uid");

                    this.emitPaymentEvent("success", imp_uid, merchant_uid);
                }

                intent.replaceExtras(new Bundle());
                intent.setAction("");
                intent.setData(null);
                intent.setFlags(0);
            }
        }
    }

    @Override
    public void onHostPause() {

        Log.i("iamport", "onHostPause - IAmPortWebView");
    }

    @Override
    public void onHostDestroy() {

        Log.i("iamport", "onHostDestroy - IAmPortWebView");
        destroy();
    }
}
