package cn.t.jwt.util.rsa.server;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;

import cn.t.jwt.util.exception.JwtChangeKeyException;
import cn.t.jwt.util.rsa.RSAKeyGenerator;
import cn.t.jwt.util.rsa.RSAKeyPair;

/**
 * 认证中心RSA私钥存储器<br>
 * 功能: 存储公钥私钥, 更新公钥私钥<br>
 * 使用时需要调用loadStorage方法
 * 
 * @author TMQ
 *
 */
public class RSAKeyStorage {

	/** 常驻内存的RSA公钥/私钥对象 */
	private static RSAKeyPair KEY_STORE ;
	
	/** 
	 * 加载存储器，生成密钥对并返回公钥
	 * @return 公钥字符串
	 */
	public static String loadStorage() {
		return changeKey();
	}
	
	/**
	 * 公钥/私钥对象,使用私钥加密Token
	 * 
	 * @return
	 */
	public static RSAKeyPair getSavedKeyPair() {
		return KEY_STORE;
	}

	/**
	 * 切换内存中的公钥/私钥
	 * @return 公钥,同时需要通知各个服务更新公钥
	 */
	public static String changeKey() {
		try {
			KEY_STORE = RSAKeyGenerator.generate();
			return KEY_STORE.getPublicKey();
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new JwtChangeKeyException(e.getMessage());
		}
	}
}
