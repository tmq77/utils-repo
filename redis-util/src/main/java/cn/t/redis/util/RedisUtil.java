package cn.t.redis.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import cn.t.redis.util.type.RedisDataType;

/**
 * redis操作工具组件,底层使用jedis连接池
 * 
 * @author tmq
 *
 */
@Component
public class RedisUtil {

	private static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

	/**
	 * 用于操作对象类型,也可以操作单个字符串或者数字
	 */
	@Autowired
	private RedisTemplate<String, Object> objecTemplate;

	/**
	 * 用于操作字符串或者数字类型
	 */
	// @Autowired
	// private StringRedisTemplate stringTemplate;

	/**
	 * 单次设值
	 * 
	 * @param key   键
	 * @param value 值
	 * @return true:成功 | false:失败
	 */
	public boolean set(String key, Object value) {
		try {
			this.objecTemplate.opsForValue().set(key, value);
			return true;
		} catch (Exception e) {
			logger.error(e.getCause());
			return false;
		}
	}

	/**
	 * 单次设值并设置缓存时间
	 * 
	 * @param key     键
	 * @param value   值
	 * @param expired 过期时间,单位秒,超过时间后对应key的value会被删除变成null
	 * @return true:成功 | false:失败
	 */
	public boolean set(String key, Object value, long expired) {
		try {
			if (expired <= 0) {
				this.set(key, value);
			} else {
				this.objecTemplate.opsForValue().set(key, value, expired, TimeUnit.SECONDS);
			}
			return true;
		} catch (Exception e) {
			logger.error(e.getCause());
			return false;
		}
	}

	/**
	 * 单次取值
	 * 
	 * @param key 键
	 * @return 值
	 */
	public Object get(String key) {
		try {
			return this.objecTemplate.opsForValue().get(key);
		} catch (Exception e) {
			logger.error(e.getCause());
			return null;
		}
	}

	/**
	 * 删除给定的键
	 * 
	 * @param key 键:String[]
	 */
	public void delete(String... key) {
		if (key != null && key.length > 0) {
			this.objecTemplate.delete(Arrays.asList(key));
		}
	}

	/**
	 * 单次递增递减
	 * 
	 * @param key
	 * @param delta 增量
	 * @return 计算结果
	 */
	public long cal(String key, long delta) {
		return this.objecTemplate.opsForValue().increment(key, delta);
	}

	/**
	 * 判断是否有当前key
	 * 
	 * @param key 键
	 * @return true:有 | false:无
	 */
	public boolean hasKey(String key) {
		try {
			return this.objecTemplate.hasKey(key);
		} catch (Exception e) {
			logger.error(e.getCause());
			return false;
		}
	}

	/**
	 * 单次hash赋值 hash : key(String)-value(Object)映射表
	 * 
	 * @param key  键
	 * @param hash 键值对map
	 * @return true:成功 | false:失败
	 */
	public boolean setHashMap(String key, Map<String, Object> hash) {
		try {
			this.objecTemplate.opsForHash().putAll(key, hash);
			return true;
		} catch (Exception e) {
			logger.error(e.getCause());
			return false;
		}
	}

	/**
	 * 单次hash赋值 hash : key(String)-value(Object)映射表
	 * 
	 * @param key    键
	 * @param hash   键值对map
	 * @param expire 过期时间(秒)
	 * @return true:成功 | false:失败
	 */
	public boolean setHashMap(String key, Map<String, Object> hash, long expire) {
		try {
			this.objecTemplate.opsForHash().putAll(key, hash);
			if (expire > 0) {
				this.setExpire(key, expire);
			}
			return true;
		} catch (Exception e) {
			logger.error(e.getCause());
			return false;
		}
	}

	/**
	 * 单次给指定key的键值对赋值,如果键值对映射中没有给定的hashKey,则新增
	 * 
	 * @param key     键
	 * @param hashKey 项
	 * @param value   值
	 * @return true:成功 | false:失败
	 */
	public boolean setHashValue(String key, String hashKey, Object value) {
		try {
			this.objecTemplate.opsForHash().put(key, hashKey, value);
			return true;
		} catch (Exception e) {
			logger.error(e.getCause());
			return false;
		}

	}

	/**
	 * 单次给指定key的键值对赋值并设置key对应的整个hash的过期时间,如果键值对映射中没有给定的hashKey,则新增
	 * 
	 * @param key     键
	 * @param hashKey 项
	 * @param value   值
	 * @param expire  过期时间
	 * @return true:成功 | false:失败
	 */
	public boolean setHashValue(String key, String hashKey, Object value, long expire) {
		try {
			this.objecTemplate.opsForHash().put(key, hashKey, value);
			if (expire > 0) {
				this.setExpire(key, expire);
			}
			return true;
		} catch (Exception e) {
			logger.error(e.getCause());
			return false;
		}
	}

	/**
	 * 获取指定key的指定项的值
	 * 
	 * @param key     键
	 * @param hashKey 项
	 * @return 指定key指定hashKey的值
	 */
	public Object getHashValue(String key, String hashKey) {
		try {
			return this.objecTemplate.opsForHash().get(key, hashKey);
		} catch (Exception e) {
			logger.error(e.getCause());
			return null;
		}
	}

