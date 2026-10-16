package com.neusoft.yyzx.web.controller;

import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.common.result.ResultCode;
import com.neusoft.yyzx.entity.User;
import com.neusoft.yyzx.service.LoginService;
import com.neusoft.yyzx.service.impl.LoginServiceImpl;
import com.neusoft.yyzx.web.mvc.Body;
import com.neusoft.yyzx.web.mvc.CurrentUser;
import com.neusoft.yyzx.web.mvc.Http;
import com.neusoft.yyzx.web.mvc.RestApi;
import com.neusoft.yyzx.web.mvc.Route;
import com.neusoft.yyzx.web.mvc.Token;
import com.neusoft.yyzx.web.security.TokenManager;
import com.neusoft.yyzx.web.vo.LoginVO;

import java.util.Map;

/**
 * 认证接口：登录 / 退出 / 当前用户。
 */
@RestApi("/api/auth")
public class AuthController {

    private final LoginService loginService = new LoginServiceImpl();

    /** 登录：成功返回 token + 用户信息 */
    @Route(method = Http.POST, path = "/login")
    public Result<LoginVO> login(@Body Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || username.trim().isEmpty()
                || password == null || password.isEmpty()) {
            return Result.fail(ResultCode.PARAM_ERROR);
        }
        Result<User> result = loginService.login(username.trim(), password);
        if (!result.isSuccess()) {
            return Result.fail(ResultCode.LOGIN_ERROR);
        }
        String token = TokenManager.create(result.getData());
        return Result.success(new LoginVO(token, result.getData()));
    }

    /** 退出登录 */
    @Route(method = Http.POST, path = "/logout")
    public Result<Void> logout(@Token String token) {
        TokenManager.remove(token);
        return Result.success();
    }

    /** 获取当前登录用户 */
    @Route(method = Http.GET, path = "/me")
    public Result<User> me(@CurrentUser User currentUser) {
        return Result.success(currentUser);
    }
}
