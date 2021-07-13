package cn.t.auth.entity;

import java.util.List;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 用户认证信息类
 * @author TMQ
 *
 */
public class JwtUserDetail implements UserDetails, CredentialsContainer {

	private static final long serialVersionUID = 5734311199802036256L;
	
	private String password;

	private final String username;

	private final List<JwtGrantedAuthority> authorities;

	private final boolean accountNonExpired;

	private final boolean accountNonLocked;

	private final boolean credentialsNonExpired;

	private final boolean enabled;

	/**
	 * JsonCreator注解指定序列化时使用的构造器,默认使用无参,当显示指定构造器时,默认的无参构造器不会自动生成,序列化则会报错
	 * @param username
	 * @param password
	 * @param authorities
	 */
	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public JwtUserDetail(@JsonProperty("username") String username,  @JsonProperty("password") String password, @JsonProperty("authorities") List<JwtGrantedAuthority> authorities) {
		this(username, password, true, true, true, true, authorities);
	}

	public JwtUserDetail(String username, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked,
			List<JwtGrantedAuthority> authorities) {
		this.username = username;
		this.password = password;
		this.enabled = enabled;
		this.accountNonExpired = accountNonExpired;
		this.credentialsNonExpired = credentialsNonExpired;
		this.accountNonLocked = accountNonLocked;
		this.authorities = authorities;
	}

	@Override
	public void eraseCredentials() {
		this.password = null;
	}

	@Override
	public List<JwtGrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return this.accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return this.accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return this.credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}
}
