package cn.t.auth.handler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import cn.t.auth.constant.AuthConstant;
import cn.t.auth.entity.JwtUserDetail;

public class JwtUserDetailsService implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		if (username == null || username.trim().length() == 0) {
			 throw new BadCredentialsException(AuthConstant.MSG_USERNAME_EMPTY);
		}
		
		
		return new JwtUserDetail(username, username, this.getAuthorities());
	}
	
	
	// TODO
	/**
	 * 创建权限
	 * @return
	 */
	private List<GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<>();
		
		SimpleGrantedAuthority auth = new SimpleGrantedAuthority(AuthConstant.PREFIX_ROLE + "admin");
		
		authorities.add(auth);
		
		return authorities;
	}

}
