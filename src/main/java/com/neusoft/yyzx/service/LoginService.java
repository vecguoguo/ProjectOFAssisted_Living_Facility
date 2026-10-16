package com.neusoft.yyzx.service;

import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.entity.User;

import java.util.List;

/**
 * 登录与用户管理业务接口
 */
public interface LoginService {
    /**
     * 登录
     * @param username 账号
     * @param password 密码
     * @return 登录结果
     */
    public Result<User> login(String username, String password);

    /**
     * 新增系统用户（用户管理）
     * @param user 用户对象
     * @return 新增结果
     */
    public Result<Integer> addUser(User user);

    /**
     * 查询全部用户
     * @return 用户列表
     */
    public List<User> listAll();
}
