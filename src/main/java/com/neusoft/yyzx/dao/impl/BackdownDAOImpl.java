package com.neusoft.yyzx.dao.impl;

import com.neusoft.yyzx.dao.BackdownDAO;
import com.neusoft.yyzx.entity.Backdown;
import com.neusoft.yyzx.mapping.EntityMapping;
import com.neusoft.yyzx.mapping.MappingFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * backdown 表数据访问实现
 */
public class BackdownDAOImpl extends BaseDao implements BackdownDAO {

    /** 关联客户查询（用于展示客户姓名），统一使用 left join */
    private static final String SELECT_JOIN =
            "select b.*, c.customer_name as customer_name"
                    + " from backdown b left join customer c on b.customer_id = c.id";

    @Override
    public int insert(Backdown b) {
        String SQL = "insert into backdown(customer_id, retreattime, retreattype, retreatreason,"
                + " auditstatus, auditperson, audittime, remarks, is_deleted)"
                + " values(?,?,?,?,?,?,?,?,0)";
        return jt.insert(SQL,
                b.getCustomerId(),
                toSqlDate(b.getRetreattime()),
                b.getRetreattype(),
                b.getRetreatreason() == null ? "" : b.getRetreatreason(),
                b.getAuditstatus(),
                b.getAuditperson() == null ? "" : b.getAuditperson(),
                toSqlDateOrMax(b.getAudittime()),
                b.getRemarks() == null ? "" : b.getRemarks());
    }

    @Override
    public Backdown findById(int id) {
        String SQL = SELECT_JOIN + " where b.id = ? and b.is_deleted = 0";
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.BACKDOWN_MAPPING);
        return (Backdown) jt.queryOne(SQL, mapping, id);
    }

    @Override
    public List<Backdown> listByCustomerName(String name) {
        String SQL = SELECT_JOIN + " where b.is_deleted = 0 and c.customer_name like ? order by b.id";
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.BACKDOWN_MAPPING);
        return toBackdownList(jt.query(SQL, mapping, like(name)));
    }

    @Override
    public List<Backdown> listByCustomerNameAndUserId(String name, int userId) {
        String SQL = SELECT_JOIN + " where b.is_deleted = 0 and c.user_id = ? and c.customer_name like ? order by b.id";
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.BACKDOWN_MAPPING);
        return toBackdownList(jt.query(SQL, mapping, userId, like(name)));
    }

    @Override
    public int audit(int id, int auditstatus, String auditperson, LocalDate audittime) {
        String SQL = "update backdown set auditstatus = ?, auditperson = ?, audittime = ? where id = ?";
        return jt.update(SQL, auditstatus, auditperson, toSqlDate(audittime), id);
    }

    private List<Backdown> toBackdownList(List<Object> list) {
        List<Backdown> result = new ArrayList<>();
        for (Object o : list) {
            result.add((Backdown) o);
        }
        return result;
    }

    /** 空名字转 % 表示查询全部 */
    private String like(String name) {
        return "%" + (name == null ? "" : name) + "%";
    }

    private java.sql.Date toSqlDate(LocalDate date) {
        return date == null ? null : java.sql.Date.valueOf(date);
    }

    /** 未发生的时间字段（未审批）按表结构 NOT NULL 约束存 9999-12-31，与 beddetails 约定一致 */
    private java.sql.Date toSqlDateOrMax(LocalDate date) {
        return date == null ? java.sql.Date.valueOf(LocalDate.of(9999, 12, 31)) : java.sql.Date.valueOf(date);
    }
}
