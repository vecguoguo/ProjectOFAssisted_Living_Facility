package com.neusoft.yyzx.web.mvc;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 参数类型转换工具（纯 JDK）：
 * <ul>
 *     <li>{@link #scalar} —— 把 URL 查询串 / 路径占位符里的字符串转成方法参数目标类型。</li>
 *     <li>{@link #fromJsonValue} —— 把已解析的 JSON 值（Long/Double/Boolean/String/null）转成目标类型。</li>
 *     <li>{@link #toBean} —— 把 JSON 对象（Map）按 setter 绑定到实体 POJO，未知字段忽略。</li>
 * </ul>
 */
public final class Bind {

    private Bind() {
    }

    /** 字符串 → 目标类型（用于 @Path / @Query）。 */
    public static Object scalar(String raw, Class<?> target) {
        if (target == String.class || CharSequence.class.isAssignableFrom(target)) {
            return raw;
        }
        if (raw == null || raw.trim().isEmpty()) {
            return defaultOf(target);
        }
        String v = raw.trim();
        try {
            if (target == int.class || target == Integer.class) {
                return Integer.valueOf(v);
            }
            if (target == long.class || target == Long.class) {
                return Long.valueOf(v);
            }
            if (target == double.class || target == Double.class) {
                return Double.valueOf(v);
            }
            if (target == boolean.class || target == Boolean.class) {
                return Boolean.valueOf(v);
            }
            if (target == LocalDate.class) {
                return parseDate(v);
            }
            if (target == LocalDateTime.class) {
                return parseDateTime(v);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("参数格式错误: " + v);
        }
        return v;
    }

    /** JSON 值 → 目标类型（用于 @Body 实体字段）。 */
    @SuppressWarnings("unchecked")
    public static Object fromJsonValue(Object v, Class<?> target) {
        if (v == null) {
            return defaultOf(target);
        }
        if (target == String.class || CharSequence.class.isAssignableFrom(target)) {
            return v.toString();
        }
        if (target == Integer.class || target == int.class) {
            return v instanceof Number n ? n.intValue() : Integer.valueOf(v.toString().trim());
        }
        if (target == Long.class || target == long.class) {
            return v instanceof Number n ? n.longValue() : Long.valueOf(v.toString().trim());
        }
        if (target == Double.class || target == double.class) {
            return v instanceof Number n ? n.doubleValue() : Double.valueOf(v.toString().trim());
        }
        if (target == Boolean.class || target == boolean.class) {
            if (v instanceof Boolean b) {
                return b;
            }
            if (v instanceof Number n) {
                return n.intValue() != 0;
            }
            return Boolean.valueOf(v.toString().trim());
        }
        if (target == LocalDate.class) {
            return parseDate(v.toString());
        }
        if (target == LocalDateTime.class) {
            return parseDateTime(v.toString());
        }
        if (Map.class.isAssignableFrom(target) || List.class.isAssignableFrom(target)) {
            return v;
        }
        return v;
    }

    /** 把 JSON 对象绑定为实体：按 setter 名匹配属性，未知字段忽略（对齐 Jackson FAIL_ON_UNKNOWN=false）。 */
    public static <T> T toBean(Map<String, Object> map, Class<T> type) {
        T bean;
        try {
            bean = type.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("无法实例化目标类型: " + type.getName());
        }
        Map<String, Method> setters = new HashMap<>();
        for (Method m : type.getMethods()) {
            String name = m.getName();
            if (name.startsWith("set") && name.length() > 3 && m.getParameterCount() == 1) {
                setters.put(decapitalize(name.substring(3)), m);
            }
        }
        for (Map.Entry<String, Object> e : map.entrySet()) {
            Method setter = setters.get(e.getKey());
            if (setter == null) {
                continue; // 忽略未知字段
            }
            Object converted = fromJsonValue(e.getValue(), setter.getParameterTypes()[0]);
            try {
                setter.invoke(bean, converted);
            } catch (ReflectiveOperationException ex) {
                throw new IllegalArgumentException("字段绑定失败: " + e.getKey());
            }
        }
        return bean;
    }

    private static Object defaultOf(Class<?> target) {
        if (target == int.class) {
            return 0;
        }
        if (target == long.class) {
            return 0L;
        }
        if (target == double.class) {
            return 0d;
        }
        if (target == boolean.class) {
            return false;
        }
        return null;
    }

    private static LocalDate parseDate(String v) {
        String s = v.trim();
        if (s.length() > 10) {
            s = s.substring(0, 10);
        }
        return LocalDate.parse(s);
    }

    private static LocalDateTime parseDateTime(String v) {
        String s = v.trim().replace(' ', 'T');
        if (s.length() == 10) {
            return LocalDate.parse(s).atStartOfDay();
        }
        if (s.length() == 16) {
            return LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        }
        return LocalDateTime.parse(s);
    }

    private static String decapitalize(String s) {
        if (s.isEmpty()) {
            return s;
        }
        if (s.length() > 1 && Character.isUpperCase(s.charAt(0)) && Character.isUpperCase(s.charAt(1))) {
            return s;
        }
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }
}
