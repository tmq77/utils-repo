package cn.t.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.t.auth.response.AuthResponse;

@RestController
public class AuthController {
  
  @PostMapping("/")
  public AuthResponse login() {
    
    return null;
  }
  
}
