package com.neusoft.yyzx.mapping;

import com.neusoft.yyzx.entity.Outward;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * outward 表 → Outward 实体映射
 * 列表查询使用 left join customer，附带映射 customer_name 用于展示
 */
public class OutwardMapping implements EntityMapping {
    @Override
    public Object mapping(ResultSet rs) throws SQLException {
        Outward o = new Outward();
        o.setId(rs.getInt("id"));
        o.setCustomerId(rs.getInt("customer_id"));
        o.setCustomerName(rs.getString("customer_name"));
        o.setOutgoingreason(rs.getString("outgoingreason"));
        o.setOutgoingtime(toLocalDate(rs.getDate("outgoingtime")));
        o.setExpectedreturntime(toLocalDate(rs.getDate("expectedreturntime")));
        o.setActualreturntime(toNullableLocalDate(rs.getDate("actualreturntime")));
        o.setEscorted(rs.getString("escorted"));
        o.setRelation(rs.getString("relation"));
        o.setEscortedtel(rs.getString("escortedtel"));
        o.setAuditstatus(rs.getInt("auditstatus"));
        o.setAuditperson(rs.getString("auditperson"));
        o.setAudittime(toNullableLocalDate(rs.getDate("audittime")));
        o.setRemarks(rs.getString("remarks"));
        o.setIsDeleted(rs.getInt("is_deleted"));
        return o;
    }

    private static LocalDate toLocalDate(Date date) {
        return date == null ? null : date.toLocalDate();
    }

    /** 列 NOT NULL 约定：未发生的时间（未回院/未审批）存 9999-12-31，读取时归一化为 null，前端按“空”展示 */
    private static LocalDate toNullableLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        LocalDate value = date.toLocalDate();
        return LocalDate.of(9999, 12, 31).equals(value) ? null : value;
    }
}
