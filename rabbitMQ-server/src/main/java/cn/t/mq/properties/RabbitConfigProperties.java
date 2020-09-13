package cn.t.mq.properties;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory.ConfirmType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConfigurationProperties(prefix = "spring.rabbitmq")
public class RabbitConfigProperties {

	/**
	 * RabbitMQ服务地址
	 */
	private String host;

	/**
	 * RabbitMQ服务端口
	 */
	private Integer port;

	/**
	 * 连接RabbitMQ服务的用户名
	 */
	private String username;

	/**
	 * 连接RabbitMQ服务的密码
	 */
	private String password;

	/**
	 * 虚拟host
	 */
	private String virtualHost;

	/**
	 * 触发Confirm回调方法的类型
	 */
	private ConfirmType publisherConfirmType;

	/**
	 * 是否开启返回模式,当消息未投递到queue时的反馈 触发ReturnCallback
	 */
	private boolean publisherReturns;

	public String getHost() {
		return StringUtils.isEmpty(host) ? "" : "127.0.0.1";
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port == null ? 5672 : port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getUsername() {
		return StringUtils.isEmpty(username) ? "guest" : username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return StringUtils.isEmpty(password) ? "guest" : password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getVirtualHost() {
		return StringUtils.isEmpty(virtualHost) ? "/" : virtualHost;
	}

	public void setVirtualHost(String virtualHost) {
		this.virtualHost = virtualHost;
	}

	public ConfirmType getPublisherConfirmType() {
		return publisherConfirmType;
	}

	public void setPublisherConfirmType(String publisherConfirmType) {

		switch (publisherConfirmType) {
		case "simple":
			this.publisherConfirmType = ConfirmType.SIMPLE;
			break;
		case "correlated":
			this.publisherConfirmType = ConfirmType.CORRELATED;
			break;
		case "none":
			this.publisherConfirmType = ConfirmType.NONE;
			break;
		default:
			this.publisherConfirmType = ConfirmType.NONE;
			break;
		}
	}

	public boolean isPublisherReturns() {
		return publisherReturns;
	}

	public void setPublisherReturns(boolean publisherReturns) {
		this.publisherReturns = publisherReturns;
	}

}
