package com.neusoft.yyzx.service.impl;

import com.neusoft.yyzx.common.constant.BedStatusConstant;
import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.common.result.ResultCode;
import com.neusoft.yyzx.common.utils.DateUtil;
import com.neusoft.yyzx.dao.impl.BedDAOImpl;
import com.neusoft.yyzx.dao.impl.BedDetailsDAOImpl;
import com.neusoft.yyzx.dao.impl.CustomerDAOImpl;
import com.neusoft.yyzx.db.TxManager;
import com.neusoft.yyzx.entity.Bed;
import com.neusoft.yyzx.entity.BedDetails;
import com.neusoft.yyzx.entity.Customer;
import com.neusoft.yyzx.service.CustomerService;

import java.time.LocalDate;
import java.util.List;

public class CustomerServiceImpl implements CustomerService {

    @Override
    public Result<Integer> checkIn(Customer customer) {
        return TxManager.tx(conn -> {
            if (customer.getExpirationDate() == null || customer.getCheckinDate() == null
                    || !customer.getExpirationDate().isAfter(customer.getCheckinDate())) {
                return Result.fail(ResultCode.CONTRACT_DATE_INVALID);
            }
            if (customer.getIdcard() == null || customer.getIdcard().isBlank()) {
                return Result.fail(ResultCode.PARAM_ERROR);
            }
            CustomerDAOImpl customerDAO = new CustomerDAOImpl();
            BedDAOImpl bedDAO = new BedDAOImpl();
            BedDetailsDAOImpl bedDetailsDAO = new BedDetailsDAOImpl();
            customerDAO.setConnection(conn);
            bedDAO.setConnection(conn);
            bedDetailsDAO.setConnection(conn);

            if (customerDAO.findByIdcard(customer.getIdcard()) != null) {
                return Result.fail("该身份证号已登记入住");
            }
            // 加行锁读取目标床位：锁持有至事务提交，避免并发抢同一张空闲床
            Bed bed = bedDAO.findByIdForUpdate(customer.getBedId());
            if (bed == null || !BedStatusConstant.FREE.equals(bed.getBedStatus())) {
                return Result.fail(ResultCode.BED_NOT_FREE);
            }

            customer.setCustomerAge(DateUtil.calcAge(customer.getBirthday()));
            customer.setBuildingNo(BUILDING_NO);
            customer.setRoomNo(String.valueOf(bed.getRoomNo()));

            LocalDate today = LocalDate.now();
            int customerId = customerDAO.insert(customer);
            bedDAO.updateStatus(bed.getId(), BedStatusConstant.OCCUPIED);

            BedDetails details = new BedDetails();
            details.setStartDate(today);
            details.setEndDate(null);
            details.setBedDetails("入住登记-床位" + bed.getBedNo());
            details.setCustomerId(customerId);
            details.setBedId(bed.getId());
            bedDetailsDAO.insert(details);
            return Result.success(customerId);
        });
    }

    @Override
    public Customer findById(int id) {
        return TxManager.query(conn -> {
            CustomerDAOImpl customerDAO = new CustomerDAOImpl();
            customerDAO.setConnection(conn);
            return customerDAO.findById(id);
        });
    }

    @Override
    public List<Customer> listAll() {
        return TxManager.query(conn -> {
            CustomerDAOImpl customerDAO = new CustomerDAOImpl();
            customerDAO.setConnection(conn);
            return customerDAO.listAll();
        });
    }

    @Override
    public List<Customer> listByUserId(int userId) {
        return TxManager.query(conn -> {
            CustomerDAOImpl customerDAO = new CustomerDAOImpl();
            customerDAO.setConnection(conn);
            return customerDAO.listByUserId(userId);
        });
    }

    @Override
    public List<Customer> listWithoutButler(String nameLike) {
        return TxManager.query(conn -> {
            CustomerDAOImpl dao = new CustomerDAOImpl();
            dao.setConnection(conn);
            return dao.listWithoutButler(nameLike);
        });
    }

