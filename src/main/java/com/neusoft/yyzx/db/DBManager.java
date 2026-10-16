package com.neusoft.yyzx.db;

import com.neusoft.yyzx.util.PropertyUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBManager {

	private Properties prop;
	private String path = "db.properties";
	private PropertyUtil propertyUtil;
	private static DBManager dbManager = null;
	private Log logger = LogFactory.getLog(DBManager.class);

	/** 连接池（懒加载）。 */
	private volatile ConnectionPool pool;

	private DBManager() {
	}

	public static DBManager getInstance() {
		if (dbManager == null) {
			dbManager = new DBManager();
		}
		return dbManager;
	}

	/**
	 * 从连接池借出一个连接。返回的是代理对象，调用其 {@code close()} 即归还池中（并非物理断开），
	 * 上层 {@link TxManager} 据此实现“用完必还”，既杜绝泄漏又复用连接。
	 *
	 * @throws RuntimeException 驱动缺失或连接失败时立即抛出（fail-fast）
	 */
	public Connection getConnection() {
		return getPool().borrow();
	}

	/** 懒加载初始化连接池，双重检查保证仅创建一次。 */
	private ConnectionPool getPool() {
		if (pool == null) {
			synchronized (this) {
				if (pool == null) {
					loadConfig();
					int maxSize = Integer.parseInt(prop.getProperty("pool.maxSize", "32"));
					long borrowTimeout = Long.parseLong(prop.getProperty("pool.borrowTimeoutMs", "10000"));
					pool = new ConnectionPool(this::createRawConnection, maxSize, borrowTimeout);
					if (logger.isInfoEnabled()) {
						logger.info("数据库连接池初始化完成，maxSize=" + maxSize);
					}
				}
			}
		}
		return pool;
	}

	/** 读取数据库配置（仅首次）。 */
	private void loadConfig() {
		if (propertyUtil == null) {
			propertyUtil = PropertyUtil.getInstance();
			if (prop == null) {
				prop = propertyUtil.getProperty(path);
				if (logger.isInfoEnabled()) {
					logger.info("读取数据库配置文件：	" + path);
				}
			}
		}
	}

	/** 创建一条全新的物理连接（供连接池按需调用），关闭自动提交，事务由 TxManager 统一管理。 */
	private Connection createRawConnection() {
		try {
			Class.forName(prop.getProperty("jdbc.driver"));
			Connection conn = DriverManager.getConnection(prop.getProperty("jdbc.url"),
					prop.getProperty("jdbc.username"), prop.getProperty("jdbc.password"));
			conn.setAutoCommit(false);
			return conn;
		} catch (ClassNotFoundException e) {
			logger.error("找不到数据库驱动！", e);
			throw new RuntimeException("找不到数据库驱动！", e);
		} catch (SQLException e) {
			logger.error("数据库连接失败！", e);
			throw new RuntimeException("数据库连接失败！", e);
		}
	}

}
