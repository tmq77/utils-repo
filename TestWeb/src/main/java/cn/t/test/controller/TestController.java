package cn.t.test.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.t.jwt.util.JwtUtil;
import cn.t.jwt.util.annotation.SkipToken;
import cn.t.jwt.util.token.Token;
import cn.t.redis.util.RedisUtil;

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
}
