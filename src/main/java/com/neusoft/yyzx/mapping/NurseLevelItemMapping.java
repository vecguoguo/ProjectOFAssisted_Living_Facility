package com.neusoft.yyzx.mapping;

import com.neusoft.yyzx.entity.NurseLevelItem;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NurseLevelItemMapping implements EntityMapping {
    @Override
    public Object mapping(ResultSet rs) throws SQLException {
        NurseLevelItem i = new NurseLevelItem();
        i.setId(rs.getInt("id"));
        i.setLevelId(rs.getInt("level_id"));
        i.setItemId(rs.getInt("item_id"));
        return i;
    }
}
