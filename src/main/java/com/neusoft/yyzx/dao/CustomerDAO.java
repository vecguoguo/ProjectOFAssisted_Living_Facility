package com.neusoft.yyzx.dao;

import com.neusoft.yyzx.entity.Customer;

import java.util.List;

public interface CustomerDAO {

    public Customer findByIdcard(String idcard);

    public Customer findById(int id);

    public List<Customer> listAll();

    public List<Customer> listByUserId(int userId);

    public int insert(Customer c);

    public int updateBedInfo(int customerId, String roomNo, int bedId, String buildingNo);

    public int updateLevelId(int customerId, Integer levelId);

    public List<Customer> listByNameLike(String nameLike);

    public List<Customer> listByButler(int userId, String nameLike);

    public List<Customer> listWithoutButler(String nameLike);

    public int updateUserId(int customerId, int userId);

    /**
     * 修改客户资料（姓名、性别、出生日期、联系电话、家属、健康信息等）
     * @param c 客户对象（id 必填）
     * @return 影响行数
     */
    public int update(Customer c);

    /**
     * 退住/删除客户（逻辑删除：is_deleted = 1）
     * @param customerId 客户ID
     * @return 影响行数
     */
    public int deleteById(int customerId);
}