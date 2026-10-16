package com.neusoft.yyzx.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface EntityMapping {
    /**
     * 将结果集中的一行数据映射为一个Object对象
     * @param rs 当前行数据
     * @return Object
     */
    public Object mapping(ResultSet rs) throws SQLException;

}
