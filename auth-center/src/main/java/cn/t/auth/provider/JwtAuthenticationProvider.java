package cn.t.auth.provider;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import cn.t.auth.entity.JwtAccessToken;
import cn.t.auth.entity.JwtUserDetail;
import cn.t.auth.handler.JwtUserDetailsService;

/**
 * 自定义用户认证<br>
 * AuthenticationProvider是具体的登录认证逻辑实现<br>
 * security框架默认使用DaoAuthenticationProvider验证(传入UserDetailService)
 * 
 * @author Administrator
 *
 */
public class JwtAuthenticationProvider implements AuthenticationProvider {
  
  private JwtUserDetailsService userDetailsService;
  
  public JwtAuthenticationProvider(JwtUserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }
  
  /**
   * 具体的用户验证逻辑
   */
  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    
    // TODO
    // defaultCheck
    String username = authentication.getName();
    JwtUserDetail userDertail = (JwtUserDetail) this.userDetailsService.loadUserByUsername(username);
    this.defaultCheck(userDertail);
    
    JwtAccessToken authenticatedToken = new JwtAccessToken(userDertail, userDertail.getUsername(), userDertail.getAuthorities());
    authenticatedToken.setDetails(authentication.getDetails());
    // 如果前面的登录过滤器已经完成了认证,直接返回认证对象即可
    return authenticatedToken;
  }

  /**
   * 配置验证类<br>
   * 该认证服务只验证JwtAccessToken，其他Token会被默认的DaoAuthenticationProvider进行验证
   */
  @Override
  public boolean supports(Class<?> authentication) {
    return (JwtAccessToken.class.isAssignableFrom(authentication));
  }
  
  /**
   * 认证信息的检查
   *
   * @param user
   */
  private void defaultCheck(UserDetails user) {
      if (!user.isAccountNonLocked()) {
          throw new LockedException("User account is locked");
      }

      if (!user.isEnabled()) {
          throw new DisabledException("User is disabled");
      }

      if (!user.isAccountNonExpired()) {
          throw new AccountExpiredException("User account has expired");
      }
  }
}