	/**
	 * 批量删除 删除给定键对应的值
	 * 
	 * @param key      键
	 * @param hashKeys hash中要删除的键数组
	 */
	public void deleteHash(String key, Object... hashKeys) {
		if (hashKeys != null && hashKeys.length > 0) {
			this.objecTemplate.opsForHash().delete(key, hashKeys);
		}
	}

	/**
	 * 判断hash中是否有对应的键
	 * 
	 * @param key     键
	 * @param hashKey 项
	 * @return true:成功 | false:失败
	 */
	public boolean hasHashKey(String key, String hashKey) {
		return this.objecTemplate.opsForHash().hasKey(key, hashKey);
	}

	/**
	 * hash递增递减计算 如果不存在,就会创建一个 并把计算后的值返回
	 * 
	 * @param key     键
	 * @param hashKey 项
	 * @param delta   增减量
	 * @return 计算结果
	 */
	public Long hashCal(String key, String hashKey, long delta) {
		return this.objecTemplate.opsForHash().increment(key, hashKey, delta);
	}

	/**
	 * 单次获取指定key的键值对映射
	 * 
	 * @param key 键
	 * @return 对应的HashMap
	 */
	public Map<Object, Object> getHashMap(String key) {
		try {
			return this.objecTemplate.opsForHash().entries(key);
		} catch (Exception e) {
			logger.error(e.getCause());
			return null;
		}
	}

	/**
	 * 单次Set赋值
	 * 
	 * @param key    键
	 * @param values 值数组
	 * @return 成功添加的个数
	 */
	public Long setSet(String key, Object... values) {
		try {
			return this.objecTemplate.opsForSet().add(key, values);
		} catch (Exception e) {
			logger.error(e.getCause());
			return null;
		}
	}

	/**
	 * 单次Set赋值并设置过期时间
	 * 
	 * @param key    键
	 * @param expire 过期时间
	 * @param values 值数组
	 * @return 成功添加的个数
	 */
	public Long setSet(String key, long expire, Object... values) {
		try {
			Long result = this.objecTemplate.opsForSet().add(key, values);
			if (expire > 0) {
				this.setExpire(key, expire);
			}
			return result;
		} catch (Exception e) {
			logger.error(e.getCause());
			return null;
		}
	}

	/**
	 * 获取对应key的Set
	 * 
	 * @param key 键
	 * @return 对应的Set
	 */
	public Set<Object> getSet(String key) {
		try {
			return this.objecTemplate.opsForSet().members(key);
		} catch (Exception e) {
			logger.error(e.getCause());
			return null;
		}
	}

	/**
	 * 判断对应key的的set中是否有value
	 * 
	 * @param key   键
	 * @param value 值
	 * @return true:成功 | false:失败
	 */
	public boolean hasSetKeyValue(String key, Object value) {
		try {
			return this.objecTemplate.opsForSet().isMember(key, value);
		} catch (Exception e) {
			logger.error(e.getCause());
			return false;
		}
	}

	/**
	 * 获取Set的大小
	 * 
	 * @param key 键
	 * @return Set的大小
	 */
	public Long getSetSize(String key) {
		try {
			return this.objecTemplate.opsForSet().size(key);
		} catch (Exception e) {
			logger.error(e.getCause());
			return null;
		}
	}

	/**
	 * 删除指定key的Set中的指定值
	 * 
	 * @param key    键
	 * @param values 值数组
	 * @return 删除的件数
	 */
	public Long deleteSet(String key, Object... values) {
		try {
			return this.objecTemplate.opsForSet().remove(key, values);
		} catch (Exception e) {
			logger.error(e.getCause());
			return null;
		}
	}

	/**
	 * 向当前key对应的List右侧添加一个数据
	 * 
	 * @param key   键
	 * @param value 值
	 * @return true:成功 | false:失败
	 */
	public boolean setListR(String key, Object value) {
		try {
			this.objecTemplate.opsForList().rightPush(key, value);
			return true;
		} catch (Exception e) {
			logger.error(e.getCause());
			return false;
		}
	}

	/**
	 * 向当前key对应的List右侧添加一个数据并设置整个List的超时时间
	 * 
	 * @param key    键
	 * @param value  值
	 * @param expire 超时时间
	 * @return true:成功 | false:失败
	 */
	public boolean setListR(String key, Object value, long expire) {
		try {
			this.objecTemplate.opsForList().rightPush(key, value);
			if (expire > 0) {
				return this.setExpire(key, expire);
			}
			return true;
		} catch (Exception e) {
			logger.error(e.getCause());
			return false;
		}
	}

	/**
	 * 向当前key对应的List右侧添加一个列表的数据
	 * 
	 * @param key    键
	 * @param values 值
	 * @return true:成功 | false:失败
	 */
	public boolean setListRAll(String key, List<? extends Object> values) {
		try {
			this.objecTemplate.opsForList().rightPushAll(key, values);
			return true;
		} catch (Exception e) {
			logger.error(e.getCause());
			return false;
		}
	}

