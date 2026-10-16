package com.neusoft.yyzx.mapping;

import com.neusoft.yyzx.entity.NurseContent;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NurseContentMapping implements EntityMapping {
    @Override
    public Object mapping(ResultSet rs) throws SQLException {
        NurseContent n = new NurseContent();
        n.setId(rs.getInt("id"));
        n.setSerialNumber(rs.getString("serial_number"));
        n.setNursingName(rs.getString("nursing_name"));
        n.setServicePrice(rs.getString("service_price"));
        n.setMessage(rs.getString("message"));
        n.setStatus(rs.getInt("status"));
        n.setExecutionCycle(rs.getString("execution_cycle"));
        n.setExecutionTimes(rs.getString("execution_times"));
        n.setIsDeleted(rs.getInt("is_deleted"));
        return n;
    }
}
