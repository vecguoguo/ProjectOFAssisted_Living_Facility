package com.neusoft.yyzx.web.controller;

import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.common.result.ResultCode;
import com.neusoft.yyzx.entity.User;
import com.neusoft.yyzx.service.UserService;
import com.neusoft.yyzx.service.impl.UserServiceImpl;
import com.neusoft.yyzx.web.mvc.Body;
import com.neusoft.yyzx.web.mvc.CurrentUser;
import com.neusoft.yyzx.web.mvc.Http;
import com.neusoft.yyzx.web.mvc.Path;
import com.neusoft.yyzx.web.mvc.Query;
import com.neusoft.yyzx.web.mvc.RestApi;
import com.neusoft.yyzx.web.mvc.Route;

import java.util.List;

/**
 * 用户管理接口（管理员）。
 */
@RestApi("/api/users")
public class UserController {

    private final UserService userService = new UserServiceImpl();

    /** 条件查询用户列表（昵称模糊、角色） */
    @Route
    public Result<List<User>> list(@Query(value = "nickname", required = false) String nickname,
                                   @Query(value = "roleId", required = false) Integer roleId) {
        return Result.success(userService.listByCondition(nickname, roleId));
    }

    /** 根据 id 查询用户 */
    @Route(method = Http.GET, path = "/{id}")
    public Result<User> findById(@Path("id") int id) {
        User user = userService.findById(id);
        if (user == null) {
            return Result.fail(ResultCode.USER_NOT_FOUND);
        }
        return Result.success(user);
    }

    /** 新增用户 */
    @Route(method = Http.POST)
    public Result<Integer> add(@Body User user) {
        return userService.addUser(user);
    }

    /** 修改用户 */
    @Route(method = Http.PUT, path = "/{id}")
    public Result<Void> update(@Path("id") int id, @Body User user) {
        user.setId(id);
        return userService.updateUser(user);
    }

    /** 重置密码：返回重置后的明文密码 */
    @Route(method = Http.PUT, path = "/{id}/password")
    public Result<String> resetPassword(@Path("id") int id,
                                        @CurrentUser User currentUser) {
        return userService.resetPassword(id, currentUser.getId());
    }

    /** 删除用户（逻辑删除） */
    @Route(method = Http.DELETE, path = "/{id}")
    public Result<Void> delete(@Path("id") int id,
                               @CurrentUser User currentUser) {
        return userService.deleteUser(id, currentUser.getId());
    }
}
