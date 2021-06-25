package cn.t.jwt.util.data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JwtCustomClaim<T> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7126503915194517276L;
	private Map<String, Object> headerClaims;
	private String subject;
	private String issuer;
	private String tid;
	private List<T> roleList;
	private Date issuedAt;
	private Date expiresAt;
	private Map<String, String> customStringClaim;

	public static class Builder<T> {
		/** 头部自定义声明 */
		private Map<String, Object> headerClaims;
		/** 主题 */
		private String subject;
		/** 签发人 */
		private String issuer;
		/** token唯一标识 */
		private String tid;
		/** 权限 */
		private List<T> roleList;
		/** 签发时间 */
		private Date issuedAt;
		/** 过期时间 */
		private Date expiresAt;
		/** 自定义声明 */
		private Map<String, String> customStringClaim;
		
		/**
		 * 初始化必须信息
		 * @param tid token唯一标识
		 * @param subject 主题
		 * @param issuer 签发人
		 * 
		 */
		public Builder(String tid, String subject, String issuer) {
			this.tid = tid;
			this.subject = subject;
			this.issuer = issuer;
		}

		/**
		 * 头部自定义声明
		 * 
		 * @param headerClaims 头部自定义声明
		 * @return
		 */
		public Builder<T> headerClaims(Map<String, Object> headerClaims) {
			this.headerClaims = headerClaims;
			return this;
		}
		
		/**
		 * 权限
		 * 
		 * @param roleList 权限
		 * @return
		 */
		public Builder<T> roleList(List<T> roleList) {
			this.roleList = roleList;
			return this;
		}

		/**
		 * 签发时间
		 * 
		 * @param issuedAt 签发时间
		 * @return
		 */
		public Builder<T> issuedAt(Date issuedAt) {
			this.issuedAt = issuedAt;
			return this;
		}

		/**
		 * 过期时间
		 * @param expiresAt 过期时间
		 * @return
		 */
		public Builder<T> expiresAt(Date expiresAt) {
			this.expiresAt = expiresAt;
			return this;
		}
		
		/**
		 * 自定义声明
		 * @param customStringClaim 自定义声明
		 * @return
		 */
		public Builder<T> customStringClaim(Map<String, String> customStringClaim) {
			this.customStringClaim = customStringClaim;
			return this;
		}
		
		public JwtCustomClaim<T> build() {
			return new JwtCustomClaim<T>(this);
		}

	}
	
	@SuppressWarnings("unused")
	private JwtCustomClaim () {
		
	}
	
	/**
	 * 构建对象
	 * @param builder
	 */
	public JwtCustomClaim(Builder<T> builder) {
		this.headerClaims = builder.headerClaims;
		this.setSubject(builder.subject);
		this.issuer = builder.issuer;
		this.tid = builder.tid;
		this.issuedAt = builder.issuedAt;
		this.expiresAt = builder.expiresAt;
		this.roleList = builder.roleList;
		this.customStringClaim = builder.customStringClaim;
	}

	public Map<String, Object> getHeaderClaims() {
		return headerClaims;
	}

	public void setHeaderClaims(Map<String, Object> headerClaims) {
		this.headerClaims = headerClaims;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public List<T> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<T> roleList) {
		this.roleList = roleList;
	}

	public Date getIssuedAt() {
		return issuedAt;
	}

	public void setIssuedAt(Date issuedAt) {
		this.issuedAt = issuedAt;
	}

	public Date getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Date expiresAt) {
		this.expiresAt = expiresAt;
	}

	public Map<String, String> getCustomStringClaim() {
		return customStringClaim;
	}

	public void setCustomStringClaim(Map<String, String> customStringClaim) {
		this.customStringClaim = customStringClaim;
	}
}