    @Override
    public Result<Void> assignButler(int customerId, int butlerId) {
        return TxManager.tx(conn -> {
            CustomerDAOImpl customerDAO = new CustomerDAOImpl();
            customerDAO.setConnection(conn);
            Customer customer = customerDAO.findById(customerId);
            if (customer == null) {
                return Result.fail(ResultCode.CUSTOMER_NOT_FOUND);
            }
            if (butlerId <= 0) {
                return Result.fail(ResultCode.BUTLER_NOT_FOUND);
            }
            customerDAO.updateUserId(customerId, butlerId);
            return Result.success();
        });
    }

    @Override
    public Result<Void> removeButler(int customerId) {
        return TxManager.tx(conn -> {
            CustomerDAOImpl customerDAO = new CustomerDAOImpl();
            customerDAO.setConnection(conn);
            Customer customer = customerDAO.findById(customerId);
            if (customer == null) {
                return Result.fail(ResultCode.CUSTOMER_NOT_FOUND);
            }
            if (customer.getUserId() == null || customer.getUserId() == -1) {
                return Result.fail(ResultCode.CUSTOMER_NO_BUTLER);
            }
            customerDAO.updateUserId(customerId, -1);
            return Result.success();
        });
    }

    /**
     * 修改客户资料：仅更新允许编辑的字段（姓名/性别/出生日期/联系电话/家属/健康信息），
     * 身份证号、房间床位等由入住/调换流程维护，此处不变更；年龄随出生日期重算。
     */
    @Override
    public Result<Void> update(Customer customer) {
        return TxManager.tx(conn -> {
            if (customer == null || customer.getId() == null) {
                return Result.fail(ResultCode.PARAM_ERROR);
            }
            CustomerDAOImpl customerDAO = new CustomerDAOImpl();
            customerDAO.setConnection(conn);
            Customer exist = customerDAO.findById(customer.getId());
            if (exist == null) {
                return Result.fail(ResultCode.CUSTOMER_NOT_FOUND);
            }
            if (customer.getCustomerName() == null || customer.getCustomerName().isBlank()) {
                return Result.fail(ResultCode.PARAM_ERROR);
            }
            // 出生日期缺省时沿用原值；有值时按出生日期重算年龄
            if (customer.getBirthday() == null) {
                customer.setBirthday(exist.getBirthday());
            }
            if (customer.getBirthday() != null) {
                customer.setCustomerAge(DateUtil.calcAge(customer.getBirthday()));
            } else {
                customer.setCustomerAge(exist.getCustomerAge());
            }
            customerDAO.update(customer);
            return Result.success();
        });
    }

    /**
     * 退住/删除客户：结束在住的床位使用记录 -> 释放床位 -> 逻辑删除客户。
     */
    @Override
    public Result<Void> remove(int customerId) {
        return TxManager.tx(conn -> {
            CustomerDAOImpl customerDAO = new CustomerDAOImpl();
            BedDAOImpl bedDAO = new BedDAOImpl();
            BedDetailsDAOImpl bedDetailsDAO = new BedDetailsDAOImpl();
            customerDAO.setConnection(conn);
            bedDAO.setConnection(conn);
            bedDetailsDAO.setConnection(conn);

            Customer customer = customerDAO.findById(customerId);
            if (customer == null) {
                return Result.fail(ResultCode.CUSTOMER_NOT_FOUND);
            }
            LocalDate today = LocalDate.now();
            BedDetails active = bedDetailsDAO.findActiveByCustomerId(customerId);
            if (active != null) {
                bedDetailsDAO.expire(active.getId(), today);
            }
            if (customer.getBedId() != null) {
                bedDAO.updateStatus(customer.getBedId(), BedStatusConstant.FREE);
            }
            customerDAO.deleteById(customerId);
            return Result.success();
        });
    }
}
