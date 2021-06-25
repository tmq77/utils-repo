package cn.t.jwt.util.data;

/**
 * Token实体类
 */
public class Token {

	/**
	 * token
	 */
	private String token;

	/**
	 * 创建以及验证过程中的错误信息
	 */
	private String message;

	/**
	 * 是否有错误
	 */
	private boolean error;

	/**
	 * @param token    令牌
	 * @param message  信息
	 * @param error 是否包含错误
	 */
	public Token(String token, String message, boolean error) {
		super();
		this.token = token;
		this.message = message;
		this.error = error;
	}

	public String getToken() {
		return token;
	}

	public String getMessage() {
		return message;
	}

	public boolean isError() {
		return error;
	}

	@Override
	public String toString() {
		return "[" + this.message + "]";
	}

}
