package cn.t.auth.entity;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class JwtAccessToken extends AbstractAuthenticationToken {

  private static final long serialVersionUID = 6904661818241572358L;

  private Object principal;

  private Object credentials;

  /**
   * 创建未认证的登录token
   * 
   * @param username
   * @param password
   */
  public JwtAccessToken(Object principal, Object credentials) {
    super(null);
    this.principal = principal;
    this.credentials = credentials;
    // 框架中会进行后续认证处理,所以需要设值
    super.setAuthenticated(false); // must use super, as we override
  }

  /**
   * 创建认证的登录token,在自定义的认证provider中调用
   * 
   * @param username
   * @param password
   * @param authorities
   */
  public JwtAccessToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.principal = principal;
    this.credentials = credentials;
    super.setAuthenticated(true); // must use super, as we override
  }

  @Override
  public Object getCredentials() {
    return this.credentials;
  }

  @Override
  public Object getPrincipal() {
    return this.principal;
  }
}
