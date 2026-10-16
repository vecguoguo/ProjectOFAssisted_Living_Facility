package com.neusoft.yyzx.web.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 类级注解：标记一个 REST 控制器，并声明其基础路径（如 /api/beds）。
 * 替代 Spring 的 {@code @RestController + @RequestMapping}。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RestApi {
    String value();
}
