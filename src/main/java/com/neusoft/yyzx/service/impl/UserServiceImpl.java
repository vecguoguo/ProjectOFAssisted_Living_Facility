package com.neusoft.yyzx.service.impl;

import com.neusoft.yyzx.common.constant.RoleConstant;
import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.common.result.ResultCode;
import com.neusoft.yyzx.common.utils.PasswordUtil;
import com.neusoft.yyzx.dao.impl.UserDAOImpl;
import com.neusoft.yyzx.db.TxManager;
import com.neusoft.yyzx.entity.User;
import com.neusoft.yyzx.service.UserService;

import java.util.List;

public class UserServiceImpl implements UserService {

    @Override
    public List<User> listByCondition(String nickname, Integer roleId) {
        return TxManager.query(conn -> {
            UserDAOImpl userDAO = new UserDAOImpl();
            userDAO.setConnection(conn);
            return userDAO.listByCondition(nickname, roleId);
        });
    }

    @Override
    public User findById(int id) {
        return TxManager.query(conn -> {
            UserDAOImpl userDAO = new UserDAOImpl();
            userDAO.setConnection(conn);
            return userDAO.findById(id);
        });
    }

    /**
     * 新增用户：账号唯一校验 -> 健康管家默认密码为手机号后6位 -> 保存
     */
    @Override
    public Result<Integer> addUser(User user) {
        return TxManager.tx(conn -> {
            if (user.getUsername() == null || user.getUsername().isBlank()
                    || user.getNickname() == null || user.getNickname().isBlank()) {
                return Result.fail(ResultCode.PARAM_ERROR);
            }
            UserDAOImpl userDAO = new UserDAOImpl();
            userDAO.setConnection(conn);
            if (userDAO.findByUsername(user.getUsername()) != null) {
                return Result.fail(ResultCode.USERNAME_EXISTS);
            }
            // 健康管家由管理员统一创建，密码默认手机号后6位；管理员使用录入的密码
            if (RoleConstant.HEALTH_BUTLER.equals(user.getRoleId())) {
                user.setPassword(defaultPassword(user.getPhoneNumber()));
            }
            user.setPassword(PasswordUtil.hash(user.getPassword()));
            int id = userDAO.insert(user);
            return Result.success(id);
        });
    }

    @Override
    public Result<Void> updateUser(User user) {
        return TxManager.tx(conn -> {
            UserDAOImpl userDAO = new UserDAOImpl();
            userDAO.setConnection(conn);
            User exist = userDAO.findById(user.getId());
            if (exist == null) {
                return Result.fail(ResultCode.USER_NOT_FOUND);
            }
            userDAO.updateUser(user);
            return Result.success();
        });
    }

    /**
     * 重置密码为手机号后6位
     */
    @Override
    public Result<String> resetPassword(int id, Integer operatorId) {
        return TxManager.tx(conn -> {
            UserDAOImpl userDAO = new UserDAOImpl();
            userDAO.setConnection(conn);
            User exist = userDAO.findById(id);
            if (exist == null) {
                return Result.fail(ResultCode.USER_NOT_FOUND);
            }
            String newPwd = defaultPassword(exist.getPhoneNumber());
            userDAO.resetPassword(id, PasswordUtil.hash(newPwd), operatorId);
            return Result.success(newPwd);
        });
    }

    /**
     * 逻辑删除：不能删除当前登录用户
     */
    @Override
    public Result<Void> deleteUser(int id, Integer operatorId) {
        return TxManager.tx(conn -> {
            UserDAOImpl userDAO = new UserDAOImpl();
            userDAO.setConnection(conn);
            User exist = userDAO.findById(id);
            if (exist == null) {
                return Result.fail(ResultCode.USER_NOT_FOUND);
            }
            if (operatorId != null && operatorId == id) {
                return Result.fail(ResultCode.USER_DELETE_SELF);
            }
            userDAO.logicDelete(id);
            return Result.success();
        });
    }

    /** 默认密码：手机号后6位，号码不足6位时回退为 123456 */
    private String defaultPassword(String phone) {
        if (phone == null || phone.length() < 6) {
            return "123456";
        }
        return phone.substring(phone.length() - 6);
    }
}
