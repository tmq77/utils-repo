package cn.t.redis.util.type;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.redis.connection.DataType;

/**
 * Redis数据类型的枚举类
 * @author tmq
 *
 */
public enum RedisDataType {
	
	STRING("string"),
	NONE("none"),
	SET("set"),
	ZSET("zset"),
	LIST("list"),
	STREAM("stream"),
	HASH("hash");
	
	// private EnumMap<RedisDataType,String> map = new EnumMap<RedisDataType,String>(RedisDataType.class);
	/**
	 * 存放枚举类与值的映射关系
	 */
	private static final Map<String, RedisDataType> MAP = new ConcurrentHashMap<>();
	
	static {
		for (RedisDataType type : EnumSet.allOf(RedisDataType.class))
			MAP.put(type.value, type);
	}
	
	/**
	 * 枚举类对应的值
	 */
	private final String value;
	
	/**
	 * 构造方法
	 * @param value 枚举类对应的值
	 */
	private RedisDataType(String value) {
		this.value = value;
	}
	
	/**
	 * 获取枚举类对应的值
	 * @return 枚举类对应的值
	 */
	public String getValue() {
		return this.value;
	}
	
	/**
	 * Redis包中的DataType转换为本工具类中的类型
	 * @param type org.springframework.data.redis.connection.DataType
	 * @return 根据Redis的DataType转换后的RedisDataType枚举类型
	 */
	public static RedisDataType convertType(DataType type) {
		return MAP.get(type.code());
	}
}
