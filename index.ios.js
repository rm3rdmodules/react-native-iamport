import React, { Component } from 'react';

import { WebView } from 'react-native';

export default class IAmPort extends Component {

  constructor(props) {

    super(props);
  }

  getRequestContent() {

    let params = this.props.params;
    const merchant_uid = params.merchant_uid || ('merchant_' + new Date().getTime());
    let HTML = `
    <!DOCTYPE html>
    <html>
      <head>
        <title>i'mport react native payment module</title>
        <meta http-equiv="content-type" content="text/html; charset=utf-8">
      </head>
      <body>
        <script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js" ></script>
        <script type="text/javascript" src="https://service.iamport.kr/js/iamport.payment-1.1.5.js"></script>
        <script type="text/javascript">
          var IMP = window.IMP;
          IMP.init('${params.code}');

          var params = {
            pg : '${params.pg}',
            pay_method : '${params.pay_method}',
            merchant_uid : '${merchant_uid}',
            m_redirect_url : '${params.app_scheme}://success',
            app_scheme : '${params.app_scheme}',
            name : '${params.name}',
            amount : ${params.amount},
            buyer_email : '${params.buyer_email}',
            buyer_name : '${params.buyer_name}',
            buyer_tel : '${params.buyer_tel}',
            buyer_addr : '${params.buyer_addr}',
            buyer_postcode : '${params.buyer_postcode}',
            vbank_due : '${params.vbank_due}',
            kakaoOpenApp : ${params.pg === "kakaopay"}
          };
          if('${params.pg}' == 'nice') {
            params['niceMobileV2'] = true;
          }

          IMP.request_pay(params, function(rsp) {
           if('${params.pg}' == 'nice') {
            return;
           }

           window.postMessage(JSON.stringify(rsp));
         });
        </script>
      </body>
    </html>
    `;
    return HTML;
  }

  getParameterByName(name, url) {

    if (!url) {
      url = window.location.href;
    }
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
      results = regex.exec(url);
    if (!results)
      return null;
    if (!results[2])
      return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
  }

  _onMessage(e) {

    console.log('_onMessage native event', e.nativeEvent);
    const { data, action } = e.nativeEvent;
    const { params, onPaymentResultReceive } = this.props;

    if(params.pg !== "kakaopay") {

      //iamport 내부적으로 사용되는 다른 action 들이 추가로 더 있기 때문에 done 타입의 action이 아닌 경우 onMessage를 무시해야 함.
      if ( action !== "done" ) return;
    }

    if (!data) {
      return;
    }

    const res = JSON.parse(data);
    const { success, request_id, imp_uid, merchant_uid, error_msg } = res;
    const result = success ? "success" : "cancel";
    console.log('_onMessage', result);

    onPaymentResultReceive({ result, imp_uid, merchant_uid });
  }

  _onShouldStartLoadWithRequest(e) {

    var url = e.url;
    var me = this;

    console.log("onShouldStartLoadWithRequest", e);

    var imp_uid = this.getParameterByName("imp_uid", url),
      merchant_uid = this.getParameterByName("merchant_uid", url),
      result = "";

    if (url.includes('imp_success=false')) { // 취소 버튼을 눌렀거나 결제 실패시
      result = "failed"
    } else if (url.indexOf(this.props.params.app_scheme + '://success') == 0) {
      result = "success";
    } else if (url.indexOf(this.props.params.app_scheme + '://cancel') == 0) {
      result = "canceled";
    }
    console.log("onShouldStartLoadWithRequest result", result);

    if (result) {
      this.props.onPaymentResultReceive({result, imp_uid, merchant_uid});
    }

    return true;
  }

  injectPostMessageFetch() {

    const patchPostMessageFunction = function () {
      var originalPostMessage = window.postMessage;

      var patchedPostMessage = function (message, targetOrigin, transfer) {
        originalPostMessage(message, targetOrigin, transfer);
      };

      patchedPostMessage.toString = function () {
        return String(Object.hasOwnProperty).replace('hasOwnProperty', 'postMessage');
      };

      window.postMessage = patchedPostMessage;
    };

    return '(' + String(patchPostMessageFunction) + ')();';
  }

  render() {
    return (
      <WebView
        {...this.props}
        originWhitelist={['http://*', 'https://*', `${this.props.params.app_scheme}://*`]}
        source={{ html: this.getRequestContent() }}
        startInLoadingState={true}
        injectedJavaScript={this.injectPostMessageFetch()}
        onMessage={this._onMessage.bind(this)}
        onShouldStartLoadWithRequest={this._onShouldStartLoadWithRequest.bind(this)}
        renderError={(e) => {
          return null;
        }}
        style={this.props.style} />
    );
  }
}

module.exports = IAmPort;
