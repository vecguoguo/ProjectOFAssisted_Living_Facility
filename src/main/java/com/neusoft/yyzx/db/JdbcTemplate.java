package com.neusoft.yyzx.db;

import com.neusoft.yyzx.mapping.EntityMapping;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JdbcTemplate {

    private Connection con;
    private PreparedStatement psta = null;
    private ResultSet rs = null;
    private Log logger = LogFactory.getLog(JdbcTemplate.class);

    /**
     * 每个 DAO 实例持有自己的 JdbcTemplate（非单例）。
     * 因为 con/psta/rs 是每次操作的可变状态，若全局共享单例，Web 层并发请求会互相
     * 覆盖 rs/psta，导致一个线程读到另一个线程（不同表）的 ResultSet，抛出
     * "Column 'xxx' not found" / "columnDefinition is null"。故此处禁止单例共享。
     */
    public JdbcTemplate() {
    }

    /**
     * 注入数据库连接
     * @param connection
     */
    public void setConnection(Connection connection) {
        this.con = connection;
    }

    /**
     * 用于执行insert，update，delete语句，可带参数
     * @param sql
     * @param values
     * @return 返回影响行数
     */
    public int update(String sql, Object... values) {
        if (logger.isDebugEnabled()) {
            logger.debug("发送sql语句：" + sql);
        }
        int row = 0;
        try {
            psta = con.prepareStatement(sql);
            for (int i = 0; i < values.length; i++) {
                psta.setObject(i + 1, values[i]);
            }
            row = psta.executeUpdate();
        } catch (SQLException e) {
            String errorMsg = "sql语句发生错误！sql语句为：" + sql;
            if (logger.isErrorEnabled()) {
                logger.error(errorMsg, e);
            }
            throw new RuntimeException(errorMsg);
        } finally {
            close();
        }
        return row;
    }

    /**
     * 用于执行insert语句并返回自增主键
     * @param sql
     * @param values
     * @return 返回自增主键，失败返回-1
     */
    public int insert(String sql, Object... values) {
        if (logger.isDebugEnabled()) {
            logger.debug("发送sql语句：" + sql);
        }
        int key = -1;
        try {
            psta = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < values.length; i++) {
                psta.setObject(i + 1, values[i]);
            }
            psta.executeUpdate();
            rs = psta.getGeneratedKeys();
            if (rs.next()) {
                key = rs.getInt(1);
            }
        } catch (SQLException e) {
            String errorMsg = "sql语句发生错误！sql语句为：" + sql;
            if (logger.isErrorEnabled()) {
                logger.error(errorMsg, e);
            }
            throw new RuntimeException(errorMsg);
        } finally {
            close();
        }
        return key;
    }

    /**
     * 查询一个double值
     * @param sql
     * @return
     */
    public double queryDouble(String sql, Object... values) {
        if (logger.isDebugEnabled()) {
            logger.debug("发送sql语句：" + sql);
        }
        double result = 0;
        try {
            psta = con.prepareStatement(sql);
            for (int i = 0; i < values.length; i++) {
                psta.setObject(i + 1, values[i]);
            }
            rs = psta.executeQuery();
            if (rs.next()) {
                result = rs.getDouble(1);
            }
        } catch (SQLException e) {
            String errorMsg = "sql语句发生错误！sql语句为：" + sql;
            if (logger.isErrorEnabled()) {
                logger.error(errorMsg, e);
            }
            throw new RuntimeException(errorMsg);
        } finally {
            close();
        }
        return result;
    }

    /**
     * 用于执行select count（*）语句,或者获取seq序列的语句
     * @param sql
     * @return 返回第一个值
     */
    public int query(String sql) {
        if (logger.isDebugEnabled()) {
            logger.debug("发送sql语句：" + sql);
        }
        int result = 0;
        try {
            psta = con.prepareStatement(sql);
            rs = psta.executeQuery();
            if (rs.next()) {
                result = rs.getInt(1);
            }
        } catch (SQLException e) {
            String errorMsg = "sql语句发生错误！sql语句为：" + sql;
            if (logger.isErrorEnabled()) {
                logger.error(errorMsg, e);
            }
            throw new RuntimeException(errorMsg);
        } finally {
            close();
        }
        return result;
    }

    /**
     * 用于执行查询返回多个对象的sql语句
     * @param sql
     * @param mapping
     * @param values
     * @return 返回对象链表
     */
    public List<Object> query(String sql, EntityMapping mapping, Object... values) {
        if (logger.isDebugEnabled()) {
            logger.debug("发送sql语句：" + sql);
        }
        List<Object> list = new ArrayList<Object>();
        try {
            psta = con.prepareStatement(sql);
            for (int i = 0; i < values.length; i++) {
                psta.setObject(i + 1, values[i]);
            }
            rs = psta.executeQuery();
            while (rs.next()) {
                list.add(mapping.mapping(rs));
            }
        } catch (SQLException e) {
            String errorMsg = "sql语句发生错误！sql语句为：" + sql;
            if (logger.isErrorEnabled()) {
                logger.error(errorMsg, e);
            }
            throw new RuntimeException(errorMsg);
        } finally {
            close();
        }
        return list;
    }

    /**
     * 用于执行查询返回单个对象的sql语句，无数据时返回null
     * @param sql
     * @param mapping
     * @param values
     * @return 返回对象，无数据返回null
     */
    public Object queryOne(String sql, EntityMapping mapping, Object... values) {
        if (logger.isDebugEnabled()) {
            logger.debug("发送sql语句：" + sql);
        }
        Object result = null;
        try {
            psta = con.prepareStatement(sql);
            for (int i = 0; i < values.length; i++) {
                psta.setObject(i + 1, values[i]);
            }
            rs = psta.executeQuery();
            if (rs.next()) {
                result = mapping.mapping(rs);
            }
        } catch (SQLException e) {
            String errorMsg = "sql语句发生错误！sql语句为：" + sql;
            if (logger.isErrorEnabled()) {
                logger.error(errorMsg, e);
            }
            throw new RuntimeException(errorMsg);
        } finally {
            close();
        }
        return result;
    }

    /**
     * 关闭数据库连接资源
     */
    public void close() {
        try {
            if (psta != null) {
                psta.close();
                psta = null;
            }
            if (rs != null) {
                rs.close();
                rs = null;
            }
        } catch (SQLException e) {
            String errorMsg = "关闭preparedstatment 和resultset发生错误！";
            if (logger.isErrorEnabled()) {
                logger.error(errorMsg, e);
            }
            throw new RuntimeException(errorMsg);
        }
    }
}
