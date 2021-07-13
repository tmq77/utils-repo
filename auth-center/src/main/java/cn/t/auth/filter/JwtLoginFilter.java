package cn.t.auth.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import cn.t.auth.entity.JwtAccessToken;

/**
 * 
 * 继承UsernamePasswordAuthenticationFilter用于处理默认的登录拦截
 * @author Administrator
 *
 */
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {
  
  /**
   * 无参构造器,默认使用父类设置的登录拦截url<>
   * 在配置中配置formLogin().loginProcessingUrl(“XXXX”) 可以改变UsernamePasswordAuthenticationFilter登录拦截的url
   * 
   */
  public JwtLoginFilter() {
    // 父类中的无参构造器中默认使用/login作为拦截的url
    super();
  }
  
  public JwtLoginFilter(String processUrl) {
    // 设定拦截的登录url
    super.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(processUrl, "POST"));
  }

  /**
   * 
   * Performs actual authentication.
   * AbstractAuthenticationProcessingFilter中会调用此方法进行处理
   * 
   * <p>
   * The implementation should do one of the following:
   * <ol>
   * <li>Return a populated authentication token for the authenticated user, indicating
   * successful authentication</li>
   * <li>Return null, indicating that the authentication process is still in progress.
   * Before returning, the implementation should perform any additional work required to
   * complete the process.</li>
   * <li>Throw an <tt>AuthenticationException</tt> if the authentication process
   * fails</li>
   * </ol>
   * @param request from which to extract parameters and perform the authentication
   * @param response the response, which may be needed if the implementation has to do a
   * redirect as part of a multi-stage authentication process (such as OpenID).
   * @return the authenticated user token, or null if authentication is incomplete.
   * @throws AuthenticationException if authentication fails.
   */
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException {
    
    // request
    String username = super.obtainUsername(request);
    String password = super.obtainPassword(request);
    
    // 登录验证也可以在这个过滤器中执行，直接生成认证后的JwtLoginToken即可
    // 然后JwtAuthenticationProvider直接返回认证后的对象即可
    
    JwtAccessToken authToken = new JwtAccessToken(username, password);
    
    // 这里直接参照的UsernamePasswordAuthenticationFilter中的setDetails方法,讲sessionID和ip构造成认证详情对象
    authToken.setDetails(new WebAuthenticationDetails(request));
    
    // 调用AuthenticationManager中的provider处理详细认证逻辑
    // AuthenticationManager会获取受支持的AuthenticationProvider(需要在配置类中配置JwtAuthenticationProvider),
    // 生成已认证的凭证,此时凭证中的主体为userDetails  --- 这里会委托给AuthenticationProvider实现类来验证
    // 即 跳转到 JwtAuthenticationProvider.authenticate 方法中认证
    return super.getAuthenticationManager().authenticate(authToken);
  }

}
