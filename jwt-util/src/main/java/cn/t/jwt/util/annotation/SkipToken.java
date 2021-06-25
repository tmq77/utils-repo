package cn.t.jwt.util.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 自定义注解,拥有该注解的方法将会跳过Token验证
 * 跳过验证的逻辑由拦截器或者过滤器或者AOP实现
 * @author tmq
 *
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface SkipToken {
	/**
	 * 是否跳过该方法
	 * true:跳过验证该方法/请求
	 * 默认:true
	 * @return
	 */
	boolean value() default true;
}
