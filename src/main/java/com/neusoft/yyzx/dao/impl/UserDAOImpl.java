package com.neusoft.yyzx.dao.impl;

import com.neusoft.yyzx.dao.UserDAO;
import com.neusoft.yyzx.entity.User;
import com.neusoft.yyzx.mapping.EntityMapping;
import com.neusoft.yyzx.mapping.MappingFactory;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl extends BaseDao implements UserDAO {

    @Override
    public User findByUsername(String username) {
        String SQL = "select * from `user` where username = ? and is_deleted = 0";
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.USER_MAPPING);
        return (User) jt.queryOne(SQL, mapping, username);
    }

    @Override
    public List<User> listAll() {
        String SQL = "select * from `user` where is_deleted = 0 order by id";
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.USER_MAPPING);
        List<User> users = new ArrayList<>();
        for (Object o : jt.query(SQL, mapping)) {
            users.add((User) o);
        }
        return users;
    }

    @Override
    public int insert(User u) {
        String SQL = "insert into `user`(nickname, username, password, sex, email, phone_number, role_id,"
                + " create_time, create_by, update_time, update_by, is_deleted)"
                + " values(?,?,?,?,?,?,?,?,?,?,?,0)";
        LocalDateTime now = LocalDateTime.now();
        return jt.insert(SQL,
                u.getNickname(),
                u.getUsername(),
                u.getPassword(),
                u.getSex(),
                u.getEmail(),
                u.getPhoneNumber(),
                u.getRoleId(),
                Timestamp.valueOf(now),
                u.getCreateBy() == null ? -1 : u.getCreateBy(),
                Timestamp.valueOf(now),
                u.getUpdateBy() == null ? -1 : u.getUpdateBy());
    }

    @Override
    public int logicDelete(int id) {
        String SQL = "update `user` set is_deleted = 1 where id = ?";
        return jt.update(SQL, id);
    }

    @Override
    public User findById(int id) {
        String SQL = "select * from `user` where id = ? and is_deleted = 0";
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.USER_MAPPING);
        return (User) jt.queryOne(SQL, mapping, id);
    }

    @Override
    public List<User> listByCondition(String nickname, Integer roleId) {
        StringBuilder sql = new StringBuilder("select * from `user` where is_deleted = 0");
        List<Object> params = new ArrayList<>();
        if (nickname != null && !nickname.isBlank()) {
            sql.append(" and nickname like ?");
            params.add("%" + nickname + "%");
        }
        if (roleId != null) {
            sql.append(" and role_id = ?");
            params.add(roleId);
        }
        sql.append(" order by id");
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.USER_MAPPING);
        List<User> users = new ArrayList<>();
        for (Object o : jt.query(sql.toString(), mapping, params.toArray())) {
            users.add((User) o);
        }
        return users;
    }

    @Override
    public int updateUser(User u) {
        String SQL = "update `user` set nickname = ?, sex = ?, email = ?, phone_number = ?, role_id = ?,"
                + " update_time = ?, update_by = ? where id = ?";
        return jt.update(SQL,
                u.getNickname(),
                u.getSex(),
                u.getEmail(),
                u.getPhoneNumber(),
                u.getRoleId(),
                Timestamp.valueOf(LocalDateTime.now()),
                u.getUpdateBy() == null ? -1 : u.getUpdateBy(),
                u.getId());
    }

    @Override
    public int resetPassword(int id, String password, Integer updateBy) {
        String SQL = "update `user` set password = ?, update_time = ?, update_by = ? where id = ?";
        return jt.update(SQL,
                password,
                Timestamp.valueOf(LocalDateTime.now()),
                updateBy == null ? -1 : updateBy,
                id);
    }

    @Override
    public List<User> listByRoleIdAndNicknameLike(int roleId, String nameLike) {
        StringBuilder SQL = new StringBuilder("select * from `user` where is_deleted = 0 and role_id = ?");
        List<Object> args = new ArrayList<>();
        args.add(roleId);
        if (nameLike != null && !nameLike.isBlank()) {
            SQL.append(" and nickname like concat('%',?,'%')");
            args.add(nameLike);
        }
        SQL.append(" order by id");
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.USER_MAPPING);
        List<User> users = new ArrayList<>();
        for (Object o : jt.query(SQL.toString(), mapping, args.toArray())) {
            users.add((User) o);
        }
        return users;
    }
}