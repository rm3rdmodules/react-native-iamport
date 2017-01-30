package com.datagraph.iamport;

import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.CookieManager;
import android.util.Log;
import android.app.Activity;
import android.content.Intent;

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

import com.siot.iamportsdk.KakaoWebViewClient;
import com.siot.iamportsdk.NiceWebViewClient;
import com.siot.iamportsdk.PaycoWebViewClient;

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

    @ReactProp(name = "html")
    public void setHtml(IAmPortWebView view, @Nullable String html) {

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

        Log.e("iamport", "PG - " + pg);

        if(pg.equals("nice")){

            view.setWebViewClient(new KakaoWebViewClient(activity, view));
        }
        else if(pg.equals("kakao")){

            view.setWebViewClient(new NiceWebViewClient(activity, view));
        }
        else if(pg.equals("payco")){

            view.setWebViewClient(new PaycoWebViewClient(activity, view));
        }
    }

    @Override
    public void onDropViewInstance(IAmPortWebView webView) {

        super.onDropViewInstance(webView);
        ((ThemedReactContext) webView.getContext()).removeLifecycleEventListener(webView);
    }
}
