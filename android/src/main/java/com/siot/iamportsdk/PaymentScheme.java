package com.siot.iamportsdk;

public class PaymentScheme {

	public final static String ISP = "ispmobile"; //	ISP모바일 : ispmobile://TID=nictest00m01011606281506341724
	public final static String BANKPAY = "kftc-bankpay";

	public final static String LOTTE_APPCARD = "lotteappcard"; 					//	롯데앱카드 : intent://lottecard/data?acctid=120160628150229605882165497397&apptid=964241&IOS_RETURN_APP=#Intent;scheme=lotteappcard;package=com.lcacApp;end
	public final static String HYUNDAI_APPCARD = "hdcardappcardansimclick"; 	//		현대앱카드 : intent:hdcardappcardansimclick://appcard?acctid=201606281503270019917080296121#Intent;package=com.hyundaicard.appcard;end;
	public final static String SAMSUNG_APPCARD = "mpocket.online.ansimclick"; 	//	삼성앱카드 : intent://xid=4752902#Intent;scheme=mpocket.online.ansimclick;package=kr.co.samsungcard.mpocket;end;
	public final static String NH_APPCARD = "nhappcardansimclick"; 				//	NH 앱카드 : intent://appcard?ACCTID=201606281507175365309074630161&P1=1532151#Intent;scheme=nhappcardansimclick;package=nh.smart.mobilecard;end;
	public final static String KB_APPCARD = "kb-acp"; 							//	KB 앱카드 : intent://pay?srCode=0613325&kb-acp://#Intent;scheme=kb-acp;package=com.kbcard.cxh.appcard;end;
	public final static String MOBIPAY = "cloudpay"; 							//	하나(모비페이) : intent://?tid=2238606309025172#Intent;scheme=cloudpay;package=com.hanaskcard.paycla;end;

	public final static String PACKAGE_ISP = "kvp.jjy.MispAndroid320";
	public final static String PACKAGE_BANKPAY = "com.kftc.bankpay.android";

	public final static String KAKAO = "kakaotalk";
	public final static String PACKAGE_KAKAO = "com.kakao.talk";
}
