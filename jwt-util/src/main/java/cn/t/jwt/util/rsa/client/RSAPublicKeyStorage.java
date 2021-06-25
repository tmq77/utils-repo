package cn.t.jwt.util.rsa.client;

import cn.t.jwt.util.data.JwtCustomClaim;

/**
 * 微服务RSA公钥存储器
 * 
 * @author TMQ
 *
 */
public class RSAPublicKeyStorage {

	/** 常驻内存的RSA公钥对象 */
	private static String KEY_STORE;

	/** 验证Token的必须信息 */
	private static JwtCustomClaim<Object> CLAIM_STORE;

	/**
	 * 装载存储器
	 * 
	 * @param newKey
	 */
	public static void loadStorage(String newKey, JwtCustomClaim<Object> claimStore) {
		KEY_STORE = newKey;
		CLAIM_STORE = claimStore;
	}

	/**
	 * 公钥对象,使用公钥解密Token
	 * 
	 * @return
	 */
	public static String getSavedPublicKey() {
		return KEY_STORE;
	}

	/**
	 * 解析Token的必要信息
	 * 
	 * @return
	 */
	public static JwtCustomClaim<Object> getSavedClaim() {
		return CLAIM_STORE;
	}
}
