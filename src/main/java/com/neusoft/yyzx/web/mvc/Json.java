package com.neusoft.yyzx.web.mvc;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 极简 JSON 编解码工具（纯 JDK 反射实现，无第三方依赖）。
 * <p>
 * 仅覆盖本项目 REST 层所需能力，行为对齐原 Spring Boot + Jackson 配置：
 * <ul>
 *     <li>序列化：null 字段忽略（non_null），日期以 ISO 字符串输出；POJO 按 public getter 展开。</li>
 *     <li>解析：字符串→{@code String}、整数→{@code Long}、小数→{@code Double}、
 *         对象→{@code LinkedHashMap}、数组→{@code ArrayList}、布尔、null。</li>
 * </ul>
 */
public final class Json {

    private Json() {
    }

    /* ==================== 序列化 ==================== */

    public static String write(Object value) {
        StringBuilder sb = new StringBuilder();
        writeValue(sb, value);
        return sb.toString();
    }

    private static void writeValue(StringBuilder sb, Object v) {
        if (v == null) {
            sb.append("null");
        } else if (v instanceof CharSequence || v instanceof Character) {
            writeString(sb, v.toString());
        } else if (v instanceof Boolean b) {
            sb.append(b ? "true" : "false");
        } else if (v instanceof Number n) {
            sb.append(n);
        } else if (v instanceof Enum<?> e) {
            writeString(sb, e.name());
        } else if (v instanceof LocalDate || v instanceof LocalDateTime
                || v instanceof LocalTime || v instanceof java.sql.Date) {
            writeString(sb, v.toString());
        } else if (v instanceof Map<?, ?> map) {
            writeMap(sb, map);
        } else if (v instanceof Collection<?> coll) {
            writeArray(sb, coll.toArray());
        } else if (v.getClass().isArray()) {
            writeArrayReflect(sb, v);
        } else {
            writeBean(sb, v);
        }
    }

    private static void writeMap(StringBuilder sb, Map<?, ?> map) {
        sb.append('{');
        boolean first = true;
        for (Map.Entry<?, ?> e : map.entrySet()) {
            if (e.getValue() == null) {
                continue; // non_null
            }
            if (!first) {
                sb.append(',');
            }
            first = false;
            writeString(sb, String.valueOf(e.getKey()));
            sb.append(':');
            writeValue(sb, e.getValue());
        }
        sb.append('}');
    }

