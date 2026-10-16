package com.neusoft.yyzx.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * JDBC 工具类：读取 db.properties，获取/释放连接
 */
public final class DBUtil {

    private static String url;
    private static String username;
    private static String password;

    static {
        try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties props = new Properties();
            props.load(in);
            String driver = props.getProperty("jdbc.driver");
            url = props.getProperty("jdbc.url");
            username = props.getProperty("jdbc.username");
            password = props.getProperty("jdbc.password");
            Class.forName(driver);
        } catch (IOException | ClassNotFoundException e) {
            throw new ExceptionInInitializerError("初始化数据库配置失败: " + e.getMessage());
        }
    }

    private DBUtil() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public static void close(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    // 忽略关闭异常
                }
            }
        }
    }
}
