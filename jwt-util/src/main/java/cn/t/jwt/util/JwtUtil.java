package cn.t.jwt.util;

import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import cn.t.jwt.util.data.JwtCustomClaim;
import cn.t.jwt.util.data.Token;
import cn.t.jwt.util.rsa.RSAKeyGenerator;
import cn.t.jwt.util.rsa.RSAKeyPair;

/**
 * http://www.leftso.com/blog/221.html <br>
 * https://github.com/auth0/java-jwt token生成验证工具类 提供: 基于HS256的对称加密解密
 * JWT版本:3.10.3
 */
public final class JwtUtil {
	
	/** 自定义盐值key */
	private static String CLAIM_KEY = "cn.t";
	
	/** 自定义盐值value */
	private static String CLAIM_VALUE = "tmq-2021-06-25";

	/**
	 * HS256静态密文
	 */
	private static String HS256_SECRET = "tmq-2020-09-09";

	/**
	 * 设置对称加密静态密文
	 * 
	 * @param 静态密文
	 */
	public static void setHs256Secret(String secret) {
		Optional.ofNullable(secret).ifPresent(s -> HS256_SECRET = s);
	}

	/**
	 * 使用HS256方式创建Token You'll first need to create a JWTCreator instance by calling
	 * JWT.create(). Use the builder to define the custom Claims your token needs to
	 * have. Finally to get the String token call sign() and pass the Algorithm
	 * instance.
	 * 
	 * <br>
	 * 测试用
	 * 
	 * @param expireTs 超时时间(millisecond)
	 * @param userId   用户Id,用于创建唯一ID（jti）
	 * @return 生成的Token
	 */
	@Deprecated
	public static Token createTokenByHS256(long expireTs, String userId) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(HS256_SECRET);

			long ts = System.currentTimeMillis();
			// 签发时间
			Date issuedAt = new Date(ts);
			// 过期时间
			Date expiresAt = new Date(ts + expireTs);
			Map<String, Object> headerClaims = new HashMap<>();
			headerClaims.put("owner", "tmq");

