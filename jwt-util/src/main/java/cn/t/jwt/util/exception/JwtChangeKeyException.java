package cn.t.jwt.util.exception;

/**
 * 更换公钥/私钥失败异常
 * @author TMQ
 *
 */
public class JwtChangeKeyException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1743000604371300222L;
	
	public JwtChangeKeyException() {
		super();
	}
	
	/**
	 * 
	 * @param message 消息
	 */
	public JwtChangeKeyException(String message) {
		super(message);
	}
	
	/**
	 * 
	 * @param cause 原因
	 */
	public JwtChangeKeyException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * 
	 * @param message 消息
	 * @param cause 原因
	 */
	public JwtChangeKeyException(String message, Throwable cause) {
		super(message);
	}

}
