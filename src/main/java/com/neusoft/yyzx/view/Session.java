package com.neusoft.yyzx.view;

import com.neusoft.yyzx.entity.User;

/**
 * 控制台会话：保存当前登录用户（模拟 web 版的登录态）
 */
public final class Session {

    private static User currentUser;

    private Session() {
    }

    public static void login(User user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isAdmin() {
        return currentUser != null && Integer.valueOf(1).equals(currentUser.getRoleId());
    }

    public static boolean isLogin() {
        return currentUser != null;
    }
}
