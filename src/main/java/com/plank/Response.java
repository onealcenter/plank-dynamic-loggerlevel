package com.plank;

public class Response {
	private String code;
	private String errMsg;
	private Object data;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Response success() {
		this.code = "0";
		return this;
	}

	public Response success(Object data) {
		this.code = "0";
		this.data = data;
		return this;
	}

	public Response success(Object data, Object perf) {
		this.code = "0";
		this.data = data;
		return this;
	}

	public Response failure(String errCode, String errMsg) {
		this.code = errCode;
		this.errMsg = errMsg;
		return this;
	}

	public Response failure(String errCode, String errMsg, Object errData) {
		this.code = errCode;
		this.errMsg = errMsg;
		this.data = errData;
		return this;
	}
}