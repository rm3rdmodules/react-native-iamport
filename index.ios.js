import React, {Component} from 'react';

import {WebView} from 'react-native';

export default class IAmPort extends Component {

  constructor(props) {

    super(props);
  }

  getRequestContent() {

    let params = this.props.params;
    let HTML = `
    <!DOCTYPE html>
    <html>
      <head>
        <title>i'mport react native payment module</title>
        <meta http-equiv="content-type" content="text/html; charset=utf-8">
      </head>
      <body>
        <script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js" ></script>
        <script type="text/javascript" src="https://service.iamport.kr/js/iamport.payment-1.1.4.js"></script>
        <script type="text/javascript">
          var IMP = window.IMP;
          IMP.init('` + params.code + `');

          IMP.request_pay({
            pg : '` + params.pg + `',
            pay_method : '` + params.pay_method + `',
            merchant_uid : '` + 'merchant_' + new Date().getTime() + `',
            m_redirect_url : '` + params.app_scheme + `://success',
            app_scheme : '` + params.app_scheme + `',
            name : '` + params.name + `',
            amount : ` + params.amount + `,
            buyer_email : '` + params.buyer_email + `',
            buyer_name : '` + params.buyer_name + `',
            buyer_tel : '` + params.buyer_tel + `',
            buyer_addr : '` + params.buyer_addr + `',
            buyer_postcode : '` + params.buyer_postcode + `'
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

  _onShouldStartLoadWithRequest(e) {

    var url = e.url;
    var me = this;

    console.log("onShouldStartLoadWithRequest", url);

    var imp_uid = this.getParameterByName("imp_uid", url),
      merchant_uid = this.getParameterByName("merchant_uid", url),
      result = "";

    if (url.indexOf(this.props.params.app_scheme + '://success') == 0) {

      result = "success";
    } else if (url.indexOf(this.props.params.app_scheme + '://cancel') == 0) {

      result = "cancel";
    } else if (url.indexOf('https://service.iamport.kr/kakao_payments/success') == 0) {

      result = "success";
    } else if (url.indexOf('https://service.iamport.kr/kakao_payments/cancel') == 0) {

      result = "cancel";
    } else if (url.indexOf('https://service.iamport.kr/payco_payments/success') == 0) {

      result = "success";
    } else if (url.indexOf('https://service.iamport.kr/payco_payments/cancel') == 0) {

      result = "cancel";
    }

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
      <WebView {...this.props} source={{
        html: this.getRequestContent()
      }} startInLoadingState={true} injectedJavaScript={this.injectPostMessageFetch()} onShouldStartLoadWithRequest={this._onShouldStartLoadWithRequest.bind(this)} style={this.props.style}></WebView>
    );
  }
}

module.exports = IAmPort;
