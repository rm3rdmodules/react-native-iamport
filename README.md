# react-native-iamport

리액트 네이티브용 아임포트 모듈입니다. 필요에 의해 나이스페이와 카카오페이, 페이코만 구현하였습니다. 안드로이드에서의 리액트네이티브 웹뷰는 이벤트 핸들링이 뜻대로 되질 않아 직접 안드로이드로 모듈 구현하였습니다. 필요에 따라 더 구현을 해서 사용하실 분들을 위해 공개합니다.

# Installation

```
npm install --save react-native-iamport
react-native link react-native-iamport
```

# Example

```javascript
import React, {Component} from 'react';

import { View } from 'react-native';

import IAmPort from 'react-native-iamport';

export default class Payment extends Component {

  _onPaymentResultReceive(response) {

    if (response.result == "success") {

	  //성공시의 로직
    } else {

      //실패시의 로직
    }
  }

  render() {

    return (
		<IAmPort onPaymentResultReceive={this._onPaymentResultReceive} params={{
	      code: "iamport",
		  pg: "nice",
	      pay_method: "card",
	      app_scheme: "yourscheme",
	      name: "주문명:결제테스트",
	      amount: 1000,
	      buyer_email: "iamport@siot.do",
	      buyer_name: "구매자",
	      buyer_tel: "010-1234-5678",
	      buyer_addr: "서울특별시 강남구 삼성동",
	      buyer_postcode: "123-456"
	    }}></IAmPort>
    )
  }
}
```

# Info.plist 파일에 내용 추가
```
<key>LSApplicationQueriesSchemes</key>
<array>
	<string>kakao0123456789abcdefghijklmn</string>
	<string>kakaokompassauth</string>
	<string>storykompassauth</string>
	<string>kakaolink</string>
	<string>kakaotalk</string>
	<string>kakaostory</string>
	<string>storylink</string>
	<string>payco</string>
	<string>kftc-bankpay</string>
	<string>ispmobile</string>
	<string>itms-apps</string>
	<string>hdcardappcardansimclick</string>
	<string>smhyundaiansimclick</string>
	<string>shinhan-sr-ansimclick</string>
	<string>smshinhanansimclick</string>
	<string>kb-acp</string>
	<string>mpocket.online.ansimclick</string>
	<string>ansimclickscard</string>
	<string>ansimclickipcollect</string>
	<string>vguardstart</string>
	<string>samsungpay</string>
	<string>scardcertiapp</string>
	<string>lottesmartpay</string>
	<string>lotteappcard</string>
	<string>cloudpay</string>
	<string>nhappvardansimclick</string>
	<string>nonghyupcardansimclick</string>
	<string>citispay</string>
	<string>citicardappkr</string>
	<string>citimobileapp</string>
	<string>itmss</string>
</array>
```
