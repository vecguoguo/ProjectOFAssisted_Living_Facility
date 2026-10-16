package com.neusoft.yyzx.web.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法级注解：声明一个路由（HTTP 方法 + 相对路径）。
 * 路径支持 {var} 占位符，如 "/{id}"、"/{id}/audit"。空串表示仅使用类上的基础路径。
 * 替代 Spring 的 @GetMapping / @PostMapping / @PutMapping / @DeleteMapping。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Route {
    Http method() default Http.GET;

    String path() default "";
}
