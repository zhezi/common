package com.zgds.common.pay;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.github.wxpay.sdk.WXPayConfig;

public class WXPayUtils {
	
	/**
	 * 扫码支付
	 * @param config
	 * @param out_trade_no
	 * @param body
	 * @param total_fee
	 * @param spbill_create_ip
	 * @param notify_url
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> nativeOrder(WXPayConfig config,String out_trade_no,String body,Integer total_fee,String spbill_create_ip, String notify_url) throws Exception {
		Map<String, String> param = new HashMap<String, String>();
		param.put("appid", config.getAppID()); // 公众账号ID
		param.put("mch_id", config.getMchID()); // 商户号
		param.put("nonce_str", WXPayUtil.generateNonceStr()); // 
		param.put("body", body); // 内容(随意填,具体参照微信官方文档)
		param.put("out_trade_no", out_trade_no); // 订单号
		param.put("total_fee", total_fee+""); // 支付价格.<注意：这里的价格单位是分,必须是整数,不能带小数点的>
		param.put("spbill_create_ip", spbill_create_ip); // IP地址 ַ
		param.put("notify_url", notify_url);
		param.put("trade_type", "NATIVE");
		WXPay wei = new WXPay(config);
		Map<String, String> resp=wei.unifiedOrder(param);
		return resp;
	}
	/**
	 * Jsapi
	 * @param config
	 * @param out_trade_no
	 * @param body
	 * @param total_fee
	 * @param spbill_create_ip
	 * @param notify_url
	 * @param openid
	 * @return
	 * @throws Exception
	 */
	public static Map<String,String> jsapiOrder(WXPayConfig config,String out_trade_no,String body,Integer total_fee,String spbill_create_ip, String notify_url,String openid) throws Exception{
        WXPay wxpay = new WXPay(config);

        Map<String, String> data = new HashMap<String, String>();
        data.put("appid", config.getAppID());//公众账号id
        data.put("mch_id", config.getMchID()); //商户号
        
        data.put("body", body);
        data.put("out_trade_no", out_trade_no);
        data.put("device_info", "");
        data.put("fee_type", "CNY");
        data.put("total_fee", String.valueOf(total_fee));
        data.put("spbill_create_ip", spbill_create_ip);
        data.put("notify_url", notify_url);
        data.put("trade_type", "JSAPI");
        data.put("product_id", "12");
        data.put("openid", openid);
        Map<String, String> resp = wxpay.unifiedOrder(data);
        return resp;
	}
 
    public Map<String, String> buildData2JSAPI(WXPayConfig config,String prepay_id) throws Exception {
    	TreeMap<String, String> result = new TreeMap<String,String>();
        result.put("appId", config.getAppID());
        result.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000L));
        result.put("nonceStr", WXPayUtil.generateNonceStr());
        result.put("package", "prepay_id=" + prepay_id);
        result.put("signType", WXPayConstants.HMACSHA256);
        result.put("paySign", WXPayUtil.generateSignature(result, config.getKey(), WXPayConstants.SignType.HMACSHA256));
        return result;
    }
}
