package cn.t.jwt.util.rsa;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * RSA公钥私钥对
 * @author Administrator
 *
 */
public class RSAKeyPair {
  
  public RSAKeyPair(String publicKey, String privateKey, RSAPublicKey publicKeyOrigin, RSAPrivateKey privateKeyOrigin) {
    this.publicKey = publicKey;
    this.privateKey = privateKey;
    this.publicKeyOrigin = publicKeyOrigin;
    this.privateKeyOrigin = privateKeyOrigin;
  }

  /** 公钥(字符串) */
  private String publicKey;

  /** 私钥(字符串) */
  private String privateKey;
  
  /** 公钥(原始对象) */
  private RSAPublicKey publicKeyOrigin;
  
  /** 私钥(原始对象) */
  private RSAPrivateKey privateKeyOrigin;

  public String getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(String publicKey) {
    this.publicKey = publicKey;
  }

  public String getPrivateKey() {
    return privateKey;
  }

  public void setPrivateKey(String privateKey) {
    this.privateKey = privateKey;
  }

  public RSAPublicKey getPublicKeyOrigin() {
    return publicKeyOrigin;
  }

  public void setPublicKeyOrigin(RSAPublicKey publicKeyOrigin) {
    this.publicKeyOrigin = publicKeyOrigin;
  }

  public RSAPrivateKey getPrivateKeyOrigin() {
    return privateKeyOrigin;
  }

  public void setPrivateKeyOrigin(RSAPrivateKey privateKeyOrigin) {
    this.privateKeyOrigin = privateKeyOrigin;
  }

}
