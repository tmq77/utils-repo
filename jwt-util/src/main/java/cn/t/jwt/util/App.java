package cn.t.jwt.util;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

import cn.t.jwt.util.data.JwtCustomClaim;
import cn.t.jwt.util.data.Token;
import cn.t.jwt.util.rsa.RSAKeyGenerator;
import cn.t.jwt.util.rsa.RSAKeyPair;
import cn.t.jwt.util.rsa.client.RSAPublicKeyStorage;
import cn.t.jwt.util.rsa.server.RSAKeyStorage;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args)
			throws InterruptedException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeySpecException {
//        Token token = JwtUtil.createTokenByHS256(1200, "tmq");
//        System.out.println(token.getToken());
//        Thread.sleep(1100);
//        System.out.println(JwtUtil.verifyTokenByHS256(token.getToken(), "tmq"));

		// 认证中心装载密钥并返回公钥字符串，此时认证中心已经保存了公钥和私钥
		String pubKey = RSAKeyStorage.loadStorage();

		// 创建验证Token的必须信息
		long ts = System.currentTimeMillis();
		long expired = ts + 2000l; // 2秒后过期  验证工具中预留了1秒的窗口  实际上时3秒过期
		JwtCustomClaim<Object> claims = new JwtCustomClaim.Builder<Object>("tmq", "测试", "t").issuedAt(new Date(ts))
				.expiresAt(new Date(expired)).build();
		// 创建Token
		Token token = JwtUtil.createTokenByRSA256(claims, RSAKeyStorage.getSavedKeyPair());
		// 成功后将 公钥和验证必须信息存入客户端
		RSAPublicKeyStorage.loadStorage(pubKey, claims);

		////////测试///////
		System.out.println(ts);
		System.out.println("Token创建状态:" + token.getMessage());
		System.out.println("Token:" + token.getToken());
		
		// 解析
		Thread.sleep(1500);
		System.out.println("将公钥字符串转化为公钥对象-----");
		RSAPublicKey publicKey = RSAKeyGenerator.convertToPublicKey(RSAPublicKeyStorage.getSavedPublicKey());
		System.out.println("公钥对象转化成功-----");
		System.out.println("解析Token开始-----");
		System.out.println(JwtUtil.verifyTokenByRSA256(token.getToken(), RSAPublicKeyStorage.getSavedClaim(), publicKey));

	}
}
