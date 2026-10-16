package com.neusoft.yyzx.dao.impl;

import com.neusoft.yyzx.dao.CustomerDAO;
import com.neusoft.yyzx.entity.Customer;
import com.neusoft.yyzx.mapping.EntityMapping;
import com.neusoft.yyzx.mapping.MappingFactory;

import java.util.ArrayList;
import java.util.List;

public class CustomerDAOImpl extends BaseDao implements CustomerDAO {

    @Override
    public Customer findByIdcard(String idcard) {
        String SQL = "select * from customer where idcard = ? and is_deleted = 0";
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.CUSTOMER_MAPPING);
        return (Customer) jt.queryOne(SQL, mapping, idcard);
    }

    @Override
    public Customer findById(int id) {
        String SQL = "select * from customer where id = ? and is_deleted = 0";
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.CUSTOMER_MAPPING);
        return (Customer) jt.queryOne(SQL, mapping, id);
    }

    @Override
    public List<Customer> listAll() {
        String SQL = "select * from customer where is_deleted = 0 order by id";
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.CUSTOMER_MAPPING);
        List<Customer> customers = new ArrayList<>();
        for (Object o : jt.query(SQL, mapping)) {
            customers.add((Customer) o);
        }
        return customers;
    }

    @Override
    public List<Customer> listByUserId(int userId) {
        String SQL = "select * from customer where user_id = ? and is_deleted = 0 order by id";
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.CUSTOMER_MAPPING);
        List<Customer> customers = new ArrayList<>();
        for (Object o : jt.query(SQL, mapping, userId)) {
            customers.add((Customer) o);
        }
        return customers;
    }

    @Override
    public int insert(Customer c) {
        String SQL = "insert into customer(customer_name, customer_age, customer_sex, idcard, room_no,"
                + " building_no, bed_id, level_id, user_id, checkin_date, expiration_date, birthday,"
                + " contact_tel, family_member, height, weight, blood_type, psychosomatic_state,"
                + " attention, filepath, is_deleted)"
                + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,0)";
        return jt.insert(SQL,
                c.getCustomerName(),
                c.getCustomerAge(),
                c.getCustomerSex(),
                c.getIdcard(),
                c.getRoomNo(),
                c.getBuildingNo(),
                c.getBedId(),
                c.getLevelId(),
                c.getUserId() == null ? -1 : c.getUserId(),
                java.sql.Date.valueOf(c.getCheckinDate()),
                java.sql.Date.valueOf(c.getExpirationDate()),
                java.sql.Date.valueOf(c.getBirthday()),
                c.getContactTel(),
                c.getFamilyMember(),
                c.getHeight(),
                c.getWeight(),
                c.getBloodType(),
                c.getPsychosomaticState(),
                c.getAttention(),
                c.getFilepath() == null ? "" : c.getFilepath());
    }

    @Override
    public int updateBedInfo(int customerId, String roomNo, int bedId, String buildingNo) {
        String SQL = "update customer set room_no = ?, bed_id = ?, building_no = ? where id = ?";
        return jt.update(SQL, roomNo, bedId, buildingNo, customerId);
    }

    @Override
    public int updateLevelId(int customerId, Integer levelId) {
        String SQL = "update customer set level_id = ? where id = ?";
        return jt.update(SQL, levelId, customerId);
    }

    @Override
    public List<Customer> listByNameLike(String nameLike) {
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.CUSTOMER_MAPPING);
        StringBuilder SQL = new StringBuilder("select * from customer where is_deleted = 0");
        List<Object> args = new ArrayList<>();
        if (nameLike != null && !nameLike.isBlank()) {
            SQL.append(" and customer_name like concat('%',?,'%')");
            args.add(nameLike);
        }
        SQL.append(" order by id");
        return toCustomerList(jt.query(SQL.toString(), mapping, args.toArray()));
    }

    @Override
    public List<Customer> listByButler(int userId, String nameLike) {
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.CUSTOMER_MAPPING);
        StringBuilder SQL = new StringBuilder("select * from customer where is_deleted = 0 and user_id = ?");
        List<Object> args = new ArrayList<>();
        args.add(userId);
        if (nameLike != null && !nameLike.isBlank()) {
            SQL.append(" and customer_name like concat('%',?,'%')");
            args.add(nameLike);
        }
        SQL.append(" order by id");
        return toCustomerList(jt.query(SQL.toString(), mapping, args.toArray()));
    }

    @Override
    public List<Customer> listWithoutButler(String nameLike) {
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.CUSTOMER_MAPPING);
        StringBuilder SQL = new StringBuilder("select * from customer where is_deleted = 0 and user_id = -1");
        List<Object> args = new ArrayList<>();
        if (nameLike != null && !nameLike.isBlank()) {
            SQL.append(" and customer_name like concat('%',?,'%')");
            args.add(nameLike);
        }
        SQL.append(" order by id");
        return toCustomerList(jt.query(SQL.toString(), mapping, args.toArray()));
    }

    @Override
    public int updateUserId(int customerId, int userId) {
        String SQL = "update customer set user_id = ? where id = ? and is_deleted = 0";
        return jt.update(SQL, userId, customerId);
    }

    @Override
    public int update(Customer c) {
        String SQL = "update customer set customer_name = ?, customer_age = ?, customer_sex = ?,"
                + " birthday = ?, contact_tel = ?, family_member = ?, height = ?, weight = ?,"
                + " blood_type = ?, psychosomatic_state = ?, attention = ?"
                + " where id = ? and is_deleted = 0";
        return jt.update(SQL,
                c.getCustomerName(),
                c.getCustomerAge(),
                c.getCustomerSex(),
                c.getBirthday() == null ? null : java.sql.Date.valueOf(c.getBirthday()),
                c.getContactTel(),
                c.getFamilyMember(),
                c.getHeight(),
                c.getWeight(),
                c.getBloodType(),
                c.getPsychosomaticState(),
                c.getAttention(),
                c.getId());
    }

    @Override
    public int deleteById(int customerId) {
        String SQL = "update customer set is_deleted = 1 where id = ? and is_deleted = 0";
        return jt.update(SQL, customerId);
    }

    private List<Customer> toCustomerList(List<Object> list) {
        List<Customer> customers = new ArrayList<>();
        for (Object o : list) {
            customers.add((Customer) o);
        }
        return customers;
    }
}