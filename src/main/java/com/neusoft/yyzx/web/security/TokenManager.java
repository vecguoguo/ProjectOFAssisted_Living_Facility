package com.neusoft.yyzx.web.security;

import com.neusoft.yyzx.entity.User;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 极简登录态管理：token -> 当前登录用户（内存存储）。
 * <p>
 * 控制台版用静态 {@code Session} 保存登录用户，Web 版是多会话场景，
 * 因此改为“登录发 token、请求带 token”的方式。
 */
public final class TokenManager {

    private static final Map<String, User> TOKENS = new ConcurrentHashMap<>();

    private TokenManager() {
    }

    /** 登录成功后签发 token */
    public static String create(User user) {
        String token = UUID.randomUUID().toString().replace("-", "");
        TOKENS.put(token, user);
        return token;
    }

    /** 根据 token 取当前登录用户，未登录返回 null */
    public static User get(String token) {
        return token == null || token.isEmpty() ? null : TOKENS.get(token);
    }

    /** 退出登录 */
    public static void remove(String token) {
        if (token != null) {
            TOKENS.remove(token);
        }
    }
}
