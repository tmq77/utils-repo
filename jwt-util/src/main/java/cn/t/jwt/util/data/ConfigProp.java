package cn.t.jwt.util.data;

public class ConfigProp {
  
  /** 过期时间(ms) */
  private long expiredTs;
  
  /** 过期时间预留窗口(s) */
  private int leewayExpiresAt;
  
  /** 签发时间预留窗口(s) */
  private int leewayIssuedAt;
  
  /** nbf(不早于)时间预留窗口(s) */
  private int leewayNotBefore;
  
  public long getExpiredTs() {
    return expiredTs;
  }

  public void setExpiredTs(long expiredTs) {
    this.expiredTs = expiredTs;
  }

  public int getLeewayExpiresAt() {
    return leewayExpiresAt;
  }

  public void setLeewayExpiresAt(int leewayExpiresAt) {
    this.leewayExpiresAt = leewayExpiresAt;
  }

  public int getLeewayIssuedAt() {
    return leewayIssuedAt;
  }

  public void setLeewayIssuedAt(int leewayIssuedAt) {
    this.leewayIssuedAt = leewayIssuedAt;
  }

  public int getLeewayNotBefore() {
    return leewayNotBefore;
  }

  public void setLeewayNotBefore(int leewayNotBefore) {
    this.leewayNotBefore = leewayNotBefore;
  }
}
