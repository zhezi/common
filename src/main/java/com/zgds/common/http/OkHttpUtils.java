package com.zgds.common.http;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.apache.log4j.Logger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtils {

	static Logger log = Logger.getLogger(OkHttpUtils.class);

	private static OkHttpClient client = null;
	public final static int CONNECT_TIMEOUT = 100;
	public final static int READ_TIMEOUT = 100;
	public final static int WRITE_TIMEOUT = 100;

	private static OkHttpClient getInstance() {
		if (client == null)
			client = new OkHttpClient.Builder()
					.hostnameVerifier(new HostnameVerifier() {
						@Override
						public boolean verify(String hostname, SSLSession session) {
							return true;
						}
					})
					.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)// 设置读取超时时间
					.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)// 设置写的超时时间
					.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)// 设置连接超时时间
					.build();
		return client;
	}
	public static String get(String url) throws IOException {
		Response response = getInstance().newCall(new Request.Builder().url(url).get().build()).execute();
		String json = response.body().string();
		return json;
	}
 
	public static String post(String url, RequestBody body) throws IOException {
		Response response = getInstance().newCall(new Request.Builder().url(url).post(body).build()).execute();
		String json = response.body().string();
		return json;
	}

	public static String call(Request request)throws IOException{
		Response response = getInstance().newCall(request).execute();
		String content=response.body().string();
		log.info(content);
		return content;
	}
}
