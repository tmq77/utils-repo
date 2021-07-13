package cn.t.auth.response;

import org.springframework.http.HttpStatus;

public class AuthResponse {

	/**
	 * 状态码
	 */
	private int code;

	/** 状态码字符串 */
	private HttpStatus status;

	/** 消息 */
	private String msg;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
