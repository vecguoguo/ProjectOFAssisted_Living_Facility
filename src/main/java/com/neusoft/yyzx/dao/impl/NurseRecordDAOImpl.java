package com.neusoft.yyzx.dao.impl;

import com.neusoft.yyzx.dao.NurseRecordDAO;
import com.neusoft.yyzx.entity.NurseRecord;
import com.neusoft.yyzx.mapping.EntityMapping;
import com.neusoft.yyzx.mapping.MappingFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * nurserecord 表数据访问实现
 */
public class NurseRecordDAOImpl extends BaseDao implements NurseRecordDAO {

    @Override
    public List<NurseRecord> listByCustomerId(int customerId) {
        String SQL = "select nr.*, nc.nursing_name as item_name from nurserecord nr"
                + " join nursecontent nc on nr.item_id = nc.id"
                + " where nr.customer_id = ? and nr.is_deleted = 0 order by nr.nursing_time desc";
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.NURSE_RECORD_MAPPING);
        List<NurseRecord> records = new ArrayList<>();
        for (Object o : jt.query(SQL, mapping, customerId)) {
            records.add((NurseRecord) o);
        }
        return records;
    }

    @Override
    public int insert(NurseRecord r) {
        String SQL = "insert into nurserecord(customer_id, item_id, user_id, nursing_time,"
                + " nursing_content, nursing_count, is_deleted) values(?,?,?,?,?,?,0)";
        LocalDateTime time = r.getNursingTime() == null ? LocalDateTime.now() : r.getNursingTime();
        return jt.insert(SQL, r.getCustomerId(), r.getItemId(), r.getUserId(),
                java.sql.Timestamp.valueOf(time),
                r.getNursingContent() == null ? "" : r.getNursingContent(),
                r.getNursingCount());
    }

    @Override
    public int logicDelete(int id) {
        String SQL = "update nurserecord set is_deleted = 1 where id = ?";
        return jt.update(SQL, id);
    }
}
