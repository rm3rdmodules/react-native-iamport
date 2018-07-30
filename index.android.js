'use strict';

import React, {Component} from 'react';

import {requireNativeComponent, DeviceEventEmitter} from 'react-native';

const IAmPortViewManager = requireNativeComponent('IAmPortViewManager', null);

class IAmPort extends Component {

  componentDidMount() {

    DeviceEventEmitter.addListener('paymentEvent', this.paymentEvent.bind(this));
  }

  componentWillUnmount() {

    DeviceEventEmitter.removeAllListeners('paymentEvent');
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

  paymentEvent(e) {

    var url = e.result;
    var me = this;
    var original = e;

    //TODO delete
    console.log("paymentEvent", e);
    if (e.result == "success" || e.result == "failed") {
      this.props.onPaymentResultReceive(e);
    }

    var imp_uid = this.getParameterByName("imp_uid", url),
      merchant_uid = this.getParameterByName("merchant_uid", url),
      result = "";

    if (url.includes('success=false')) { // 취소 버튼을 눌렀거나 결제 실패시
      result = "failed"
    } else if (url.includes('success=true')) {
      result = "success";
    } else if (url.includes('payments/vbank')) {
      result = "vbank";
    }

    if (result) {
      this.props.onPaymentResultReceive({result, imp_uid, merchant_uid, original});
    }

    return true;
  }

  getRequestContent() {

    let params = this.props.params;
    const merchant_uid = params.merchant_uid || ('merchant_' + new Date().getTime());
    const m_redirect_url = params.m_redirect_url || (params.pg == 'paypal' ? 'https://service.iamport.kr/payments/success' : null);
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

          IMP.request_pay({
            pg : '${params.pg}',
            pay_method : '${params.pay_method}',
            merchant_uid : '${merchant_uid}',
            m_redirect_url : '${m_redirect_url}',
            app_scheme : '${params.app_scheme}',
            name : '${params.name}',
            amount : ${params.amount},
            buyer_email : '${params.buyer_email}',
            buyer_name : '${params.buyer_name}',
            buyer_tel : '${params.buyer_tel}',
            buyer_addr : '${params.buyer_addr}',
            buyer_postcode : '${params.buyer_postcode}',
            vbank_due : '${params.vbank_due}'
          }, function(rsp){

            if('${params.pg}' == 'nice'){
              return;
            }

            iamport.receiveResult(rsp.success ? "success" : "failed", rsp.imp_uid, rsp.merchant_uid);
          });
        </script>
      </body>
    </html>
    `;
    return HTML;
  }

  _onPaymentResultReceive(e) {
    // TODO: delete
    // console.log(e);

    if (this.props.onPaymentResultReceive) {

      this.props.onPaymentResultReceive(e);
    }
  }

  render() {

    return (
      <IAmPortViewManager {...this.props} source={this.getRequestContent()} pg={this.props.params.pg} appScheme={this.props.params.app_scheme}></IAmPortViewManager>
    );
  }
}

module.exports = IAmPort
