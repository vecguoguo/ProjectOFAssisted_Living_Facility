package com.neusoft.yyzx.mapping;

import com.neusoft.yyzx.entity.NurseLevel;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NurseLevelMapping implements EntityMapping {
    @Override
    public Object mapping(ResultSet rs) throws SQLException {
        NurseLevel l = new NurseLevel();
        l.setId(rs.getInt("id"));
        l.setLevelName(rs.getString("level_name"));
        l.setLevelStatus(rs.getInt("level_status"));
        l.setIsDeleted(rs.getInt("is_deleted"));
        return l;
    }
}
