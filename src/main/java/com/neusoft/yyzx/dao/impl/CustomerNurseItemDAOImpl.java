package com.neusoft.yyzx.dao.impl;

import com.neusoft.yyzx.dao.CustomerNurseItemDAO;
import com.neusoft.yyzx.entity.CustomerNurseItem;
import com.neusoft.yyzx.mapping.EntityMapping;
import com.neusoft.yyzx.mapping.MappingFactory;

import java.util.ArrayList;
import java.util.List;

public class CustomerNurseItemDAOImpl extends BaseDao implements CustomerNurseItemDAO {

    private static final String SELECT_JOIN =
            "select cni.*, nc.nursing_name as item_name, nc.service_price as service_price"
                    + " from customernurseitem cni join nursecontent nc on cni.item_id = nc.id";

    @Override
    public List<CustomerNurseItem> listByCustomerId(int customerId) {
        String SQL = SELECT_JOIN + " where cni.custormer_id = ? and cni.is_deleted = 0 order by cni.id";
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.CUSTOMER_NURSE_ITEM_MAPPING);
        List<CustomerNurseItem> items = new ArrayList<>();
        for (Object o : jt.query(SQL, mapping, customerId)) {
            items.add((CustomerNurseItem) o);
        }
        return items;
    }

    @Override
    public CustomerNurseItem getById(int id) {
        String SQL = SELECT_JOIN + " where cni.id = ? and cni.is_deleted = 0";
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.CUSTOMER_NURSE_ITEM_MAPPING);
        return (CustomerNurseItem) jt.queryOne(SQL, mapping, id);
    }

    @Override
    public int insert(CustomerNurseItem item) {
        String SQL = "insert into customernurseitem(item_id, custormer_id, level_id, nurse_number,"
                + " buy_time, maturity_time, is_deleted) values(?,?,?,?,?,?,0)";
        return jt.insert(SQL, item.getItemId(), item.getCustormerId(), item.getLevelId(),
                item.getNurseNumber(), java.sql.Date.valueOf(item.getBuyTime()),
                java.sql.Date.valueOf(item.getMaturityTime()));
    }

    @Override
    public int reduceNumber(int id, int count) {
        String SQL = "update customernurseitem set nurse_number = nurse_number - ?"
                + " where id = ? and nurse_number >= ?";
        return jt.update(SQL, count, id, count);
    }

    @Override
    public int deleteByCustomerAndLevel(int customerId, int levelId) {
        String SQL = "update customernurseitem set is_deleted = 1"
                + " where custormer_id = ? and level_id = ? and is_deleted = 0";
        return jt.update(SQL, customerId, levelId);
    }

    @Override
    public boolean existsByCustomerAndItem(int customerId, int itemId) {
        String SQL = SELECT_JOIN + " where cni.custormer_id = ? and cni.item_id = ? and cni.is_deleted = 0 limit 1";
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.CUSTOMER_NURSE_ITEM_MAPPING);
        return jt.queryOne(SQL, mapping, customerId, itemId) != null;
    }

    @Override
    public int renewItem(int id, java.time.LocalDate newMaturityTime, int newNumber) {
        String SQL = "update customernurseitem set nurse_number = ?, maturity_time = ?"
                + " where id = ? and is_deleted = 0";
        return jt.update(SQL, newNumber, java.sql.Date.valueOf(newMaturityTime), id);
    }

    @Override
    public int deleteById(int id) {
        String SQL = "update customernurseitem set is_deleted = 1 where id = ? and is_deleted = 0";
        return jt.update(SQL, id);
    }

    @Override
    public List<Integer> listPurchasedItemIds(int customerId) {
        List<CustomerNurseItem> items = listByCustomerId(customerId);
        List<Integer> ids = new ArrayList<>();
        for (CustomerNurseItem item : items) {
            ids.add(item.getItemId());
        }
        return ids;
    }
}