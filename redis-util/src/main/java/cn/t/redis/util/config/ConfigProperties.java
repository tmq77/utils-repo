package cn.t.redis.util.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class ConfigProperties {
	
	@Value("${spring.redis.host:localhost}")
	private String host;
	
	@Value("${spring.redis.port:6379}")
	private Integer port;
	
	@Value("${spring.redis.password:}")
	private String password;
	
	@Value("${spring.redis.timeout:1000}")
	private Integer timeout;
	
	@Value("${spring.redis.database:0}")
	private Integer database;
	
	@Value("${spring.redis.jedis.pool.max-active:8}")
	private Integer maxActive;

	@Value("${spring.redis.jedis.pool.max-wait:1000}")
	private Integer maxWait;
	
	@Value("${spring.redis.jedis.pool.max-idle:8}")
	private Integer maxIdle;
	
	@Value("${spring.redis.jedis.pool.min-idle:0}")
	private Integer minIdle;
		
}
