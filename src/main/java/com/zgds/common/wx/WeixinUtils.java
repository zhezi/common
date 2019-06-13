package com.zgds.common.wx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.log4j.Logger;
import com.google.gson.Gson;
import com.zgds.common.http.OkHttpUtils;

public class WeixinUtils {
	private static Logger log = Logger.getLogger(WeixinUtils.class);
	private static String appid="wx3570bbcdf5e9a033";
	private static String secret="eebd3756d1ddc2cb28a0f5fd2b19123b";
	private static Gson gson=null;
	public static Gson getGson() {
		if(gson==null)gson= new Gson();
		return gson;
	}
	
	/**
	 * snsapi_base[需关注公众号，不弹出授权页面，直接跳转，只能获取用户openid]
	 * snsapi_userinfo[不用关注公众号，弹出授权页面，可通过openid拿到昵称、性别、所在地]
	 * @param uri
	 * @return
	 */
	public static String authorize(String uri) {
		return "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appid+"&redirect_uri="+uri+"&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
	}
	
	/**
	 * snsapi_login
	 * @param uri
	 * @return
	 */
	public static String login(String uri) {
		return "https://open.weixin.qq.com/connect/qrconnect?appid="+appid+"&redirect_uri="+uri+"&response_type=code&scope=snsapi_login&state=STATE#wechat_redirect";
	}
	
	
	/**
	   *   带参数二维码的使用 
	 * @param access_token
	 * @return
	 * @throws IOException
	 */
	public static WxQrcode createQrcode(String access_token) throws IOException {
		String url="https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token="+access_token;
		String json="{\"expire_seconds\": 604800, \"action_name\": \"QR_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": 123}}}";
//		String json="{\"action_name\": \"QR_LIMIT_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": 123}}}";
		String res=post(url, json);
		System.out.println(res);
		WxQrcode result=getGson().fromJson(res, WxQrcode.class);
		return result;
	}
	
	public static void showQrcode(String ticket) {
		try {
			String url="https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket="+ticket;
			String json=OkHttpUtils.get(url);
			log.info(json);
		}catch (Exception e) {
			log.error(e);
		}
	}
	
	public static WxToken getSnsAccessToken(String code) {
		try {
			String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appid+"&secret="+secret+"&code="+code+"&grant_type=authorization_code";
			String json=OkHttpUtils.get(url);
			WxToken result=getGson().fromJson(json, WxToken.class);
			return result;
		}catch (Exception e) {
			log.error(e);
			return null;
		}
	}
	
	public static WxUserInfo getSnsUserInfo(String access_token,String openid) {
		try {
			String url ="https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token+ "&openid=" + openid+ "&lang=zh_CN";
			String json=OkHttpUtils.get(url);
			System.out.println(json);
			WxUserInfo result=getGson().fromJson(json,  WxUserInfo.class);
			return result;
		}catch (Exception e) {
			log.error(e);
			return null;
		}
	}
	
	public static WxToken getAccessToken() {
		try {
			String url="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appid+"&secret="+secret;
			String json=OkHttpUtils.get(url);
			System.out.println(json);
			WxToken result=getGson().fromJson(json, WxToken.class);
			return result;
		}catch (Exception e) {
			log.error(e);
			return null;
		}
	}
	
	public static WxOpenId getUserList(String access_token) {
		try {
			String url="https://api.weixin.qq.com/cgi-bin/user/get?access_token="+access_token+"&next_openid=";//+next_openid;
			String json=OkHttpUtils.get(url);
			WxOpenId result=getGson().fromJson(json,  WxOpenId.class);
			System.out.println(json);
			return result;
		}catch (Exception e) {
			log.error(e);
			return null;
		}
	}
	
	public static WxUserInfo getUserInfo(String access_token,String openid) {
		try {
			String url="https://api.weixin.qq.com/cgi-bin/user/info?access_token="+access_token+"&openid="+openid+"&lang=zh_CN";
			String json=OkHttpUtils.get(url);
			System.out.println(json);
			WxUserInfo result=getGson().fromJson(json,  WxUserInfo.class);
			return result;
		}catch (Exception e) {
			log.error(e);
			return null;
		}
	}
	
	public static String post(String path, String postContent) {
        URL url = null;
        try {
            url = new URL(path);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.connect();
            OutputStream os=httpURLConnection.getOutputStream();
            os.write(postContent.getBytes("UTF-8"));
            os.flush();
            StringBuilder sb = new StringBuilder();
            int httpRspCode = httpURLConnection.getResponseCode();
            if (httpRspCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
	
	public static void main(String[] args) throws Exception{
//		WxToken token=getSnsAccessToken(code);
//		System.out.println(token);
//		String access_token=token.getAccess_token();
//		String access_token="20_BwFSI5GdJwekFuMVYDML2qX97FLfJ1C9J6WnrWupzCzX-X3-BlHokjkgr9Rq_Z8j8fLpIMtobvptObC2iReQyfoB2SpxrwU4-6E5_t0XHHwsF1E0pKKfVj_moJW__QX1G4a8U09yinlhFKG-FGGaACASTT";
//		System.out.println(access_token);
//		createQrcode(access_token);
//		String ticket="gQG48TwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyZ19hWEVBUkNkcmoxMDAwME0wM0cAAgQIisFcAwQAAAAA";
//		showQrcode(ticket);
//		OpenIdBase result=getUserList(access_token);
//		for(String openid:result.getData().getOpenid()) {
//			getUserInfo(access_token, openid);
//		}
//		String xml="<xml><ToUserName><![CDATA[toUser]]></ToUserName><FromUserName><![CDATA[FromUser]]></FromUserName><CreateTime>123456789</CreateTime><MsgType><![CDATA[event]]></MsgType><Event><![CDATA[subscribe]]></Event><EventKey><![CDATA[qrscene_123123]]></EventKey><Ticket><![CDATA[TICKET]]></Ticket></xml>";
	}
}
