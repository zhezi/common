package com.zgds.common.wx;

public class WxQrcode {
	String ticket;
	long expire_seconds;
	String url;
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	public long getExpire_seconds() {
		return expire_seconds;
	}
	public void setExpire_seconds(long expire_seconds) {
		this.expire_seconds = expire_seconds;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
