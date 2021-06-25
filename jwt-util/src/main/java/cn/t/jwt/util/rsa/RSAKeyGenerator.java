package cn.t.jwt.util.rsa;

import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * https://blog.csdn.net/Aeve_imp/article/details/101217466 <br>
 * RSA公钥密钥生成器<br>
 * 每个对象都是一组公钥密钥对
 * 
 * @author tmq
 *
 */
public class RSAKeyGenerator {

	/**
	 * RSA加解密算法
	 */
	public static final String KEY_ALGORITHM = "RSA";

	/**
	 * 默认编码
	 */
	public static final String CHARSET = "UTF-8";

	/**
	 * 
	 * 生成公钥私钥对<br>
	 * 私钥加密，持有私钥或公钥才可以解密<br>
	 * 公钥加密，持有私钥才可解密<br>
	 * 私钥存在服务器<br>
	 * 公钥存在各个服务中,即使公钥泄露,使用公钥加密的数据无法使用公钥解密，只能用服务器的私钥解密，所以认证服务器使用私钥加密下发令牌
	 * 
	 * @throws NoSuchAlgorithmException     加密算法不存在
	 * @throws UnsupportedEncodingException 字符类型不支持
	 * @return RSA公钥私钥对
	 */
	public static RSAKeyPair generate() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		// 创建加密算法实例
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		// 非对称加密存在加密数据限制问题（即使用非对称密钥加密数据时，一次加密的数据长度是（密钥长度／8-11）
		// 本程序中，密钥长度是1024( keyPairGenerator.initialize(1024);程序中这里指定了)，
		// 那么加密内容应该不得超过1024/8-11=117，解密要求密文最大长度为1024/8=128字节，如果需要加密的数据长度大于117，则会报错：Data
		// must not be longer than 117 bytes
		// 注意:加密内容越大，则加解密越慢
		keyPairGenerator.initialize(1024);
		// 使用RSA生成器获取 公钥/密钥对实例
		KeyPair keyPair = keyPairGenerator.generateKeyPair();

		// 公钥
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		// 私钥
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

		return new RSAKeyPair(new String(Base64.getEncoder().encode(publicKey.getEncoded()), CHARSET),
				new String(Base64.getEncoder().encode(privateKey.getEncoded()), CHARSET), publicKey, privateKey);
	}

	/**
	 * 将公钥字符串转化为公钥对象
	 * 
	 * @param publicKey 公钥字符串
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 **/
	public static RSAPublicKey convertToPublicKey(String publicKey)
			throws UnsupportedEncodingException, InvalidKeySpecException, NoSuchAlgorithmException {
		// base64编码的公钥
		byte[] keyBytes = Base64.getDecoder().decode(publicKey.getBytes(CHARSET));
		RSAPublicKey rsaPublicKey = (RSAPublicKey) KeyFactory.getInstance(KEY_ALGORITHM)
				.generatePublic(new X509EncodedKeySpec(keyBytes));
		// 生成私钥对象需要使用PKCS8EncodedKeySpec类
		return rsaPublicKey;
	}

	/**
	 * 将私钥字符串转化为私钥对象
	 * 
	 * @param privateKey 私钥字符串
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 **/
	public static RSAPrivateKey convertToPrivateKey(String privateKey)
			throws UnsupportedEncodingException, InvalidKeySpecException, NoSuchAlgorithmException {
		// base64编码的公钥
		byte[] keyBytes = Base64.getDecoder().decode(privateKey.getBytes(CHARSET));
		RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) KeyFactory.getInstance(KEY_ALGORITHM)
				.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
		return rsaPrivateKey;
	}

}
