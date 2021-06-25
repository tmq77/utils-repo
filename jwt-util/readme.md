### Get Start

1. 打包方式:

   使用`clean assembly:assembly`进行打包(`assembly:assembly`命令会将依赖项一并打包) 
### Annotation

1. 自带一个注解`@SkipToken`，该注解可以用于方法上,在拦截器中获取标注了该注解的方法后,判断是否需要进行Token验证。

   举例:

   ```java
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
           // 判断是否有对应的注解
           if (method.isAnnotationPresent(SkipToken.class)) {
           	SkipToken skipToken = method.getAnnotation(SkipToken.class);
               if (skipToken.value()) {
           		return true;
           	}
           }
           // 从header部获取token
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
   ```

   

   

   

