package com.siot.iamportsdk;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.jeongjuwon.iamport.UrlLoadingCallBack;

/**
 * from https://github.com/iamport/kcp-android-graddle/blob/master/app/src/main/java/kr/iamport/sdk/KcpWebViewClient.java
 * Created by jang on 2017. 9. 14..
 */

public class KcpWebViewClient extends WebViewClient {

    private Activity activity;
    private WebView target;
    final String KTFC_PACKAGE = "com.kftc.bankpay.android";
    UrlLoadingCallBack mCallBack;

    public KcpWebViewClient(Activity activity, WebView target, UrlLoadingCallBack callBack) {
        this.activity = activity;
        this.target = target;
        this.mCallBack = callBack;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.i("iamport", "KcpWebViewClient.shouldOverrideUrlLoading - " + url);
        mCallBack.shouldOverrideUrlLoadingCallBack(url);

        if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("javascript:")) {
            Intent intent = null;

            try {
                intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME); //IntentURI처리
                Uri uri = Uri.parse(intent.getDataString());

                activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                return true;
            } catch (URISyntaxException ex) {
                return false;
            } catch (ActivityNotFoundException e) {
                if (intent == null) return false;

                if (handleNotFoundPaymentScheme(intent.getScheme())) return true;

                String packageName = intent.getPackage();
                if (packageName != null) {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                    return true;
                }

                return false;
            }
        }

        return false;
    }

    /**
     * @param scheme
     * @return 해당 scheme에 대해 처리를 직접 하는지 여부
     * <p>
     * 결제를 위한 3rd-party 앱이 아직 설치되어있지 않아 ActivityNotFoundException이 발생하는 경우 처리합니다.
     * 여기서 handler되지않은 scheme에 대해서는 intent로부터 Package정보 추출이 가능하다면 다음에서 packageName으로 market이동합니다.
     */
    protected boolean handleNotFoundPaymentScheme(String scheme) {
        //PG사에서 호출하는 url에 package정보가 없어 ActivityNotFoundException이 난 후 market 실행이 안되는 경우
        if (PaymentScheme.ISP.equalsIgnoreCase(scheme)) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PaymentScheme.PACKAGE_ISP)));
            return true;
        } else if (PaymentScheme.BANKPAY.equalsIgnoreCase(scheme)) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PaymentScheme.PACKAGE_BANKPAY)));
            return true;
        }

        return false;
    }

}