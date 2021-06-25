package cn.t.jwt.util;

public class JwtStringUtil {
	
	/**
	 * trim之后进行判断是否为空
	 * @param str 判空字符串
	 * @return
	 */
	public static boolean isNotEmpty(String str) {
		if (str == null) {
			return false;
		}
		return str.trim().length() > 0;
	}

}
