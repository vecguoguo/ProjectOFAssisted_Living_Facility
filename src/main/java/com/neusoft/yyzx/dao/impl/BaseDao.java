package com.neusoft.yyzx.dao.impl;

import com.neusoft.yyzx.db.JdbcTemplate;
import com.neusoft.yyzx.mapping.MappingFactory;

import java.sql.Connection;

public class BaseDao {
    public JdbcTemplate jt = new JdbcTemplate();
    public MappingFactory mappingFactory = MappingFactory.getInstance();

    /**
     * 注入数据库连接
     * @param conn
     */
    public void setConnection(Connection conn) {
        jt.setConnection(conn);
    }

}
