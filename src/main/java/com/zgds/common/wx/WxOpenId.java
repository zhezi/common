package com.zgds.common.wx;

public class WxOpenId {
	int total;
	int count;
	WxOpenIdList data;
	String next_openid;
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public WxOpenIdList getData() {
		return data;
	}
	public void setData(WxOpenIdList data) {
		this.data = data;
	}
	public String getNext_openid() {
		return next_openid;
	}
	public void setNext_openid(String next_openid) {
		this.next_openid = next_openid;
	}
}
