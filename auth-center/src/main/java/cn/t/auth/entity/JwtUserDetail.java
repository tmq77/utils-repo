package cn.t.auth.entity;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class JwtUserDetail extends User {

  private static final long serialVersionUID = 5734311199802036256L;

  public JwtUserDetail(String username, String password, Collection<? extends GrantedAuthority> authorities) {
    super(username, password, authorities);
  }

  public JwtUserDetail(String username, String password, boolean enabled, boolean accountNonExpired,
      boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
    super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
  }
}
