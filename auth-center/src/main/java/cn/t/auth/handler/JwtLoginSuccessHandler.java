package cn.t.auth.handler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.t.auth.constant.AuthConstant;
import cn.t.auth.entity.JwtUserDetail;
import cn.t.auth.response.AuthResponse;
import cn.t.auth.store.KeyStore;
import cn.t.auth.util.ResponseUtil;
import cn.t.jwt.util.JwtUtil;
import cn.t.jwt.util.data.Token;

public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

	private ObjectMapper objectMapper;

	private KeyStore keyStore;

	public JwtLoginSuccessHandler(ObjectMapper objectMapper, KeyStore keyStore) {
		this.objectMapper = objectMapper;
		this.keyStore = keyStore;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, JsonProcessingException {

		ResponseUtil.setResponse(response);

		// 获取认证成功后的用户信息对象
		JwtUserDetail userDetail = (JwtUserDetail) authentication.getPrincipal();
		String detailAsString = this.objectMapper.writeValueAsString(userDetail);

		// 30min timeout
		Token accessToken = JwtUtil.createTokenByRSA256(this.keyStore.getPrivateKey(), userDetail.getUsername(),
				detailAsString, 1800);
		Token refreshToken = JwtUtil.createTokenByRSA256(this.keyStore.getPrivateKey(), userDetail.getUsername(),
				detailAsString, 60 * 60 * 24 * 7);
		AuthResponse res = new AuthResponse();
		if (!accessToken.isError() && !refreshToken.isError()) {
			res.setCode(HttpStatus.OK.value());
			res.setStatus(HttpStatus.OK);
			response.setHeader(AuthConstant.HEAD_AUTHORIZATION, accessToken.getToken());
			response.setHeader(AuthConstant.HEAD_REFRESH_T, refreshToken.getToken());
		} else {
			res.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			res.setMsg(AuthConstant.MSG_SYS_ERROR);
		}
		ResponseUtil.write(response, this.objectMapper.writeValueAsString(res));
	}

}
