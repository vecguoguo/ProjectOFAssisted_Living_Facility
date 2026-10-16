package com.neusoft.yyzx.web.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 请求参数取值工具：用于 {@code Map<String,Object>} 形式的请求体，
 * 统一处理 null / 类型转换 / 日期解析，避免 Controller 里重复写转换代码。
 */
public final class ParamUtil {

    private ParamUtil() {
    }

    /** 取字符串，不存在返回 null */
    public static String getStr(Map<String, Object> body, String key) {
        Object v = body.get(key);
        return v == null ? null : v.toString();
    }

    /** 取字符串，空串归一化为 null（便于“可选模糊查询”场景） */
    public static String getStrOrNull(Map<String, Object> body, String key) {
        String s = getStr(body, key);
        return s == null || s.trim().isEmpty() ? null : s.trim();
    }

    /** 取 Integer，不存在返回 null */
    public static Integer getInt(Map<String, Object> body, String key) {
        Object v = body.get(key);
        if (v == null || v.toString().trim().isEmpty()) {
            return null;
        }
        if (v instanceof Number n) {
            return n.intValue();
        }
        try {
            return Integer.valueOf(v.toString().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** 取 int，不存在返回默认值 */
    public static int getInt(Map<String, Object> body, String key, int defaultValue) {
        Integer v = getInt(body, key);
        return v == null ? defaultValue : v;
    }

    /** 取必填 int，缺失或非法时抛 IllegalArgumentException（由全局异常处理转成 400） */
    public static int requireInt(Map<String, Object> body, String key) {
        Integer v = getInt(body, key);
        if (v == null) {
            throw new IllegalArgumentException("缺少必要参数: " + key);
        }
        return v;
    }

    /** 取布尔，不存在返回默认值 */
    public static boolean getBool(Map<String, Object> body, String key, boolean defaultValue) {
        Object v = body.get(key);
        if (v == null) {
            return defaultValue;
        }
        if (v instanceof Boolean b) {
            return b;
        }
        return Boolean.parseBoolean(v.toString());
    }

    /** 取 LocalDate（yyyy-MM-dd 或 ISO 格式），空返回 null */
    public static LocalDate getDate(Map<String, Object> body, String key) {
        String s = getStr(body, key);
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        s = s.trim();
        // 兼容 "2026-10-10" 与 "2026-10-10T00:00"
        if (s.length() > 10) {
            s = s.substring(0, 10);
        }
        return LocalDate.parse(s);
    }

    /** 取 LocalDateTime（yyyy-MM-ddTHH:mm 或 yyyy-MM-dd HH:mm），空返回 null */
    public static LocalDateTime getDateTime(Map<String, Object> body, String key) {
        String s = getStr(body, key);
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        s = s.trim().replace(' ', 'T');
        if (s.length() == 10) {
            return LocalDate.parse(s).atStartOfDay();
        }
        if (s.length() == 16) {
            return LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        }
        return LocalDateTime.parse(s);
    }

    /** 取必填字符串 */
    public static String requireStr(Map<String, Object> body, String key) {
        String s = getStr(body, key);
        if (s == null || s.trim().isEmpty()) {
            throw new IllegalArgumentException("缺少必要参数: " + key);
        }
        return s.trim();
    }
}
