package com.neusoft.yyzx.service;

import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.entity.Customer;

import java.util.List;

public interface CustomerService {

    public static final String BUILDING_NO = "606";

    public Result<Integer> checkIn(Customer customer);

    public Customer findById(int id);

    public List<Customer> listAll();

    public List<Customer> listByUserId(int userId);

    public List<Customer> listWithoutButler(String nameLike);

    public Result<Void> assignButler(int customerId, int butlerId);

    public Result<Void> removeButler(int customerId);

    /**
     * 修改客户资料（联系电话、家属、健康信息等；姓名/性别/出生日期可改，年龄随出生日期重算）
     */
    public Result<Void> update(Customer customer);

    /**
     * 退住/删除客户：结束床位使用记录 -> 释放床位 -> 逻辑删除客户
     */
    public Result<Void> remove(int customerId);
}