package com.neusoft.yyzx.service;

import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.entity.User;

import java.util.List;

/**
 * 用户管理业务接口（对应需求：系统用户维护）
 * 登录认证见 {@link LoginService}，本接口专注用户的增删改查
 */
public interface UserService {

    /**
     * 多条件查询用户（昵称/角色，null 忽略）
     * @param nickname 真实姓名（模糊）
     * @param roleId 角色ID
     * @return 用户列表
     */
    public List<User> listByCondition(String nickname, Integer roleId);

    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户对象，不存在返回null
     */
    public User findById(int id);

    /**
     * 新增用户：
     * 账号唯一校验；健康管家（角色2）密码默认为手机号后6位，管理员使用传入密码
     * @param user 用户对象
     * @return 新增结果（data为用户ID）
     */
    public Result<Integer> addUser(User user);

    /**
     * 修改用户基本信息
     * @param user 用户对象
     * @return 修改结果
     */
    public Result<Void> updateUser(User user);

    /**
     * 重置密码为手机号后6位
     * @param id 用户ID
     * @param operatorId 操作人ID
     * @return 重置结果（data为新密码）
     */
    public Result<String> resetPassword(int id, Integer operatorId);

    /**
     * 逻辑删除用户（不能删除当前登录用户）
     * @param id 用户ID
     * @param operatorId 操作人ID
     * @return 删除结果
     */
    public Result<Void> deleteUser(int id, Integer operatorId);
}
