package com.neusoft.yyzx.service;

import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.entity.Customer;
import com.neusoft.yyzx.entity.CustomerNurseItem;
import com.neusoft.yyzx.entity.NurseContent;
import com.neusoft.yyzx.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CustomerNurseService {

    public List<Customer> listCustomers(String nameLike);

    public List<Customer> listMyCustomers(int butlerId, String nameLike);

    public Result<Void> setLevel(int customerId, int levelId);

    public Result<Void> removeLevel(int customerId);

    public List<CustomerNurseItem> listCustomerItems(int customerId);

    public Result<Integer> doDailyNurse(int customerNurseItemId, int userId,
                                        LocalDateTime nursingTime, String nursingContent, int nursingCount);

    public Result<Void> renewItem(int customerNurseItemId, int addNumber, LocalDate newMaturityTime);

    public Result<Void> removeItem(int customerNurseItemId);

    public List<NurseContent> listAvailableItems(int customerId, String nameLike);

    public Result<Void> purchaseItem(int customerId, int itemId, int number, LocalDate maturityTime);

    public List<User> listButlers(String nameLike);
}