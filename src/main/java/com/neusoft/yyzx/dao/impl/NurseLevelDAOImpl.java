package com.neusoft.yyzx.dao.impl;

import com.neusoft.yyzx.dao.NurseLevelDAO;
import com.neusoft.yyzx.entity.NurseLevel;
import com.neusoft.yyzx.mapping.EntityMapping;
import com.neusoft.yyzx.mapping.MappingFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * nurselevel 表数据访问实现
 */
public class NurseLevelDAOImpl extends BaseDao implements NurseLevelDAO {

    @Override
    public List<NurseLevel> listByStatus(Integer status) {
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.NURSE_LEVEL_MAPPING);
        StringBuilder SQL = new StringBuilder("select * from nurselevel where is_deleted = 0");
        List<Object> args = new ArrayList<>();
        if (status != null) {
            SQL.append(" and level_status = ?");
            args.add(status);
        }
        SQL.append(" order by id");
        return toLevelList(jt.query(SQL.toString(), mapping, args.toArray()));
    }

    @Override
    public NurseLevel findById(int id) {
        String SQL = "select * from nurselevel where id = ? and is_deleted = 0";
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.NURSE_LEVEL_MAPPING);
        return (NurseLevel) jt.queryOne(SQL, mapping, id);
    }

    @Override
    public int insert(NurseLevel level) {
        String SQL = "insert into nurselevel(level_name, level_status, is_deleted) values(?,?,0)";
        return jt.insert(SQL, level.getLevelName(),
                level.getLevelStatus() == null ? 1 : level.getLevelStatus());
    }

    @Override
    public int updateStatus(int id, int status) {
        String SQL = "update nurselevel set level_status = ? where id = ?";
        return jt.update(SQL, status, id);
    }

    private List<NurseLevel> toLevelList(List<Object> list) {
        List<NurseLevel> levels = new ArrayList<>();
        for (Object o : list) {
            levels.add((NurseLevel) o);
        }
        return levels;
    }
}
