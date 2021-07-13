package cn.t.auth.store;

import java.security.interfaces.RSAPrivateKey;

public class KeyStore {
  
  private RSAPrivateKey privateKey;
  
  private String publicKey;
  
  public KeyStore(String publicKey, RSAPrivateKey privateKey) {
    this.privateKey = privateKey;
    this.publicKey = publicKey;
  }

  public RSAPrivateKey getPrivateKey() {
    return privateKey;
  }

  public void setPrivateKey(RSAPrivateKey privateKey) {
    this.privateKey = privateKey;
  }

  public String getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(String publicKey) {
    this.publicKey = publicKey;
  }
  
}
