package cn.t.auth.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.t.auth.constant.AuthConstant;
import cn.t.auth.entity.JwtAccessToken;
import cn.t.auth.entity.JwtUserDetail;
import cn.t.auth.response.AuthResponse;
import cn.t.auth.store.KeyStore;
import cn.t.auth.util.ResponseUtil;
import cn.t.jwt.util.JwtUtil;
import cn.t.jwt.util.data.Token;

/**
 * 在spring中，filter都默认继承OncePerRequestFilter<br>
 * 确保在一次请求只通过一次filter，而不需要重复执行
 * @author TMQ
 *
 */
public class JwtTokenFilter extends OncePerRequestFilter {

	private ObjectMapper objectMapper;

	private KeyStore keyStore;

	public JwtTokenFilter(ObjectMapper objectMapper, KeyStore keyStore) {
		this.keyStore = keyStore;
		this.objectMapper = objectMapper;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		ResponseUtil.setResponse(response);
		AuthResponse res = new AuthResponse();
		try {
			String header = request.getHeader(AuthConstant.HEAD_AUTHORIZATION);
			if (header == null || header.trim().isEmpty() || !header.startsWith(AuthConstant.PREFIX_BEARER)) {
				res.setCode(HttpStatus.FORBIDDEN.value());
				res.setStatus(HttpStatus.FORBIDDEN);
				res.setMsg(AuthConstant.MSG_NO_ACCESS);
				ResponseUtil.write(response, this.objectMapper.writeValueAsString(res));
				return;
			}

			String token = header.replace(AuthConstant.PREFIX_BEARER, "");
			Token result = JwtUtil.verifyTokenByRSA256(token, this.keyStore.getPublicKey());

			if (result.isError()) {
				res.setCode(HttpStatus.FORBIDDEN.value());
				res.setStatus(HttpStatus.FORBIDDEN);
				res.setMsg(AuthConstant.MSG_NO_ACCESS);
				ResponseUtil.write(response, this.objectMapper.writeValueAsString(res));
				return;
			}

			// 上下文中放入认证信息
			DecodedJWT decodedJwt = JwtUtil.parseToken(token);

			String userInfoStr = decodedJwt.getClaim(decodedJwt.getId()).asString();
			JwtUserDetail userDetail = this.objectMapper.readValue(userInfoStr, JwtUserDetail.class);

			// 生成认证成功的AbstractAuthenticationToken对象
			JwtAccessToken accessToken = new JwtAccessToken(userDetail.getUsername(), "", userDetail.getAuthorities());
			// 认证信息放入security框架上下文中,
			SecurityContextHolder.getContext().setAuthentication(accessToken);
			filterChain.doFilter(request, response);

		} catch (BadCredentialsException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			res.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			res.setMsg(AuthConstant.MSG_SYS_ERROR);
			ResponseUtil.write(response, this.objectMapper.writeValueAsString(res));
		}
	}
}
