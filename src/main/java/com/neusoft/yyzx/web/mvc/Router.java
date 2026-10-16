package com.neusoft.yyzx.web.mvc;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 极简路由表 + 参数绑定分发器（纯反射，无第三方依赖）。
 * <p>
 * 注册带 {@link RestApi} / {@link Route} 注解的控制器，按 HTTP 方法 + 路径（支持 {var}）匹配，
 * 再依据方法参数上的 {@link Path} / {@link Query} / {@link Body} / {@link CurrentUser} 注解读取并转换实参，
 * 反射调用目标方法，返回其 {@code Result} 结果。
 */
public final class Router {

    /** 一条已注册路由。 */
    private static final class Entry {
        final Http http;
        final String[] segments; // 路径分段（已去空、{var} 原样保留）
        final Object controller;
        final Method method;

        Entry(Http http, String[] segments, Object controller, Method method) {
            this.http = http;
            this.segments = segments;
            this.controller = controller;
            this.method = method;
        }
    }

    private final List<Entry> entries = new ArrayList<>();

    /** 扫描并注册一个控制器实例的所有路由。 */
    public void register(Object controller) {
        Class<?> type = controller.getClass();
        RestApi base = type.getAnnotation(RestApi.class);
        if (base == null) {
            throw new IllegalArgumentException("控制器缺少 @RestApi: " + type.getName());
        }
        for (Method m : type.getDeclaredMethods()) {
            Route route = m.getAnnotation(Route.class);
            if (route == null) {
                continue;
            }
            String full = joinPath(base.value(), route.path());
            m.setAccessible(true);
            entries.add(new Entry(route.method(), splitPath(full), controller, m));
        }
    }

    /**
     * 匹配并调用；找不到路由返回 {@code null}（由上层转 404/错误）。
     *
     * @param currentUser 鉴权通过的当前用户，可为 null
     */
    public Object dispatch(String httpMethod, String path, Map<String, String> query,
                           Map<String, Object> body, Object currentUser, String token) throws Exception {
        String[] reqSegs = splitPath(path);
        Entry best = null;
        Map<String, String> bestVars = null;
        int bestVarCount = Integer.MAX_VALUE;
        for (Entry e : entries) {
            if (!e.http.name().equalsIgnoreCase(httpMethod)) {
                continue;
            }
            Map<String, String> pathVars = new java.util.HashMap<>();
            if (e.segments.length == reqSegs.length && matchSegments(e.segments, reqSegs, pathVars)) {
                // 字面量段优先：选择“变量段最少”的路由，避免 /customers/{id} 抢走 /customers/by-butler
                int varCount = countVars(e.segments);
                if (varCount < bestVarCount) {
                    best = e;
                    bestVars = pathVars;
                    bestVarCount = varCount;
                    if (varCount == 0) {
                        break; // 完全字面量匹配，最优
                    }
                }
            }
        }
        if (best == null) {
            return null;
        }
        Object[] args = buildArgs(best.method, bestVars, query, body, currentUser, token);
        try {
            return best.method.invoke(best.controller, args);
        } catch (java.lang.reflect.InvocationTargetException ite) {
            Throwable cause = ite.getCause();
            if (cause instanceof Exception ex) {
                throw ex;
            }
            throw ite;
        }
    }

    private static int countVars(String[] segments) {
        int n = 0;
        for (String s : segments) {
            if (s.length() > 1 && s.charAt(0) == '{' && s.charAt(s.length() - 1) == '}') {
                n++;
            }
        }
        return n;
    }

    private boolean matchSegments(String[] pattern, String[] actual, Map<String, String> out) {
        for (int i = 0; i < pattern.length; i++) {
            String p = pattern[i];
            if (p.length() > 1 && p.charAt(0) == '{' && p.charAt(p.length() - 1) == '}') {
                out.put(p.substring(1, p.length() - 1), actual[i]);
            } else if (!p.equals(actual[i])) {
                return false;
            }
        }
        return true;
    }

    private Object[] buildArgs(Method method, Map<String, String> pathVars,
                               Map<String, String> query, Map<String, Object> body, Object currentUser, String token) {
        Parameter[] params = method.getParameters();
        Object[] args = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            Parameter p = params[i];
            Class<?> type = p.getType();
            if (p.isAnnotationPresent(CurrentUser.class)) {
                args[i] = currentUser;
            } else if (p.isAnnotationPresent(Token.class)) {
                args[i] = token;
            } else if (p.isAnnotationPresent(Path.class)) {
                String name = p.getAnnotation(Path.class).value();
                String raw = pathVars.get(name);
                if (raw == null) {
                    throw new IllegalArgumentException("缺少路径参数: " + name);
                }
                args[i] = Bind.scalar(raw, type);
            } else if (p.isAnnotationPresent(Query.class)) {
                Query q = p.getAnnotation(Query.class);
                String raw = query.get(q.value());
                if (raw == null || raw.isEmpty()) {
                    // 必填参数“缺失”或“传了空值”都统一给友好提示；
                    // 非必填才回退默认值（无默认值时保持原样，由 Bind 处理）。
                    if (q.required()) {
                        throw new IllegalArgumentException("缺少必要参数: " + q.value());
                    }
                    if (!q.defaultValue().isEmpty()) {
                        raw = q.defaultValue();
                    }
                }
                args[i] = Bind.scalar(raw, type);
            } else if (p.isAnnotationPresent(Body.class)) {
                if (Map.class.isAssignableFrom(type)) {
                    args[i] = body;
                } else {
                    args[i] = Bind.toBean(body, type);
                }
            } else {
                args[i] = null;
            }
        }
        return args;
    }

    /* ---------- 路径工具 ---------- */

    private static String joinPath(String base, String path) {
        String b = base == null ? "" : base.trim();
        String p = path == null ? "" : path.trim();
        if (b.endsWith("/")) {
            b = b.substring(0, b.length() - 1);
        }
        if (!p.isEmpty() && !p.startsWith("/")) {
            p = "/" + p;
        }
        return b + p;
    }

    private static String[] splitPath(String path) {
        List<String> segs = new ArrayList<>();
        for (String s : path.split("/")) {
            if (!s.isEmpty()) {
                segs.add(s);
            }
        }
        return segs.toArray(new String[0]);
    }
}
