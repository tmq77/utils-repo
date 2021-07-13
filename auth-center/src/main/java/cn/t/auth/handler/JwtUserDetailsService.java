package cn.t.auth.handler;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import cn.t.auth.entity.JwtUserDetail;

public class JwtUserDetailsService implements UserDetailsService{

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    
    // TODO
    
    return new JwtUserDetail(username, username,  null);
  }

}
