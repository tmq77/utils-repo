package cn.t.jwt.util;

import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

/**
 * https://blog.csdn.net/Aeve_imp/article/details/101217466 RSA公钥密钥生成器<br>
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

  /** 公钥 */
  private String rsaPublicKey;

  /** 私钥 */
  private String rsaPrivateKey;

  /**
   * 默认编码
   */
  public static final String CHARSET = "UTF-8";

  /**
   * 
   * 生成公钥私钥对
   * 
   * @throws NoSuchAlgorithmException     加密算法不存在
   * @throws UnsupportedEncodingException 字符类型不支持
   */
  public void generate() throws NoSuchAlgorithmException, UnsupportedEncodingException {
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

    // 转为字符串
    this.setRsaPublicKey(new String(Base64.getEncoder().encode(publicKey.getEncoded()), CHARSET));
    this.setRsaPrivateKey(new String(Base64.getEncoder().encode(privateKey.getEncoded()), CHARSET));
  }

  /**
   * 获取生成的公钥字符串
   * 
   * @return the rsaPublicKey
   */
  public String getRsaPublicKey() {
    return rsaPublicKey;
  }

  /**
   * 
   * @param rsaPublicKey the rsaPublicKey to set
   */
  private void setRsaPublicKey(String rsaPublicKey) {
    this.rsaPublicKey = rsaPublicKey;
  }

  /**
   * 获取生成的私钥字符串
   * 
   * @return the rsaPrivateKey
   */
  public String getRsaPrivateKey() {
    return rsaPrivateKey;
  }

  /**
   * @param rsaPrivateKey the rsaPrivateKey to set
   */
  private void setRsaPrivateKey(String rsaPrivateKey) {
    this.rsaPrivateKey = rsaPrivateKey;
  }

  public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    RSAKeyGenerator generator = new RSAKeyGenerator();
    generator.generate();
    System.out.println(generator.getRsaPrivateKey());
  }

}
