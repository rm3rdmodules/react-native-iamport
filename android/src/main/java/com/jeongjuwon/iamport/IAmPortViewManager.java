package com.jeongjuwon.iamport;

import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.CookieManager;
import android.util.Log;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.facebook.react.ReactActivity;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.common.annotations.VisibleForTesting;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;

import com.siot.iamportsdk.KakaoWebViewClient;
import com.siot.iamportsdk.NiceWebViewClient;
import com.siot.iamportsdk.PaycoWebViewClient;
import com.siot.iamportsdk.KcpWebViewClient;
import com.siot.iamportsdk.CallbackWebViewClient;

public class IAmPortViewManager extends SimpleViewManager<IAmPortWebView> {

    private static final String HTML_MIME_TYPE = "text/html";

    private HashMap<String, String> headerMap = new HashMap<>();
    private IAmPortPackage aPackage;
    private Activity activity;
    private ThemedReactContext reactContext;

    @VisibleForTesting
    public static final String REACT_CLASS = "IAmPortViewManager";

    @Override
    public String getName() {

        return REACT_CLASS;
    }

    @Override
    public IAmPortWebView createViewInstance(ThemedReactContext context) {

        IAmPortWebView webView = new IAmPortWebView(this, context);

        reactContext = context;

        activity = context.getCurrentActivity();

        // Fixes broken full-screen modals/galleries due to body
        // height being 0.
        webView.setLayoutParams(
            new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT)
        );
        CookieManager.getInstance().setAcceptCookie(true); // add default cookie support
        CookieManager.getInstance().setAcceptFileSchemeCookies(true); // add default cookie support

        return webView;
    }

    public void setPackage(IAmPortPackage aPackage) {

        this.aPackage = aPackage;
    }

    public IAmPortPackage getPackage() {

        return this.aPackage;
    }

    public void emitPaymentEvent(String result, String imp_uid, String merchant_uid){

        WritableMap params = Arguments.createMap();
        params.putString("result", result);
        params.putString("imp_uid", imp_uid);
        params.putString("merchant_uid", merchant_uid);

        reactContext.getJSModule(RCTDeviceEventEmitter.class).emit("paymentEvent", params);
    }

    @ReactProp(name = "html")
    public void setHtml(IAmPortWebView view, @Nullable String html) {
        Log.i("iamport", "setHtml: " + html);
        view.loadDataWithBaseURL(view.getBaseUrl(), html, HTML_MIME_TYPE, view.getCharset(), null);
    }

    @ReactProp(name = "appScheme")
    public void setAppScheme(IAmPortWebView view, @Nullable String appScheme) {

        view.setAppScheme(appScheme);
    }

    @ReactProp(name = "source")
    public void setSource(IAmPortWebView view, @Nullable String source) {

        setHtml(view, source);
    }

    @ReactProp(name = "pg")
    public void setPG(IAmPortWebView view, @Nullable String pg) {

        Log.i("iamport", "PG - " + pg);

        if(pg.equals("nice")){
          NiceWebViewClient webViewClient = new NiceWebViewClient(activity, view, new UrlLoadingCallBack() {

            @Override
            public void shouldOverrideUrlLoadingCallBack(String s) {
              Log.i("iamport", "NiceWebViewClient.shouldOverrideUrlLoadingCallBack - " + s);
              emitPaymentEvent(s, s, s);
            }

          });
          view.setWebViewClient(webViewClient);
        } else if(pg.equals("kakao")){
            view.setWebViewClient(new KakaoWebViewClient(activity, view));
        } else if(pg.equals("payco")){
          PaycoWebViewClient webViewClient = new PaycoWebViewClient(activity, view, new UrlLoadingCallBack() {

            @Override
            public void shouldOverrideUrlLoadingCallBack(String s) {
              Log.i("iamport", "PaycoWebViewClient.shouldOverrideUrlLoadingCallBack - " + s);
              emitPaymentEvent(s, s, s);
            }

          });
          view.setWebViewClient(webViewClient);
        } else if(pg.equals("kcp")){
            KcpWebViewClient webViewClient = new KcpWebViewClient(activity, view, new UrlLoadingCallBack() {
                @Override
                public void shouldOverrideUrlLoadingCallBack(String s) {
                    Log.i("iamport", "KcpWebViewClient.shouldOverrideUrlLoadingCallBack - " + s);
                    emitPaymentEvent(s, s, s);
                }
            });
            view.setWebViewClient(webViewClient);
        } else {
            CallbackWebViewClient defaultWebViewClient = new CallbackWebViewClient(activity, view, new UrlLoadingCallBack() {
                @Override
                public void shouldOverrideUrlLoadingCallBack(String s) {
                    Log.i("iamport", "CallbackWebViewClient.shouldOverrideUrlLoadingCallBack - " + s);
                    emitPaymentEvent(s, s, s);
                }
            });
            view.setWebViewClient(defaultWebViewClient);
        }
    }

    @Override
    public void onDropViewInstance(IAmPortWebView webView) {

        super.onDropViewInstance(webView);
        ((ThemedReactContext) webView.getContext()).removeLifecycleEventListener(webView);
    }
}