    private static void writeArray(StringBuilder sb, Object[] arr) {
        sb.append('[');
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            writeValue(sb, arr[i]);
        }
        sb.append(']');
    }

    private static void writeArrayReflect(StringBuilder sb, Object array) {
        int len = Array.getLength(array);
        sb.append('[');
        for (int i = 0; i < len; i++) {
            if (i > 0) {
                sb.append(',');
            }
            writeValue(sb, Array.get(array, i));
        }
        sb.append(']');
    }

    /** 按 public getter 序列化 POJO，值为 null 的字段跳过。 */
    private static void writeBean(StringBuilder sb, Object bean) {
        sb.append('{');
        boolean first = true;
        for (Method m : bean.getClass().getMethods()) {
            String prop = propName(m);
            if (prop == null || m.getParameterCount() != 0) {
                continue;
            }
            Object val;
            try {
                val = m.invoke(bean);
            } catch (ReflectiveOperationException e) {
                continue;
            }
            if (val == null) {
                continue; // non_null
            }
            if (!first) {
                sb.append(',');
            }
            first = false;
            writeString(sb, prop);
            sb.append(':');
            writeValue(sb, val);
        }
        sb.append('}');
    }

    /** 由 getter 方法名推导属性名（对齐 Jackson：getXxx/isXxx → xxx）。返回 null 表示非 getter。 */
    private static String propName(Method m) {
        if (m.isSynthetic() || java.lang.reflect.Modifier.isStatic(m.getModifiers())) {
            return null;
        }
        String name = m.getName();
        Class<?> rt = m.getReturnType();
        if (rt == void.class || rt == Void.class) {
            return null;
        }
        if (name.startsWith("get") && name.length() > 3 && !name.equals("getClass")) {
            return decapitalize(name.substring(3));
        }
        if (name.startsWith("is") && name.length() > 2
                && (rt == boolean.class || rt == Boolean.class)) {
            return decapitalize(name.substring(2));
        }
        return null;
    }

    private static String decapitalize(String s) {
        if (s.isEmpty()) {
            return s;
        }
        // 前两位都大写时保持原样（如 URL），与 java.beans.Introspector 一致
        if (s.length() > 1 && Character.isUpperCase(s.charAt(0)) && Character.isUpperCase(s.charAt(1))) {
            return s;
        }
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    private static void writeString(StringBuilder sb, String s) {
        sb.append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        sb.append('"');
    }

    /* ==================== 反序列化 ==================== */

    public static Object parse(String text) {
        if (text == null) {
            return null;
        }
        Parser p = new Parser(text);
        p.skipWs();
        if (p.eof()) {
            return null;
        }
        Object v = p.readValue();
        p.skipWs();
        if (!p.eof()) {
            throw new IllegalArgumentException("JSON 结尾存在多余内容");
        }
        return v;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseObject(String text) {
        Object v = parse(text);
        if (v == null) {
            return new LinkedHashMap<>();
        }
        if (!(v instanceof Map)) {
            throw new IllegalArgumentException("请求体必须是 JSON 对象");
        }
        return (Map<String, Object>) v;
    }

    private static final class Parser {
        private final String s;
        private int i;

        Parser(String s) {
            this.s = s;
        }

        boolean eof() {
            return i >= s.length();
        }

        void skipWs() {
            while (i < s.length()) {
                char c = s.charAt(i);
                if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                    i++;
                } else {
                    break;
                }
            }
        }

        Object readValue() {
            skipWs();
            if (eof()) {
                throw new IllegalArgumentException("JSON 意外结束");
            }
            char c = s.charAt(i);
            return switch (c) {
                case '{' -> readObject();
                case '[' -> readArray();
                case '"' -> readString();
                case 't', 'f' -> readBool();
                case 'n' -> readNull();
                default -> readNumber();
            };
        }

        Map<String, Object> readObject() {
            Map<String, Object> map = new LinkedHashMap<>();
            i++; // {
            skipWs();
            if (!eof() && s.charAt(i) == '}') {
                i++;
                return map;
            }
            while (true) {
                skipWs();
                String key = readString();
                skipWs();
                expect(':');
                Object val = readValue();
                map.put(key, val);
                skipWs();
                char d = next();
                if (d == ',') {
                    continue;
                } else if (d == '}') {
                    break;
                } else {
                    throw new IllegalArgumentException("JSON 对象解析异常");
                }
            }
            return map;
        }

        List<Object> readArray() {
            List<Object> list = new java.util.ArrayList<>();
            i++; // [
            skipWs();
            if (!eof() && s.charAt(i) == ']') {
                i++;
                return list;
            }
            while (true) {
                Object val = readValue();
                list.add(val);
                skipWs();
                char d = next();
                if (d == ',') {
                    continue;
                } else if (d == ']') {
                    break;
                } else {
                    throw new IllegalArgumentException("JSON 数组解析异常");
                }
            }
            return list;
        }

        String readString() {
            expect('"');
            StringBuilder sb = new StringBuilder();
            while (true) {
                if (eof()) {
                    throw new IllegalArgumentException("字符串未闭合");
                }
                char c = s.charAt(i++);
                if (c == '"') {
                    break;
                } else if (c == '\\') {
                    char e = s.charAt(i++);
                    switch (e) {
                        case '"' -> sb.append('"');
                        case '\\' -> sb.append('\\');
                        case '/' -> sb.append('/');
                        case 'b' -> sb.append('\b');
                        case 'f' -> sb.append('\f');
                        case 'n' -> sb.append('\n');
                        case 'r' -> sb.append('\r');
                        case 't' -> sb.append('\t');
                        case 'u' -> {
                            String hex = s.substring(i, i + 4);
                            i += 4;
                            sb.append((char) Integer.parseInt(hex, 16));
                        }
                        default -> throw new IllegalArgumentException("非法转义 \\" + e);
                    }
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }

        Object readBool() {
            if (s.startsWith("true", i)) {
                i += 4;
                return Boolean.TRUE;
            }
            if (s.startsWith("false", i)) {
                i += 5;
                return Boolean.FALSE;
            }
            throw new IllegalArgumentException("非法字面量");
        }

        Object readNull() {
            if (s.startsWith("null", i)) {
                i += 4;
                return null;
            }
            throw new IllegalArgumentException("非法字面量");
        }

        Object readNumber() {
            int start = i;
            boolean frac = false;
            if (!eof() && (s.charAt(i) == '-' || s.charAt(i) == '+')) {
                i++;
            }
            while (!eof()) {
                char c = s.charAt(i);
                if (c >= '0' && c <= '9') {
                    i++;
                } else if (c == '.' || c == 'e' || c == 'E' || c == '-' || c == '+') {
                    if (c == '.' || c == 'e' || c == 'E') {
                        frac = true;
                    }
                    i++;
                } else {
                    break;
                }
            }
            String num = s.substring(start, i);
            if (num.isEmpty()) {
                throw new IllegalArgumentException("非法数字");
            }
            if (frac) {
                return Double.valueOf(num);
            }
            return Long.valueOf(num);
        }

        char next() {
            if (eof()) {
                throw new IllegalArgumentException("JSON 意外结束");
            }
            return s.charAt(i++);
        }

        void expect(char c) {
            if (eof() || s.charAt(i) != c) {
                throw new IllegalArgumentException("期望字符 " + c);
            }
            i++;
        }
    }
}
