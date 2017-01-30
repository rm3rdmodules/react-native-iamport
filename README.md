# react-native-iamport

리액트 네이티브용 아임포트 모듈입니다.

# 설치

```
npm install --save
```

```javascript
import React, {Component} from 'react';

import { View } from 'react-native';

import IAmPort from 'react-native-iamport';

export default class Payment extends Component {

  _onPaymentResultReceive(response) {

    if (response.result == "success") {

	  //성공시의 로직
    } else {

      // 실패시의 로직
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
