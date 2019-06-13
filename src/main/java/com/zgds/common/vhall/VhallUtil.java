package com.zgds.common.vhall;

import java.io.File;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.springframework.util.DigestUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zgds.common.http.OkHttpUtils;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class VhallUtil {
	private static Logger log = Logger.getLogger(VhallUtil.class);
	public static String host="http://api.yun.vhall.com/api/v1";
	public static String AppID ="0d6a9f1e";
	public static String SecretKey ="0a0f579c415371db1bcb27c6dbeb8c31";
	
	private static Gson gson;
	
	public static Gson getGson() {
		if(gson==null)gson=new Gson();
		return gson;
	}
	
	/**
	 * 创建直播间
	 * @return
	 */
	public static VhallResult<RoomInfo> createRoom() {
		try {
			String api=host+"/room/create";
			Map<String,String> map=new HashMap<String,String>();
			map.put("app_id", AppID);
			map.put("signed_at", String.valueOf(new Date().getTime()));
			api+=sign(map);
			String json=OkHttpUtils.get(api);
			log.info(json);
			if(StringUtils.isEmpty(json))return null;
			VhallResult<RoomInfo> result=getGson().fromJson(json,new TypeToken<VhallResult<RoomInfo>>() {}.getType());
			return result;
		}catch (Exception e) {
			log.error(e);
			return null;
		}
	}
	
	/**
	 * 获取推流信息
	 * @param roomId房间ID
	 * @param expire_time过期时间, 格式: 2017/01/01 00:00:00
	 * @return
	 */
	public static VhallResult<PushInfo> getPushInfo(String roomId) {
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.HOUR, 8);
			Date date = calendar.getTime();
			String expireTime = DateFormatUtils.format(date, "yyyy/MM/dd HH:mm:ss");
			String api=host+"/room/get-push-info";
			Map<String,String> map=new HashMap<String,String>();
			map.put("app_id", AppID);
			map.put("signed_at", String.valueOf(new Date().getTime()));
			map.put("room_id", roomId);
			map.put("expire_time", expireTime);//默认8小时
			api+=sign(map);
			String json=OkHttpUtils.get(api);
			log.info(json);
			if(StringUtils.isEmpty(json))return null;
			VhallResult<PushInfo> result=getGson().fromJson(json,new TypeToken<VhallResult<PushInfo>>() {}.getType());
			return result;
		}catch (Exception e) {
			log.error(e);
			return null;
		}
	}
	
	private static String sign(Map<String,String> map) {
		String url=null;
		StringBuffer sb=new StringBuffer();
		for(String k:kSort(map).keySet()) {
			sb.append(k+map.get(k));
			if(url==null)url="?";else url+="&";
			url+=k+"="+map.get(k);
		}
		String sign=DigestUtils.md5DigestAsHex((SecretKey+sb.toString()+SecretKey).getBytes());
		url+="&sign="+sign;
		return url;
	}
	
	private static String getSign(Map<String,String> map) {
		StringBuffer sb=new StringBuffer();
		for(String k:kSort(map).keySet()) {
			sb.append(k+map.get(k));
		}
		String sign=DigestUtils.md5DigestAsHex((SecretKey+sb.toString()+SecretKey).getBytes());
		return sign;
	}
	
	private static Map<String, String> kSort(Map<String, String> data) {
		Map<String, String> treeMap = new TreeMap<String, String>(new Comparator<String>() {
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		treeMap.putAll(data);
		return treeMap;
	}
	
	/**
	 * 1 2 3 4 对应 左上、右上、右下和左下
	 * @param roomId
	 * @return
	 */
	public static VhallResult<WaterMark> createWatermark(File file) {
		try {
			String api=host+"/watermark/create";
			String signed_at=String.valueOf(new Date().getTime());
			Map<String,String> map=new HashMap<String,String>();
			map.put("app_id", AppID);
			map.put("signed_at",signed_at );
			map.put("watermark_name", "水印");
			map.put("watermark_positiontype", "2");
			String sign=getSign(map);
	        RequestBody requestBody = new MultipartBody.Builder()
	        	.setType(MultipartBody.FORM)
	        	.addFormDataPart("app_id", AppID)
	        	.addFormDataPart("signed_at", signed_at)
	        	.addFormDataPart("sign", sign)
	        	.addFormDataPart("watermark_name", "水印")
	        	.addFormDataPart("watermark_positiontype", "2")
	        	.addFormDataPart("watermark_image", file.getName(), RequestBody.create(MediaType.parse("application/image"), file))
	            .build();
	        String json=new OkHttpClient.Builder().build().newCall(new Request.Builder().url(api).post(requestBody).build()).execute().body().string();
			log.info(json);
			if(StringUtils.isEmpty(json))return null;
			VhallResult<WaterMark> result=getGson().fromJson(json,new TypeToken<VhallResult<WaterMark>>() {}.getType());
			return result;
		}catch (Exception e) {
			log.error(e);
			return null;
		}
	}
}
