package com.neusoft.yyzx.mapping;

import com.neusoft.yyzx.entity.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class UserMapping implements EntityMapping {
    @Override
    public Object mapping(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setNickname(rs.getString("nickname"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setSex(rs.getInt("sex"));
        u.setEmail(rs.getString("email"));
        u.setPhoneNumber(rs.getString("phone_number"));
        u.setRoleId(rs.getInt("role_id"));
        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            u.setCreateTime(createTime.toLocalDateTime());
        }
        u.setCreateBy(rs.getInt("create_by"));
        Timestamp updateTime = rs.getTimestamp("update_time");
        if (updateTime != null) {
            u.setUpdateTime(updateTime.toLocalDateTime());
        }
        u.setUpdateBy(rs.getInt("update_by"));
        u.setIsDeleted(rs.getInt("is_deleted"));
        return u;
    }
}
