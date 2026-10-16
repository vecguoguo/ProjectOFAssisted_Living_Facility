package com.neusoft.yyzx.mapping;

import com.neusoft.yyzx.entity.Bed;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BedMapping implements EntityMapping {
    @Override
    public Object mapping(ResultSet rs) throws SQLException {
        Bed b = new Bed();
        b.setId(rs.getInt("id"));
        b.setRoomNo(rs.getInt("room_no"));
        b.setBedNo(rs.getString("bed_no"));
        b.setBedStatus(rs.getInt("bed_status"));
        b.setRemarks(rs.getString("remarks"));
        return b;
    }
}
