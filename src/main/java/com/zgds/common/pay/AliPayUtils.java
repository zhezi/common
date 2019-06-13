package com.zgds.common.pay;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.TradeStatus;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeQueryRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.model.result.AlipayF2FQueryResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.zgds.common.http.OkHttpUtils;

public class AliPayUtils {
	static Logger log = Logger.getLogger(OkHttpUtils.class);
	/**
	 *	当面付2.0生成支付二维码
	 * @param outTradeNo(必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，需保证商户系统端不能重复
	 * @param subject//订单标题
	 * @param body//订单描述
	 * @param totalAmount【订单总金额】=【打折金额】+【不可打折金额】
	 * @param notifyUrl
	 * @param alipayTradeService
	 * @return
	 */
    public static String getAlipayQrcode(AlipayTradeService alipayTradeService,
    		String outTradeNo,
    		String subject,
    		String body,
    		String totalAmount,
    		String notifyUrl) {
    	String qrcode="";
        String undiscountableAmount = "0";//打折金额
        String sellerId = "";// 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String operatorId = "test_operator_id";  // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String storeId = "test_store_id"; // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        ExtendParams extendParams = new ExtendParams(); // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        extendParams.setSysServiceProviderId("2088100200300400500");
        String timeoutExpress = "120m";// 支付超时，定义为120分钟

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
            .setSubject(subject)
            .setTotalAmount(totalAmount)
            .setOutTradeNo(outTradeNo)
            .setUndiscountableAmount(undiscountableAmount)
            .setSellerId(sellerId)
            .setBody(body)
            .setOperatorId(operatorId)
            .setStoreId(storeId)
            .setExtendParams(extendParams)
            .setTimeoutExpress(timeoutExpress)
            .setNotifyUrl(notifyUrl);

        AlipayF2FPrecreateResult result = alipayTradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
            	log.info("支付宝预下单成功: )");
                AlipayTradePrecreateResponse response = result.getResponse();
	            if (response != null) {
	            	log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
	                if (StringUtils.isNotEmpty(response.getSubCode())) {
	                	log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),response.getSubMsg()));
	                }
	                log.info("body:" + response.getBody());
	            }
//              log.info("tradeNo:"+response.getOutTradeNo());
	            log.info("qrcode:"+response.getQrCode());
	            qrcode=response.getQrCode();
                break;
            case FAILED:
            	log.error("支付宝预下单失败!!!");
                break;
            case UNKNOWN:
            	log.error("系统异常，预下单状态未知!!!");
                break;
            default:
            	log.error("不支持的交易状态，交易返回异常!!!");
                break;
        }
        return qrcode;
    }
	
	/**
     * 	支付宝查询订单
     */
    public static AlipayTradeQueryResponse tradeQuery(AlipayTradeService alipayTradeService,String outTradeNo) {
        AlipayTradeQueryRequestBuilder builder = new AlipayTradeQueryRequestBuilder().setOutTradeNo(outTradeNo);
        AlipayF2FQueryResult result = alipayTradeService.queryTradeResult(builder);
        if(TradeStatus.SUCCESS==result.getTradeStatus()){
        	 AlipayTradeQueryResponse response = result.getResponse();
        	 return response;
        }
        return null;
    }
}
