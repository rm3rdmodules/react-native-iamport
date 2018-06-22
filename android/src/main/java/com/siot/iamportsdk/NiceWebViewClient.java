package com.siot.iamportsdk;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.util.Log;
import com.jeongjuwon.iamport.UrlLoadingCallBack;

public class NiceWebViewClient extends WebViewClient {

	private Activity activity;
	private WebView target;
	private String BANK_TID = "";
	UrlLoadingCallBack mCallBack;

	final int RESCODE = 1;
	final String NICE_URL = "https://web.nicepay.co.kr/smart/interfaceURL.jsp";			// NICEPAY SMART 요청 URL
	final String NICE_BANK_URL = "https://web.nicepay.co.kr/smart/bank/payTrans.jsp";	// 계좌이체 거래 요청 URL
	final String KTFC_PACKAGE = "com.kftc.bankpay.android";

	public NiceWebViewClient(Activity activity, WebView target, UrlLoadingCallBack callBack) {
		this.activity = activity;
		this.target = target;
		this.mCallBack = callBack;
	}

	public void bankPayPostProcess(String bankpayCode, String bankpayValue) {
		String postData = "callbackparam2="+BANK_TID+"&bankpay_code="+bankpayCode+"&bankpay_value="+bankpayValue;
		target.postUrl(NICE_BANK_URL,EncodingUtils.getBytes(postData,"euc-kr"));
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {

	  // TODO: emit event
	  Log.i("iamport", "NiceWebViewClient.shouldOverrideUrlLoading: " + url);
		mCallBack.shouldOverrideUrlLoadingCallBack(url);

		if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("javascript:")) {
			Intent intent = null;

			try {
				/* START - BankPay(실시간계좌이체)에 대해서는 예외적으로 처리 */
				if ( url.startsWith(PaymentScheme.BANKPAY) ) {
					try {
						String reqParam = makeBankPayData(url);

						intent = new Intent(Intent.ACTION_MAIN);
	                    intent.setComponent(new ComponentName("com.kftc.bankpay.android","com.kftc.bankpay.android.activity.MainActivity"));
	                    intent.putExtra("requestInfo",reqParam);
	                    activity.startActivityForResult(intent,RESCODE);

	                    return true;
					} catch (URISyntaxException e) {
						return false;
					}
				}
				/* END - BankPay(실시간계좌이체)에 대해서는 예외적으로 처리 */

				intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME); //IntentURI처리
				Uri uri = Uri.parse(intent.getDataString());

				activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
				return true;
			} catch (URISyntaxException ex) {
				return false;
			} catch (ActivityNotFoundException e) {
				if ( intent == null )	return false;

				if ( handleNotFoundPaymentScheme(intent.getScheme()) )	return true;

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
	 *
	 * 결제를 위한 3rd-party 앱이 아직 설치되어있지 않아 ActivityNotFoundException이 발생하는 경우 처리합니다.
	 * 여기서 handler되지않은 scheme에 대해서는 intent로부터 Package정보 추출이 가능하다면 다음에서 packageName으로 market이동합니다.
	 *
	 */
	protected boolean handleNotFoundPaymentScheme(String scheme) {
		//PG사에서 호출하는 url에 package정보가 없어 ActivityNotFoundException이 난 후 market 실행이 안되는 경우
		if ( PaymentScheme.ISP.equalsIgnoreCase(scheme) ) {
			activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PaymentScheme.PACKAGE_ISP)));
			return true;
		} else if ( PaymentScheme.BANKPAY.equalsIgnoreCase(scheme) ) {
			activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PaymentScheme.PACKAGE_BANKPAY)));
			return true;
		}

		return false;
	}

	private String makeBankPayData(String url) throws URISyntaxException {
		BANK_TID = "";
		List<NameValuePair> params = URLEncodedUtils.parse(new URI(url), "UTF-8");

		StringBuilder ret_data = new StringBuilder();
		List<String> keys = Arrays.asList(new String[] {"firm_name", "amount", "serial_no", "approve_no", "receipt_yn", "user_key", "callbackparam2", ""});

		String k,v;
		for (NameValuePair param : params) {
			k = param.getName();
			v = param.getValue();

			if ( keys.contains(k) ) {
				if ( "user_key".equals(k) ) {
					BANK_TID = v;
				}
				ret_data.append("&").append(k).append("=").append(v);
			}
		}

		ret_data.append("&callbackparam1="+"nothing");
		ret_data.append("&callbackparam3="+"nothing");

    	return ret_data.toString();
	}

}
