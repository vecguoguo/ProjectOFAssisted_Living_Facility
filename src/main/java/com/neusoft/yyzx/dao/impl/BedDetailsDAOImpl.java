package com.neusoft.yyzx.dao.impl;

import com.neusoft.yyzx.dao.BedDetailsDAO;
import com.neusoft.yyzx.entity.BedDetails;
import com.neusoft.yyzx.mapping.EntityMapping;
import com.neusoft.yyzx.mapping.MappingFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * beddetails 表数据访问实现
 */
public class BedDetailsDAOImpl extends BaseDao implements BedDetailsDAO {

    @Override
    public BedDetails findActiveByCustomerId(int customerId) {
        String SQL = "select * from beddetails where customer_id = ? and is_deleted = 0"
                + " order by start_date desc limit 1";
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.BED_DETAILS_MAPPING);
        return (BedDetails) jt.queryOne(SQL, mapping, customerId);
    }

    @Override
    public List<BedDetails> listByBedId(int bedId) {
        String SQL = "select * from beddetails where bed_id = ? and is_deleted = 0 order by start_date";
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.BED_DETAILS_MAPPING);
        List<BedDetails> details = new ArrayList<>();
        for (Object o : jt.query(SQL, mapping, bedId)) {
            details.add((BedDetails) o);
        }
        return details;
    }

    @Override
    public int insert(BedDetails d) {
        String SQL = "insert into beddetails(start_date, end_date, bed_details, customer_id, bed_id, is_deleted)"
                + " values(?,?,?,?,?,0)";
        return jt.insert(SQL,
                java.sql.Date.valueOf(d.getStartDate()),
                d.getEndDate() == null ? java.sql.Date.valueOf(LocalDate.of(9999, 12, 31))
                        : java.sql.Date.valueOf(d.getEndDate()),
                d.getBedDetails() == null ? "" : d.getBedDetails(),
                d.getCustomerId(), d.getBedId());
    }

    @Override
    public int expire(int id, LocalDate endDate) {
        String SQL = "update beddetails set end_date = ?, is_deleted = 1 where id = ?";
        return jt.update(SQL, java.sql.Date.valueOf(endDate), id);
    }
}
