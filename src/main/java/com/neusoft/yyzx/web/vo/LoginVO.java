package com.neusoft.yyzx.web.vo;

import com.neusoft.yyzx.entity.User;

/**
 * 登录成功返回体：token + 用户信息。
 */
public class LoginVO {

    private String token;
    private User user;

    public LoginVO() {
    }

    public LoginVO(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
