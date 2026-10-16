package com.neusoft.yyzx.mapping;

import com.neusoft.yyzx.entity.BedDetails;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BedDetailsMapping implements EntityMapping {
    @Override
    public Object mapping(ResultSet rs) throws SQLException {
        BedDetails d = new BedDetails();
        d.setId(rs.getInt("id"));
        d.setStartDate(rs.getDate("start_date") == null ? null : rs.getDate("start_date").toLocalDate());
        d.setEndDate(rs.getDate("end_date") == null ? null : rs.getDate("end_date").toLocalDate());
        d.setBedDetails(rs.getString("bed_details"));
        d.setCustomerId(rs.getInt("customer_id"));
        d.setBedId(rs.getInt("bed_id"));
        d.setIsDeleted(rs.getInt("is_deleted"));
        return d;
    }
}
