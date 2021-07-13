package cn.t.auth.config;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.t.auth.filter.JwtLoginFilter;
import cn.t.auth.filter.JwtTokenFilter;
import cn.t.auth.handler.JwtLoginFailureHandler;
import cn.t.auth.handler.JwtLoginSuccessHandler;
import cn.t.auth.handler.JwtUserDetailsService;
import cn.t.auth.provider.JwtAuthenticationProvider;
import cn.t.auth.store.KeyStore;
import cn.t.jwt.util.rsa.RSAKeyGenerator;
import cn.t.jwt.util.rsa.RSAKeyPair;

/**
 * security config
 * 
 * @author Administrator
 *
 */
@Configuration
@EnableWebSecurity
public class JwtServerSecurityConfig extends WebSecurityConfigurerAdapter {
  
  @Autowired
  private ObjectMapper objectMapper;
  
  /**
   * url config<br>
   * The method defines which URL paths should be secured and which should not
   */
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // super.configure(http);
    // 表单登录
    // http.formLogin()
    // .loginPage("/login/home"); 登录的界面，前后端分离时不需要这个配置。没登录去访问系统资源时会重定向到这个界面

    // 禁用CSRF
    http.csrf().disable();
    // 禁用session
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    // 指定拦截的登录页面
    http.authorizeRequests().antMatchers("/auth/login").permitAll().anyRequest().authenticated().and();

    // add filter this method will not overwrite the default
    // UsernamePasswordAuthenticationFilter
    // 设置拦截的登录url(注意:这里的url需要去除properties文件中配置的server.servlet.context-path前缀, 否则会匹配不到)
    JwtLoginFilter loginFilter = new JwtLoginFilter("/login");
    // loginFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login", "POST"));
    // 需要显示指定manager,否则会报authenticationManager must be specified的异常
    loginFilter.setAuthenticationManager(super.authenticationManager());
    // 验证成功处理器
    loginFilter.setAuthenticationSuccessHandler(new JwtLoginSuccessHandler(this.objectMapper, keyStore()));
    loginFilter.setAuthenticationFailureHandler(new JwtLoginFailureHandler(this.objectMapper));
    // 在默认位置添加拦截器,原先的拦截器任然有效,但是由于拦截的url不同,所以不会互相影响
    http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);
    http.addFilterAfter(jwtTokenFilter(), JwtLoginFilter.class);
    
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    // 配置使用自定义的验证类进行验证
    auth.authenticationProvider(new JwtAuthenticationProvider(jwtUserDetailsService()));
  }
  
  /**
   * 秘钥对存储
   * @return
   * @throws UnsupportedEncodingException 
   * @throws NoSuchAlgorithmException 
   */
  @Bean
  public KeyStore keyStore() throws NoSuchAlgorithmException, UnsupportedEncodingException {
    RSAKeyPair keyPair = RSAKeyGenerator.generate();
    return new KeyStore(keyPair.getPublicKey(), keyPair.getPrivateKeyOrigin());
  }
  
  /**
   * 权限验证过滤器(非登录)
   * @param keyStore
   * @return
   * @throws NoSuchAlgorithmException
   * @throws UnsupportedEncodingException
   */
  @Bean
  public JwtTokenFilter jwtTokenFilter() throws NoSuchAlgorithmException, UnsupportedEncodingException {
    return new JwtTokenFilter(this.objectMapper, keyStore());
  }
  
  /**
   * 认证处理详细
   * @return
   */
  @Bean
  public JwtUserDetailsService jwtUserDetailsService() {
    return new JwtUserDetailsService();
  }

}
