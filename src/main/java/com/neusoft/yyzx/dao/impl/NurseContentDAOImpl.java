package com.neusoft.yyzx.dao.impl;

import com.neusoft.yyzx.dao.NurseContentDAO;
import com.neusoft.yyzx.entity.NurseContent;
import com.neusoft.yyzx.mapping.EntityMapping;
import com.neusoft.yyzx.mapping.MappingFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * nursecontent 表数据访问实现
 */
public class NurseContentDAOImpl extends BaseDao implements NurseContentDAO {

    @Override
    public List<NurseContent> listByStatusAndName(Integer status, String nameLike) {
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.NURSE_CONTENT_MAPPING);
        StringBuilder SQL = new StringBuilder("select * from nursecontent where is_deleted = 0");
        List<Object> args = new ArrayList<>();
        if (status != null) {
            SQL.append(" and status = ?");
            args.add(status);
        }
        if (nameLike != null && !nameLike.isBlank()) {
            SQL.append(" and nursing_name like concat('%',?,'%')");
            args.add(nameLike);
        }
        SQL.append(" order by id");
        return toContentList(jt.query(SQL.toString(), mapping, args.toArray()));
    }

    @Override
    public NurseContent findById(int id) {
        String SQL = "select * from nursecontent where id = ? and is_deleted = 0";
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.NURSE_CONTENT_MAPPING);
        return (NurseContent) jt.queryOne(SQL, mapping, id);
    }

    @Override
    public int insert(NurseContent c) {
        String SQL = "insert into nursecontent(serial_number, nursing_name, service_price, message,"
                + " status, execution_cycle, execution_times, is_deleted) values(?,?,?,?,?,?,?,0)";
        return jt.insert(SQL, c.getSerialNumber(), c.getNursingName(), c.getServicePrice(),
                nullToEmpty(c.getMessage()), c.getStatus() == null ? 1 : c.getStatus(),
                nullToEmpty(c.getExecutionCycle()), nullToEmpty(c.getExecutionTimes()));
    }

    @Override
    public int update(NurseContent c) {
        String SQL = "update nursecontent set serial_number = ?, nursing_name = ?, service_price = ?,"
                + " message = ?, status = ?, execution_cycle = ?, execution_times = ? where id = ?";
        return jt.update(SQL, c.getSerialNumber(), c.getNursingName(), c.getServicePrice(),
                nullToEmpty(c.getMessage()), c.getStatus(), nullToEmpty(c.getExecutionCycle()),
                nullToEmpty(c.getExecutionTimes()), c.getId());
    }

    @Override
    public int logicDelete(int id) {
        String SQL = "update nursecontent set is_deleted = 1 where id = ?";
        return jt.update(SQL, id);
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private List<NurseContent> toContentList(List<Object> list) {
        List<NurseContent> contents = new ArrayList<>();
        for (Object o : list) {
            contents.add((NurseContent) o);
        }
        return contents;
    }
}
