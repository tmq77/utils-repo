package cn.t.redis.util.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig<T> {
	

	@Autowired
	private ConfigProperties configProperties;
	
	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		
		// 连接池最大连接数
		jedisPoolConfig.setMaxTotal(this.configProperties.getMaxActive());
		// 连接池中的最大空闲连接
		jedisPoolConfig.setMaxIdle(this.configProperties.getMaxIdle());
		// 连接池最大阻塞等待时间
		jedisPoolConfig.setMaxWaitMillis(this.configProperties.getMaxWait());
		// 连接池中的最小空闲连接
		jedisPoolConfig.setMinIdle(this.configProperties.getMinIdle());
		
		// 配置给定连接池的连接工厂
		JedisConnectionFactory connectionFactory = new JedisConnectionFactory(jedisPoolConfig);
		
		// 单点Redis
	    RedisStandaloneConfiguration redisConfig = connectionFactory.getStandaloneConfiguration();
	    
	    redisConfig.setDatabase(this.configProperties.getDatabase());
	    redisConfig.setHostName(this.configProperties.getHost());
	    redisConfig.setPassword(this.configProperties.getPassword());
	    redisConfig.setPort(this.configProperties.getPort());
	    
	    // 哨兵Redis
	    // RedisSentinelConfiguration redisConfig = new RedisSentinelConfiguration();
	    // 集群Redis
	    // RedisClusterConfiguration redisConfig = new RedisClusterConfiguration();
	    return connectionFactory;
	}
	
	@Bean
	public RedisTemplate<String, T> redisTemplate(RedisConnectionFactory connectionFactory) {
		
		RedisTemplate<String, T> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(connectionFactory);
		
		// 定义Jackson2JsonRedisSerializer序列化对象
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
		
		ObjectMapper objectMapper = new ObjectMapper();
		// 指定要序列化的域，ALL:field,get和set等，ANY: 可见性,会将有private修饰符的字段也序列化
		objectMapper.setVisibility(PropertyAccessor.ALL, Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会报异常
		// objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
		
		//使用StringRedisSerializer来序列化和反序列化redis的key值
		// RedisSerializer redisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        //value
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
		
		redisTemplate.afterPropertiesSet();
		
		return redisTemplate;
	
	}
	
	
	

}