	/**
	 * 向当前key对应的List右侧添加一个列表的数据并设置整个列表的超时时间
	 * 
	 * @param key    键
	 * @param values 值
	 * @param expire 超时时间
	 * @return true:成功 | false:失败
	 */
	public boolean setListR(String key, List<? extends Object> values, long expire) {
		try {
			this.objecTemplate.opsForList().rightPushAll(key, values);
			if (expire > 0) {
				this.setExpire(key, expire);
			}
			return true;
		} catch (Exception e) {
			logger.error(e.getCause());
			return false;
		}
	}

	/**
	 * 根据索引修改对应的值
	 * 
	 * @param key   键
	 * @param index 索引
	 * @param value 修改值
	 * @return true:成功 | false:失败
	 */
	public boolean updateList(String key, long index, Object value) {
		try {
			this.objecTemplate.opsForList().set(key, index, value);
			return true;
		} catch (Exception e) {
			logger.error(e.getCause());
			return false;
		}
	}

	/**
	 * 获取对应key的List start为1并且end为-1时代表所有值
	 * 
	 * @param key   键
	 * @param start 开始索引
	 * @param end   结束索引
	 * @return 该键对应的List
	 */
	public List<? extends Object> getList(String key, long start, long end) {
		try {
			return this.objecTemplate.opsForList().range(key, start, end);
		} catch (Exception e) {
			logger.error(e.getCause());
			return null;
		}
	}

	/**
	 * 获取List的长度
	 * 
	 * @param key 键
	 * @return 数组长度
	 */
	public Long getListSize(String key) {
		try {
			return this.objecTemplate.opsForList().size(key);
		} catch (Exception e) {
			logger.error(e.getCause());
			return null;
		}
	}

	/**
	 * 根据索引取值
	 * 
	 * @param key   键
	 * @param index 索引 0开始, -1代表最后一个值,-2代表倒数第二个
	 * @return 对应索引的值
	 */
	public Object getListByIndex(String key, long index) {
		try {
			return this.objecTemplate.opsForList().index(key, index);
		} catch (Exception e) {
			logger.error(e.getCause());
			return null;
		}
	}

	/**
	 * 从存储在键中的列表中删除等于值的元素的第一个计数事件 count &gt; 0：删除等于从左到右移动的值的第一个元素 count &lt;
	 * 0：删除等于从右到左移动的值的第一个元素 count = 0：删除等于value的所有元素
	 * 
	 * @param key   键
	 * @param count 要删除的件数
	 * @param value 删除的值
	 * @return 删除的件数
	 */	public Long deleteList(String key, long count, Object value) {
		try {
			return this.objecTemplate.opsForList().remove(key, count, value);
		} catch (Exception e) {
			logger.error(e.getCause());
			return null;
		}
	}

	/**
	 * 获取对应key的过期时间
	 * 
	 * @param key 键
	 * @return null: 事务中或者没有当前key
	 */
	public Long getExpire(String key) {
		try {
			return this.objecTemplate.getExpire(key, TimeUnit.SECONDS);
		} catch (Exception e) {
			logger.error(e.getCause());
			return null;
		}
	}

	/**
	 * 设置过期时间
	 * 
	 * @param key    键
	 * @param expire 过期时间,秒为单位
	 * @return true:成功 | false:失败
	 */
	public boolean setExpire(String key, long expire) {
		try {
			if (expire > 0) {
				this.objecTemplate.expire(key, expire, TimeUnit.SECONDS);
			}
			return true;
		} catch (Exception e) {
			logger.error(e.getCause());
			return false;
		}
	}

	/**
	 * 获取redisTemplate进行更细的操作
	 * 
	 * @return RedisTemplate对象
	 */
	public RedisTemplate<String, Object> getRedisTemplate() {
		return this.objecTemplate;
	}

	/**
	 * 获取连续操作list的对象
	 * 
	 * @param key 键
	 * @return 连续操作对象
	 */
	public BoundListOperations<String, Object> getListBound(String key) {
		if (!this.hasKey(key)) {
			return null;
		}
		return this.objecTemplate.boundListOps(key);
	}

	/**
	 * 获取连续操作单值的对象
	 * 
	 * @param key 键
	 * @return 连续操作对象
	 */
	public BoundValueOperations<String, Object> getValueBound(String key) {
		if (!this.hasKey(key)) {
			return null;
		}
		return this.objecTemplate.boundValueOps(key);
	}

	/**
	 * 获取连续操作Hash的对象
	 * 
	 * @param key 键
	 * @return 连续操作对象
	 */
	public BoundHashOperations<String, String, Object> getHashBound(String key) {
		if (!this.hasKey(key)) {
			return null;
		}
		return this.objecTemplate.boundHashOps(key);
	}

	/**
	 * 获取key对应数据的类型
	 * 
	 * @param key 键
	 * @return Redis数据类型
	 */
	public RedisDataType getDataType(String key) {
		// string | none | set | list | zset | stream | hash
		return RedisDataType.convertType(this.objecTemplate.type(key));
	}

}
