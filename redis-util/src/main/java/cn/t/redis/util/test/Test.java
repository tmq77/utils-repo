package cn.t.redis.util.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

import cn.t.redis.util.RedisUtil;

//@Component
public class Test implements CommandLineRunner{

	@Autowired
	private RedisUtil util;
	
	@Override
	public void run(String... args) throws Exception {
		this.util.set("tmq", 1);
		System.out.println(this.util.get("tmq"));
		this.util.cal("tmq", -1);
		System.out.println(this.util.get("tmq"));
		this.util.setListR("list", "444");
		System.out.println(this.util.getList("list", 0, -1));
		this.util.deleteList("list", 1, "444");
		System.out.println(this.util.getList("list", 0, -1));
		this.util.setHashValue("hash", "id", "名字");
		System.out.println(this.util.getHashMap("hash"));
		
		BoundValueOperations<String, Object> bound = this.util.getValueBound("tmq");
		bound.set("变更");
		bound.set("222");
		
		this.util.delete(new String[]{"list", "tmq", "hash"});
		
	}
	
	
	/**
	 * 事务的使用方法
	 * operations.discard()可以取消当前所有事务操作
	 */
	public void redisTranscation() {
		this.util.getRedisTemplate().execute(new SessionCallback<Object>() {
			@Override
			public Object execute(RedisOperations operations) throws DataAccessException {
				operations.multi();
				operations.opsForValue().set("transcation", 2);
				operations.opsForValue().increment("transcation", 3);
				operations.exec();
				return null;
			}
		});
	}
	
	public void testTranscation() {
		this.util.getRedisTemplate().multi();
		
		this.util.set("transcation", 9);
		
		this.util.cal("transcation", -1);
		
		this.util.getRedisTemplate().exec();
	}

}
