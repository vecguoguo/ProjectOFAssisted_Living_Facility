package com.neusoft.yyzx.mapping;

import com.neusoft.yyzx.entity.CustomerNurseItem;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * customernurseitem 行映射。
 * 注意：读取的 SQL 一律联表 nursecontent 并别名出 item_name / service_price，
 * 因此本映射固定读取这两个别名列。
 */
public class CustomerNurseItemMapping implements EntityMapping {
    @Override
    public Object mapping(ResultSet rs) throws SQLException {
        CustomerNurseItem i = new CustomerNurseItem();
        i.setId(rs.getInt("id"));
        i.setItemId(rs.getInt("item_id"));
        i.setCustormerId(rs.getInt("custormer_id"));
        i.setLevelId(rs.getInt("level_id"));
        i.setNurseNumber(rs.getInt("nurse_number"));
        i.setBuyTime(toLocalDate(rs.getDate("buy_time")));
        i.setMaturityTime(toLocalDate(rs.getDate("maturity_time")));
        i.setIsDeleted(rs.getInt("is_deleted"));
        i.setItemName(rs.getString("item_name"));
        i.setServicePrice(rs.getString("service_price"));
        return i;
    }

    private static LocalDate toLocalDate(Date date) {
        return date == null ? null : date.toLocalDate();
    }
}
