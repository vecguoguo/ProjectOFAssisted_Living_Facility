package com.neusoft.yyzx.mapping;

import com.neusoft.yyzx.entity.NurseRecord;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * nurserecord 行映射。
 * 读取的 SQL 一律联表 nursecontent 并别名出 item_name，故固定读取该别名列。
 */
public class NurseRecordMapping implements EntityMapping {
    @Override
    public Object mapping(ResultSet rs) throws SQLException {
        NurseRecord r = new NurseRecord();
        r.setId(rs.getInt("id"));
        r.setCustomerId(rs.getInt("customer_id"));
        r.setItemId(rs.getInt("item_id"));
        r.setUserId(rs.getInt("user_id"));
        r.setNursingTime(toLocalDateTime(rs.getTimestamp("nursing_time")));
        r.setNursingContent(rs.getString("nursing_content"));
        r.setNursingCount(rs.getInt("nursing_count"));
        r.setIsDeleted(rs.getInt("is_deleted"));
        r.setItemName(rs.getString("item_name"));
        return r;
    }

    private static LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts == null ? null : ts.toLocalDateTime();
    }
}
