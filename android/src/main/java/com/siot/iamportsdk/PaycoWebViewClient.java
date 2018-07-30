package com.siot.iamportsdk;

import java.net.URISyntaxException;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.util.Log;

import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import com.jeongjuwon.iamport.UrlLoadingCallBack;

public class PaycoWebViewClient extends WebViewClient {

	private Activity activity;
	UrlLoadingCallBack mCallBack;

	public PaycoWebViewClient(Activity activity, WebView target, UrlLoadingCallBack callBack) {
		this.activity = activity;
		this.mCallBack = callBack;
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {

	  // TODO: emit event
	  Log.i("iamport", "PaycoWebViewClient.shouldOverrideUrlLoading: " + url);
		mCallBack.shouldOverrideUrlLoadingCallBack(url);

		if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("javascript:")) {
			Intent intent = null;

			try {
				intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME); //IntentURI처리
				Uri uri = Uri.parse(intent.getDataString());

				Log.e("iamport", uri.toString());

				activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
				return true;
			} catch (URISyntaxException ex) {
				return false;
			} catch (ActivityNotFoundException e) {
				if ( intent == null )	return false;

				String packageName = intent.getPackage(); //packageName should be com.kakao.talk
		        if (packageName != null) {
		            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
		            return true;
		        }

		        return false;
			}
		}

		return false;
	}
}
