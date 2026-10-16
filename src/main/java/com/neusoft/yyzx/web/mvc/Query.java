package com.neusoft.yyzx.web.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 参数注解：绑定 URL 查询串参数（?roomNo=101&nameLike=张）。替代 Spring 的 @RequestParam。
 * <ul>
 *     <li>{@code required=true}（默认）：缺失时抛 IllegalArgumentException（转 400）。</li>
 *     <li>{@code required=false}：缺失时，对象类型给 null，基本类型给 {@code defaultValue} 解析值。</li>
 * </ul>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {
    String value();

    boolean required() default true;

    String defaultValue() default "";
}