			String token = JWT.create().withJWTId(userId) // 针对当前token的唯一标识
					.withIssuer("cn.t.jwt") // 签发人
					// You can also verify custom Claims on the JWT.require() by calling withClaim()
					// and passing both the name and the required value.
					// 当使用jwt.create()创建一个令牌时，您可以通过调用withClaim()来指定自定义声明，并同时传递名称和值。
					.withClaim("cn.t.cust", "t").withArrayClaim("array", new Integer[] { 1, 2, 3 })
					// 当使用jwt.create()创建一个令牌时，您可以通过调用withHeader()来指定头声明，并同时传递声明的映射。
					.withHeader(headerClaims).withExpiresAt(expiresAt).withIssuedAt(issuedAt).sign(algorithm);
			// 使用Base64进行再加密
			return new Token(Base64.getEncoder().encodeToString(token.getBytes(RSAKeyGenerator.CHARSET)), "", false);
		} catch (JWTCreationException e) {
			// Invalid Signing configuration / Couldn't convert Claims.
			return new Token(null, e.getMessage(), true);
		} catch (Exception e) {
			return new Token(null, e.getMessage(), true);
		}
	}

	/**
	 * 使用HS256方式验证Token You'll first need to create a JWTVerifier instance by
	 * calling JWT.require() and passing the Algorithm instance. If you require the
	 * token to have specific Claim values, use the builder to define them. The
	 * instance returned by the method build() is reusable, so you can define it
	 * once and use it to verify different tokens. Finally call verifier.verify()
	 * passing the token.
	 */
	@Deprecated
	public static Token verifyTokenByHS256(String token, String userId) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(HS256_SECRET);
			// Reusable verifier instance
			JWTVerifier jWTVerifier = JWT.require(algorithm).withJWTId(userId).withIssuer("cn.t.jwt") // 验证签发者
					.withClaim("cn.t.cust", "t") // 验证自定义声明
					// 在JWTVerifier builder中使用accept回旋()方法，并传递一个正值的秒值 可以延长过期时间，创造回旋余地
					// .acceptLeeway(1) // 1 sec for nbf, iat and exp 
					// nbf:Not Before 的缩写，表示 JWT Token 在这个时间之前是无效的
					// iat：签发时间
					// exp： 过期时间
					// .acceptExpiresAt(5) // 5 secs for exp
					.build();
			// 验证,未抛出异常则认证通过
			// 使用BASE64解密再验证
			DecodedJWT jwt = jWTVerifier.verify(new String(Base64.getDecoder().decode(token), RSAKeyGenerator.CHARSET));
			// 可以使用jwt.getXXX获取Token中的各项信息
			System.out.println(jwt.getIssuer());

			return new Token(token, "", false);
		} catch (JWTVerificationException e) {
			// Invalid signature/claims
			return new Token(null, e.getMessage(), true);
		} catch (Exception e) {
			return new Token(null, e.getMessage(), true);
		}
	}

	///////////////////////////////////////////////////////////////////////////////
	/**
	 * 使用RSA256方式创建Token
	 * 
	 * @param <T>
	 * 
	 * @param expireTs 超时时间(millisecond)
	 * @param claims   自定义信息
	 * @param keyPair  公钥密钥对
	 * @return 创建的Token
	 */
	public static <T> Token createTokenByRSA256(JwtCustomClaim<T> claims, RSAKeyPair keyPair) {

		try {
			// Algorithm algorithm = Algorithm.RSA256(keyPair.getPublicKeyOrigin(), keyPair.getPrivateKeyOrigin());
			// 使用私钥加密,只能使用公钥或者私钥解密
			Algorithm algorithm = Algorithm.RSA256(null, keyPair.getPrivateKeyOrigin());
			JWTCreator.Builder jwtBuilder = JWT.create();

			// 头部信息
			jwtBuilder.withHeader(claims.getHeaderClaims());
			
			if (JwtStringUtil.isNotEmpty(claims.getSubject())) {
				jwtBuilder.withSubject(claims.getSubject());
			}

			// Tid Token唯一标志
			if (JwtStringUtil.isNotEmpty(claims.getTid())) {
				jwtBuilder.withJWTId(claims.getTid());
			}

			// 签发人
			if (JwtStringUtil.isNotEmpty(claims.getIssuer())) {
				jwtBuilder.withIssuer(claims.getIssuer());
			}
			
			long ts = System.currentTimeMillis();
			Date issueAt = new Date(ts);
			// 30分钟后过期
			Date expireAt = new Date(ts + 1000 * 60 * 30);

			// 签发时间
			if (claims.getIssuedAt() != null) {
				jwtBuilder.withIssuedAt(claims.getIssuedAt());
			} else {
				jwtBuilder.withIssuedAt(issueAt);
			}

			// 过期时间
			if (claims.getExpiresAt() != null) {
				jwtBuilder.withExpiresAt(claims.getExpiresAt());
			} else {
				jwtBuilder.withExpiresAt(expireAt);
			}

			// 自定义权限List
			if (claims.getRoleList() != null && claims.getRoleList().size() > 0) {
				jwtBuilder.withClaim("roles", claims.getRoleList());
			}

			// 自定义声明
			if (claims.getCustomStringClaim() != null && claims.getCustomStringClaim().size() > 0) {
				for (Entry<String, String> entry : claims.getCustomStringClaim().entrySet()) {
					jwtBuilder.withClaim(entry.getKey(), entry.getValue());
				}
			}
			
			// 自定义盐值
			jwtBuilder.withClaim(CLAIM_KEY, CLAIM_VALUE);

			// not before。如果当前时间在nbf里的时间之前，则Token不被接受；一般都会留一些余地，比如几分钟。
			jwtBuilder.withNotBefore(new Date());
			String token = jwtBuilder.sign(algorithm);
			// 进行Base64编码
			return new Token(Base64.getEncoder().encodeToString(token.getBytes(RSAKeyGenerator.CHARSET)), "create success", false);
		} catch (JWTCreationException e) {
			// Invalid Signing configuration / Couldn't convert Claims.
			return new Token(null, e.getMessage(), true);
		} catch (Exception e) {
			return new Token(null, e.getMessage(), true);
		}
	}

	/**
	 * 使用RSA256方式验证Token You'll first need to create a JWTVerifier instance by
	 * calling JWT.require() and passing the Algorithm instance. If you require the
	 * token to have specific Claim values, use the builder to define them. The
	 * instance returned by the method build() is reusable, so you can define it
	 * once and use it to verify different tokens. Finally call verifier.verify()
	 * passing the token.
	 * 
	 * @param <T>
	 */
	public static <T> Token verifyTokenByRSA256(String token, JwtCustomClaim<T> claims, RSAPublicKey publicKey) {

		try {
			Algorithm algorithm = Algorithm.RSA256(publicKey, null);
			
			JWTVerifier verifier = JWT.require(algorithm)
					.withSubject(claims.getSubject())
					.withJWTId(claims.getTid())
			        .withIssuer(claims.getIssuer())
			        .withClaim(CLAIM_KEY, CLAIM_VALUE)
			        // 过期时间窗口 预留1秒
			        .acceptExpiresAt(1)
			        // 签发时间窗口 预留1秒
			        .acceptIssuedAt(1)
			        // not before窗口, 预留3分钟,早于系统日三分钟的token被认为不合法
			        .acceptNotBefore(180)
			        .build(); //Reusable verifier instance
			    DecodedJWT jwt = verifier.verify(new String(Base64.getDecoder().decode(token), RSAKeyGenerator.CHARSET));
			   System.out.println(jwt.getExpiresAt().getTime());
			return new Token(token, "verify success", false);
		} catch (JWTVerificationException e) {
			// Invalid signature/claims
			return new Token(null, e.getMessage(), true);
		} catch (Exception e) {
			return new Token(null, e.getMessage(), true);
		}
	}

}
