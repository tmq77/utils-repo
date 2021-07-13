package cn.t.auth.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 自定义扩展权限类
 * @author TMQ
 *
 */
public class JwtGrantedAuthority implements GrantedAuthority {

	private static final long serialVersionUID = -8420223843490772575L;

	private final String authority;

	// @JsonProperty("authority") 由于序列化时写入的内容是authority: role_admin  所以反序列化指定对应的键,默认会使用get方法对应的名字作为key
	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public JwtGrantedAuthority(@JsonProperty("authority") String authority) {
		Assert.hasText(authority, "A granted authority textual representation is required");
		this.authority = authority;
	}

	@Override
	public String getAuthority() {
		return this.authority;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof JwtGrantedAuthority) {
			return this.authority.equals(((JwtGrantedAuthority) obj).authority);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.authority.hashCode();
	}

	@Override
	public String toString() {
		return this.authority;
	}
}
