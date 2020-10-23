package cn.t.mail.util.exceptions;

/**
 * 自定义异常
 * @author tmq
 *
 */
public class MailUtilException extends RuntimeException{

    public MailUtilException() {
        super();
    }

    public MailUtilException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public MailUtilException(String message, Throwable cause) {
        super(message, cause);
    }

    public MailUtilException(String message) {
        super(message);
    }

    public MailUtilException(Throwable cause) {
        super(cause);
    }


}
