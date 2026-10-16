package com.neusoft.yyzx.dao.impl;

import com.neusoft.yyzx.dao.OutwardDAO;
import com.neusoft.yyzx.entity.Outward;
import com.neusoft.yyzx.mapping.EntityMapping;
import com.neusoft.yyzx.mapping.MappingFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * outward 表数据访问实现
 */
public class OutwardDAOImpl extends BaseDao implements OutwardDAO {

    /** 关联客户查询（用于展示客户姓名），统一使用 left join */
    private static final String SELECT_JOIN =
            "select o.*, c.customer_name as customer_name"
                    + " from outward o left join customer c on o.customer_id = c.id";

    @Override
    public int insert(Outward o) {
        String SQL = "insert into outward(customer_id, outgoingreason, outgoingtime, expectedreturntime,"
                + " actualreturntime, escorted, relation, escortedtel, auditstatus, auditperson,"
                + " audittime, remarks, is_deleted)"
                + " values(?,?,?,?,?,?,?,?,?,?,?,?,0)";
        return jt.insert(SQL,
                o.getCustomerId(),
                o.getOutgoingreason(),
                toSqlDate(o.getOutgoingtime()),
                toSqlDate(o.getExpectedreturntime()),
                toSqlDateOrMax(o.getActualreturntime()),
                o.getEscorted() == null ? "" : o.getEscorted(),
                o.getRelation() == null ? "" : o.getRelation(),
                o.getEscortedtel() == null ? "" : o.getEscortedtel(),
                o.getAuditstatus(),
                o.getAuditperson() == null ? "" : o.getAuditperson(),
                toSqlDateOrMax(o.getAudittime()),
                o.getRemarks() == null ? "" : o.getRemarks());
    }

    @Override
    public Outward findById(int id) {
        String SQL = SELECT_JOIN + " where o.id = ? and o.is_deleted = 0";
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.OUTWARD_MAPPING);
        return (Outward) jt.queryOne(SQL, mapping, id);
    }

    @Override
    public List<Outward> listByCustomerName(String name) {
        String SQL = SELECT_JOIN + " where o.is_deleted = 0 and c.customer_name like ? order by o.id";
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.OUTWARD_MAPPING);
        return toOutwardList(jt.query(SQL, mapping, like(name)));
    }

    @Override
    public List<Outward> listByCustomerNameAndUserId(String name, int userId) {
        String SQL = SELECT_JOIN + " where o.is_deleted = 0 and c.user_id = ? and c.customer_name like ? order by o.id";
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.OUTWARD_MAPPING);
        return toOutwardList(jt.query(SQL, mapping, userId, like(name)));
    }

    @Override
    public int audit(int id, int auditstatus, String auditperson, LocalDate audittime) {
        String SQL = "update outward set auditstatus = ?, auditperson = ?, audittime = ? where id = ?";
        return jt.update(SQL, auditstatus, auditperson, toSqlDate(audittime), id);
    }

    @Override
    public int updateActualReturnTime(int id, LocalDate actualReturnTime) {
        String SQL = "update outward set actualreturntime = ? where id = ?";
        return jt.update(SQL, toSqlDate(actualReturnTime), id);
    }

    private List<Outward> toOutwardList(List<Object> list) {
        List<Outward> result = new ArrayList<>();
        for (Object o : list) {
            result.add((Outward) o);
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

    /** 未发生的时间字段（未回院/未审批）按表结构 NOT NULL 约束存 9999-12-31，与 beddetails 约定一致 */
    private java.sql.Date toSqlDateOrMax(LocalDate date) {
        return date == null ? java.sql.Date.valueOf(LocalDate.of(9999, 12, 31)) : java.sql.Date.valueOf(date);
    }
}
