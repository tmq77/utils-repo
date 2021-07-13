package cn.t.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.t.auth.response.AuthResponse;

@RestController
public class AuthController {

	@PostMapping("/")
	public AuthResponse login() {
		AuthResponse res = new AuthResponse();
		res.setMsg("hello Auth center");
		return res;
	}
	
	@RequestMapping("/test")
	public AuthResponse test() {
		AuthResponse res = new AuthResponse();
		res.setMsg("test success");
		return res;
	}

}
