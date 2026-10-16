package com.neusoft.yyzx.mapping;

import com.neusoft.yyzx.entity.Customer;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class CustomerMapping implements EntityMapping {
    @Override
    public Object mapping(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setId(rs.getInt("id"));
        c.setCustomerName(rs.getString("customer_name"));
        c.setCustomerAge(rs.getInt("customer_age"));
        c.setCustomerSex(rs.getInt("customer_sex"));
        c.setIdcard(rs.getString("idcard"));
        c.setRoomNo(rs.getString("room_no"));
        c.setBuildingNo(rs.getString("building_no"));
        c.setBedId(rs.getInt("bed_id"));
        // level_id 允许为空（移除护理级别后）：getInt 后用 wasNull 判定，避免把 NULL 读成 0
        int levelId = rs.getInt("level_id");
        c.setLevelId(rs.wasNull() ? null : levelId);
        c.setUserId(rs.getInt("user_id"));
        c.setCheckinDate(toLocalDate(rs.getDate("checkin_date")));
        c.setExpirationDate(toLocalDate(rs.getDate("expiration_date")));
        c.setBirthday(toLocalDate(rs.getDate("birthday")));
        c.setContactTel(rs.getString("contact_tel"));
        c.setFamilyMember(rs.getString("family_member"));
        c.setHeight(rs.getString("height"));
        c.setWeight(rs.getString("weight"));
        c.setBloodType(rs.getString("blood_type"));
        c.setPsychosomaticState(rs.getString("psychosomatic_state"));
        c.setAttention(rs.getString("attention"));
        c.setFilepath(rs.getString("filepath"));
        c.setIsDeleted(rs.getInt("is_deleted"));
        return c;
    }

    private static LocalDate toLocalDate(Date date) {
        return date == null ? null : date.toLocalDate();
    }
}
