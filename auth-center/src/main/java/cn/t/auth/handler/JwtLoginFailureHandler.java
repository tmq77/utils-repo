package cn.t.auth.handler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.t.auth.response.AuthResponse;
import cn.t.auth.util.ResponseUtil;

public class JwtLoginFailureHandler implements AuthenticationFailureHandler {

  private ObjectMapper objectMapper;

  public JwtLoginFailureHandler(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws ServletException, JsonProcessingException {
    ResponseUtil.setResponse(response);
    AuthResponse res = new AuthResponse();
    res.setMsg(exception.getMessage());
    ResponseUtil.write(response, this.objectMapper.writeValueAsString(res));
  }
}
