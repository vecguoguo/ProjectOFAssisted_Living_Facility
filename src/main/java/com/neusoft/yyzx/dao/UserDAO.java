package com.neusoft.yyzx.dao;

import com.neusoft.yyzx.entity.User;

import java.util.List;

public interface UserDAO {

    public User findByUsername(String username);

    public List<User> listAll();

    public int insert(User u);

    public int logicDelete(int id);

    /**
     * 根据ID查询未删除用户
     * @param id 用户ID
     * @return 用户对象，不存在返回null
     */
    public User findById(int id);

    /**
     * 多条件查询用户（昵称/角色，null 忽略）
     * @param nickname 真实姓名（模糊）
     * @param roleId 角色ID
     * @return 用户列表
     */
    public List<User> listByCondition(String nickname, Integer roleId);

    /**
     * 修改用户基本信息（昵称/性别/邮箱/电话/角色）
     * @param u 用户对象
     * @return 影响行数
     */
    public int updateUser(User u);

    /**
     * 重置密码
     * @param id 用户ID
     * @param password 新密码
     * @param updateBy 操作人
     * @return 影响行数
     */
    public int resetPassword(int id, String password, Integer updateBy);

    /**
     * 按角色ID+姓名模糊查询（健康管家列表用）
     * @param roleId 角色ID
     * @param nameLike 姓名模糊关键字，null不限
     * @return 用户列表
     */
    public List<User> listByRoleIdAndNicknameLike(int roleId, String nameLike);
}