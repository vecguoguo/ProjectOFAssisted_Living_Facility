package com.neusoft.yyzx.dao.impl;

import com.neusoft.yyzx.dao.BedDAO;
import com.neusoft.yyzx.entity.Bed;
import com.neusoft.yyzx.mapping.EntityMapping;
import com.neusoft.yyzx.mapping.MappingFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * bed 表数据访问实现
 */
public class BedDAOImpl extends BaseDao implements BedDAO {

    @Override
    public Bed findById(int id) {
        String SQL = "select * from bed where id = ?";
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.BED_MAPPING);
        return (Bed) jt.queryOne(SQL, mapping, id);
    }

    @Override
    public Bed findByIdForUpdate(int id) {
        String SQL = "select * from bed where id = ? for update";
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.BED_MAPPING);
        return (Bed) jt.queryOne(SQL, mapping, id);
    }

    @Override
    public List<Bed> listByRoomNo(int roomNo) {
        String SQL = "select * from bed where room_no = ? order by bed_no";
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.BED_MAPPING);
        return toBedList(jt.query(SQL, mapping, roomNo));
    }

    @Override
    public List<Bed> listFreeByRoomNo(int roomNo, int freeStatus) {
        String SQL = "select * from bed where room_no = ? and bed_status = ? order by bed_no";
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.BED_MAPPING);
        return toBedList(jt.query(SQL, mapping, roomNo, freeStatus));
    }

    @Override
    public List<Bed> listAll() {
        String SQL = "select * from bed order by room_no, bed_no";
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.BED_MAPPING);
        return toBedList(jt.query(SQL, mapping));
    }

    @Override
    public int updateStatus(int id, int status) {
        String SQL = "update bed set bed_status = ? where id = ?";
        return jt.update(SQL, status, id);
    }

    @Override
    public List<Bed> listByCondition(Integer roomNo, String bedNo, Integer status) {
        StringBuilder sql = new StringBuilder("select * from bed where 1 = 1");
        List<Object> params = new ArrayList<>();
        if (roomNo != null) {
            sql.append(" and room_no = ?");
            params.add(roomNo);
        }
        if (bedNo != null && !bedNo.isBlank()) {
            sql.append(" and bed_no = ?");
            params.add(bedNo);
        }
        if (status != null) {
            sql.append(" and bed_status = ?");
            params.add(status);
        }
        sql.append(" order by room_no, bed_no");
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.BED_MAPPING);
        return toBedList(jt.query(sql.toString(), mapping, params.toArray()));
    }

    @Override
    public Bed findByRoomNoAndBedNo(int roomNo, String bedNo) {
        String SQL = "select * from bed where room_no = ? and bed_no = ?";
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.BED_MAPPING);
        return (Bed) jt.queryOne(SQL, mapping, roomNo, bedNo);
    }

    @Override
    public int insert(Bed bed) {
        String SQL = "insert into bed(room_no, bed_no, bed_status, remarks) values(?,?,?,?)";
        return jt.insert(SQL,
                bed.getRoomNo(),
                bed.getBedNo(),
                bed.getBedStatus(),
                bed.getRemarks() == null ? "" : bed.getRemarks());
    }

    @Override
    public int updateBed(Bed bed) {
        String SQL = "update bed set room_no = ?, bed_no = ?, bed_status = ?, remarks = ? where id = ?";
        return jt.update(SQL,
                bed.getRoomNo(),
                bed.getBedNo(),
                bed.getBedStatus(),
                bed.getRemarks() == null ? "" : bed.getRemarks(),
                bed.getId());
    }

    @Override
    public int deleteById(int id) {
        String SQL = "delete from bed where id = ?";
        return jt.update(SQL, id);
    }

    private List<Bed> toBedList(List<Object> list) {
        List<Bed> beds = new ArrayList<>();
        for (Object o : list) {
            beds.add((Bed) o);
        }
        return beds;
    }
}
