package cn.t.test.interceptor;



import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import cn.t.jwt.util.JwtUtil;
import cn.t.jwt.util.annotation.SkipToken;
import cn.t.jwt.util.token.Token;

/**
 * Token拦截器
 * @author tmq
 *
 */
public class TokenInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 如果不是映射到方法直接通过
        if(!(handler instanceof HandlerMethod)){
            return true;
        }
        // 获取当前方法
        Method method = ((HandlerMethod)handler).getMethod();
        if (method.isAnnotationPresent(SkipToken.class)) {
        	SkipToken skipToken = method.getAnnotation(SkipToken.class);
        	if (skipToken.value()) {
        		return true;
        	}
        }
		String token = request.getHeader("token");
		Token result = JwtUtil.verifyTokenByHS256(token);
		if (result.isError()) {
			response.setContentType("application/json;charset=utf-8");
			response.getWriter().write("Invalid Token");;
			return false;
		}
		return true;
	}
}
