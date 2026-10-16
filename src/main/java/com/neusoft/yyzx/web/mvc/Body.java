package com.neusoft.yyzx.web.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 参数注解：绑定请求体 JSON。替代 Spring 的 @RequestBody。
 * 目标参数类型可为 {@code Map<String,Object>}、{@code Map<String,String>} 或任意实体 POJO。
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Body {
}
