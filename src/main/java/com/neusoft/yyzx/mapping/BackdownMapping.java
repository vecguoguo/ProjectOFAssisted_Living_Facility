package com.neusoft.yyzx.mapping;

import com.neusoft.yyzx.entity.Backdown;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * backdown 表 → Backdown 实体映射
 * 列表查询使用 left join customer，附带映射 customer_name 用于展示
 */
public class BackdownMapping implements EntityMapping {
    @Override
    public Object mapping(ResultSet rs) throws SQLException {
        Backdown b = new Backdown();
        b.setId(rs.getInt("id"));
        b.setCustomerId(rs.getInt("customer_id"));
        b.setCustomerName(rs.getString("customer_name"));
        b.setRetreattime(toLocalDate(rs.getDate("retreattime")));
        b.setRetreattype(rs.getInt("retreattype"));
        b.setRetreatreason(rs.getString("retreatreason"));
        b.setAuditstatus(rs.getInt("auditstatus"));
        b.setAuditperson(rs.getString("auditperson"));
        b.setAudittime(toNullableLocalDate(rs.getDate("audittime")));
        b.setRemarks(rs.getString("remarks"));
        b.setIsDeleted(rs.getInt("is_deleted"));
        return b;
    }

    private static LocalDate toLocalDate(Date date) {
        return date == null ? null : date.toLocalDate();
    }

    /** 列 NOT NULL 约定：未发生的时间（未审批）存 9999-12-31，读取时归一化为 null，前端按“空”展示 */
    private static LocalDate toNullableLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        LocalDate value = date.toLocalDate();
        return LocalDate.of(9999, 12, 31).equals(value) ? null : value;
    }
}
