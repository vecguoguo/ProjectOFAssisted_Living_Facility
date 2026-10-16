package com.neusoft.yyzx.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;

/**
 * 极简事务模板（纯 JDK，无第三方依赖）。
 * <p>
 * 统一收口“取连接 → 执行 → 提交/回滚 → 归还连接”的全生命周期，从根本上杜绝连接泄漏：
 * 无论正常返回还是抛异常，{@code finally} 都会关闭连接。
 * <p>
 * 用法：
 * <ul>
 *   <li>写操作走 {@link #tx(Action)}：回调正常返回后提交事务；抛出任何异常则回滚。</li>
 *   <li>只读查询走 {@link #query(Action)}：不提交，结束只读事务后直接归还连接。</li>
 * </ul>
 * 回调内通过参数拿到 {@link Connection}，注入到各 DAO（{@code dao.setConnection(conn)}），
 * 多 DAO 共用同一连接即天然处于同一事务。业务校验失败直接 {@code return Result.fail(...)}，
 * 不会产生任何写操作，提交空事务无副作用。
 */
public final class TxManager {

    /** 事务回调：入参为当前事务连接，允许抛出任意异常，由模板统一处理。 */
    public interface Action<T> {
        T apply(Connection conn) throws Exception;
    }

    private static final Log LOGGER = LogFactory.getLog(TxManager.class);

    private TxManager() {
    }

    /**
     * 以事务方式执行：成功提交，异常回滚，最终必定归还连接。
     */
    public static <T> T tx(Action<T> action) {
        Connection conn = DBManager.getInstance().getConnection();
        try {
            T result = action.apply(conn);
            conn.commit();
            return result;
        } catch (Throwable ex) {
            rollbackQuietly(conn);
            throw propagate(ex);
        } finally {
            closeQuietly(conn);
        }
    }

    /**
     * 只读查询：结束事务快照后归还连接，异常同样保证释放。
     */
    public static <T> T query(Action<T> action) {
        Connection conn = DBManager.getInstance().getConnection();
        try {
            return action.apply(conn);
        } catch (Throwable ex) {
            throw propagate(ex);
        } finally {
            // 只读事务也必须显式结束：连接为手动提交模式，若只做 SELECT 就直接归还池中，
            // 连接仍持有 REPEATABLE READ 一致性快照，被后续请求复用时将持续读到旧数据
            // （表现为床位状态在“空闲/有人”之间漂移）。故归还前先回滚以释放快照。
            rollbackQuietly(conn);
            closeQuietly(conn);
        }
    }

    /**
     * 保持调用方原有异常语义：运行时异常/错误原样抛出（如 {@code BusinessException}），
     * 受检异常（如 {@code SQLException}）包装为运行时异常。
     */
    private static RuntimeException propagate(Throwable ex) {
        if (ex instanceof RuntimeException re) {
            throw re;
        }
        if (ex instanceof Error err) {
            throw err;
        }
        return new RuntimeException(ex);
    }

    private static void rollbackQuietly(Connection conn) {
        if (conn == null) {
            return;
        }
        try {
            conn.rollback();
        } catch (Exception e) {
            LOGGER.error("事务回滚失败", e);
        }
    }

    /** 归还连接。DBManager 返回的是连接池代理，close() 即归还池中复用（非物理断开）。 */
    private static void closeQuietly(Connection conn) {
        if (conn == null) {
            return;
        }
        try {
            conn.close();
        } catch (Exception e) {
            LOGGER.error("关闭数据库连接失败", e);
        }
    }
}
