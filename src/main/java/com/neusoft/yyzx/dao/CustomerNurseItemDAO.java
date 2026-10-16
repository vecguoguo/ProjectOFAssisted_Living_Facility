package com.neusoft.yyzx.dao;

import com.neusoft.yyzx.entity.CustomerNurseItem;

import java.util.List;

public interface CustomerNurseItemDAO {

    public List<CustomerNurseItem> listByCustomerId(int customerId);

    public CustomerNurseItem getById(int id);

    public int insert(CustomerNurseItem item);

    public int reduceNumber(int id, int count);

    public int deleteByCustomerAndLevel(int customerId, int levelId);

    public boolean existsByCustomerAndItem(int customerId, int itemId);

    public int renewItem(int id, java.time.LocalDate newMaturityTime, int newNumber);

    public int deleteById(int id);

    public List<Integer> listPurchasedItemIds(int customerId);
}