package com.neusoft.yyzx.service.impl;

import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.common.result.ResultCode;
import com.neusoft.yyzx.common.utils.PasswordUtil;
import com.neusoft.yyzx.dao.impl.UserDAOImpl;
import com.neusoft.yyzx.db.TxManager;
import com.neusoft.yyzx.entity.User;
import com.neusoft.yyzx.service.LoginService;

import java.util.List;

public class LoginServiceImpl implements LoginService {

    @Override
    public Result<User> login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return Result.fail(ResultCode.PARAM_ERROR);
        }
        // 登录可能触发历史明文透明升级，需写库，故走事务模板
        return TxManager.tx(conn -> {
            UserDAOImpl userDAO = new UserDAOImpl();
            userDAO.setConnection(conn);
            User user = userDAO.findByUsername(username);
            if (user == null) {
                return Result.fail(ResultCode.LOGIN_ERROR);
            }
            String stored = user.getPassword();
            boolean ok;
            boolean legacyUpgrade = false;
            if (PasswordUtil.isHashed(stored)) {
                ok = PasswordUtil.matches(password, stored);
            } else {
                // 历史明文：兼容比对，成功后升级为哈希
                ok = stored != null && stored.equals(password);
                legacyUpgrade = ok;
            }
            if (!ok) {
                return Result.fail(ResultCode.LOGIN_ERROR);
            }
            if (legacyUpgrade) {
                userDAO.resetPassword(user.getId(), PasswordUtil.hash(password), user.getId());
            }
            user.setPassword(null);// 不把密码带出到 token/前端
            return Result.success(user);
        });
    }

    @Override
    public Result<Integer> addUser(User user) {
        return TxManager.tx(conn -> {
            UserDAOImpl userDAO = new UserDAOImpl();
            userDAO.setConnection(conn);
            if (userDAO.findByUsername(user.getUsername()) != null) {
                return Result.fail(ResultCode.USERNAME_EXISTS);
            }
            user.setPassword(PasswordUtil.hash(user.getPassword()));
            int id = userDAO.insert(user);
            return Result.success(id);
        });
    }

    @Override
    public List<User> listAll() {
        return TxManager.query(conn -> {
            UserDAOImpl userDAO = new UserDAOImpl();
            userDAO.setConnection(conn);
            return userDAO.listAll();
        });
    }
}
