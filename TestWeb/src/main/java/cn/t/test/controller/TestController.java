package cn.t.test.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.t.jwt.util.JwtUtil;
import cn.t.jwt.util.annotation.SkipToken;
import cn.t.jwt.util.token.Token;
import cn.t.redis.util.RedisUtil;
import cn.t.serialization.util.SerializationUtil;
import cn.t.test.bean.Pojo;

@RestController
public class TestController {

	@Autowired
	private RedisUtil redisUtil;

	@PostMapping("/hello")
	public Map<String, Object> hello() {
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("success", new Date());
		return resultMap;
	}
	
	@PostMapping("/token")
	public String getToken() {
		Token token = JwtUtil.createTokenByHS256(120 * 1000);
		if (token.isError()) {
			return token.getMessage();
		}
		this.redisUtil.set("testToken", token.getToken(), 90);
		return token.getToken();
	}
	
	@PostMapping("/showToken")
	@SkipToken
	public String showToken() {
		Object token = this.redisUtil.get("testToken");
		if (token == null) {
			return "Token已过期";
		}
		return token.toString();
	}
	
	@PostMapping("/testSerialization")
	@SkipToken
	public String testSerialization(@RequestParam("path") String path) throws Exception {
		Pojo obj = new Pojo();
		obj.setName("tmq");
		obj.setId("1111111");
		
		Pojo copy = SerializationUtil.cloneObj(obj);
		
		return "序列化完成:文件路径为:" + SerializationUtil.serializeObj(copy, path);
	}
	
	@PostMapping("/testDeserialization")
	@SkipToken
	public Object testDeserialization(@RequestParam("path") String path) throws Exception {
		
		Pojo obj = SerializationUtil.deserializeObj(path);
		return obj;
	}
	
	
}
