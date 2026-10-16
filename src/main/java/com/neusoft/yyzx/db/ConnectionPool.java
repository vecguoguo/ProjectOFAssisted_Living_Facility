package com.neusoft.yyzx.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;

/**
 * 极简数据库连接池（纯 JDK，无第三方依赖）。
 * <p>
 * 复用物理连接，避免每次请求都做一次 TCP/认证握手。发放出的 {@link Connection} 是一个动态代理，
 * 其 {@code close()} 被拦截为“归还池中”而非真正断开，故上层（{@link TxManager}）照常调用
 * {@code conn.close()} 即可无缝复用，无需感知池的存在。
 * <p>
 * 借出前做有效性校验（{@code isValid}），可自动丢弃因 MySQL {@code wait_timeout} 空闲过期而失效的连接；
 * 池打满时按超时等待，绝不无限制建连。
 */
public final class ConnectionPool {

    private static final Log LOGGER = LogFactory.getLog(ConnectionPool.class);

    private final Supplier<Connection> rawFactory;
    private final int maxSize;
    private final long borrowTimeoutMs;

    /** 空闲连接队列；同时作为锁对象与 wait/notify 监视器。 */
    private final Deque<Connection> idle = new ArrayDeque<>();
    /** 当前已创建（空闲 + 借出）的连接总数。 */
    private int total = 0;

    public ConnectionPool(Supplier<Connection> rawFactory, int maxSize, long borrowTimeoutMs) {
        this.rawFactory = rawFactory;
        this.maxSize = maxSize;
        this.borrowTimeoutMs = borrowTimeoutMs;
    }

    /** 借出一个可用连接（包装为代理，close 即归还）。 */
    public Connection borrow() {
        final long deadline = System.currentTimeMillis() + borrowTimeoutMs;
        synchronized (idle) {
            while (true) {
                Connection raw = idle.pollFirst();
                if (raw != null) {
                    if (isUsable(raw)) {
                        resetForUse(raw);
                        return wrap(raw);
                    }
                    closeQuietly(raw);
                    total--;
                    continue;
                }
                if (total < maxSize) {
                    total++;
                    try {
                        Connection created = rawFactory.get();
                        resetForUse(created);
                        return wrap(created);
                    } catch (RuntimeException e) {
                        total--;
                        idle.notifyAll();
                        throw e;
                    }
                }
                long wait = deadline - System.currentTimeMillis();
                if (wait <= 0) {
                    throw new RuntimeException("获取数据库连接超时（连接池已满，max=" + maxSize + "）");
                }
                try {
                    idle.wait(wait);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("等待数据库连接被中断", ie);
                }
            }
        }
    }

    /** 归还一个连接：有效则入队复用，失效则物理关闭并腾出配额。 */
    private void giveBack(Connection raw) {
        synchronized (idle) {
            if (isUsable(raw)) {
                idle.offerLast(raw);
            } else {
                closeQuietly(raw);
                total--;
            }
            idle.notify();
        }
    }

    /** 借出前统一置为事务模式（关闭自动提交），与 DBManager 约定一致。 */
    private void resetForUse(Connection raw) {
        try {
            if (!raw.isClosed() && raw.getAutoCommit()) {
                raw.setAutoCommit(false);
            }
        } catch (Exception e) {
            LOGGER.warn("重置连接 autoCommit 失败", e);
        }
    }

    private boolean isUsable(Connection raw) {
        try {
            return raw != null && !raw.isClosed() && raw.isValid(2);
        } catch (Exception e) {
            return false;
        }
    }

    private void closeQuietly(Connection raw) {
        try {
            if (raw != null) {
                raw.close();
            }
        } catch (Exception e) {
            LOGGER.warn("物理关闭连接失败", e);
        }
    }

    /** 将真实连接包装为代理：拦截 close 转为归还，其余方法透传。 */
    private Connection wrap(Connection raw) {
        final boolean[] returned = {false};
        InvocationHandler handler = (proxy, method, args) -> {
            String name = method.getName();
            if ("close".equals(name)) {
                if (!returned[0]) {
                    returned[0] = true;
                    giveBack(raw);
                }
                return null;
            }
            if ("isClosed".equals(name)) {
                return returned[0];
            }
            try {
                return method.invoke(raw, args);
            } catch (InvocationTargetException ite) {
                throw ite.getCause();
            }
        };
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(), new Class<?>[]{Connection.class}, handler);
    }

    /** 当前已创建的连接总数（监控/测试用）。 */
    public int getTotal() {
        synchronized (idle) {
            return total;
        }
    }
}
