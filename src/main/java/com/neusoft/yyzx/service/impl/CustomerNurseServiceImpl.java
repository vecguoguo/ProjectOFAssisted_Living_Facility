package com.neusoft.yyzx.service.impl;

import com.neusoft.yyzx.common.constant.NurseStatusConstant;
import com.neusoft.yyzx.common.constant.RoleConstant;
import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.common.result.ResultCode;
import com.neusoft.yyzx.dao.impl.CustomerDAOImpl;
import com.neusoft.yyzx.dao.impl.CustomerNurseItemDAOImpl;
import com.neusoft.yyzx.dao.impl.NurseContentDAOImpl;
import com.neusoft.yyzx.dao.impl.NurseLevelDAOImpl;
import com.neusoft.yyzx.dao.impl.NurseLevelItemDAOImpl;
import com.neusoft.yyzx.dao.impl.NurseRecordDAOImpl;
import com.neusoft.yyzx.dao.impl.UserDAOImpl;
import com.neusoft.yyzx.db.TxManager;
import com.neusoft.yyzx.entity.Customer;
import com.neusoft.yyzx.entity.CustomerNurseItem;
import com.neusoft.yyzx.entity.NurseContent;
import com.neusoft.yyzx.entity.NurseRecord;
import com.neusoft.yyzx.entity.User;
import com.neusoft.yyzx.service.CustomerNurseService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CustomerNurseServiceImpl implements CustomerNurseService {

    private static final int DEFAULT_NURSE_NUMBER = 1;
    private static final int DEFAULT_MATURITY_MONTHS = 3;

    @Override
    public List<Customer> listCustomers(String nameLike) {
        return TxManager.query(conn -> {
            CustomerDAOImpl dao = new CustomerDAOImpl();
            dao.setConnection(conn);
            return dao.listByNameLike(nameLike);
        });
    }

    @Override
    public List<Customer> listMyCustomers(int butlerId, String nameLike) {
        return TxManager.query(conn -> {
            CustomerDAOImpl dao = new CustomerDAOImpl();
            dao.setConnection(conn);
            return dao.listByButler(butlerId, nameLike);
        });
    }

    @Override
    public Result<Void> setLevel(int customerId, int levelId) {
        return TxManager.tx(conn -> {
            CustomerDAOImpl customerDAO = new CustomerDAOImpl();
            NurseLevelDAOImpl levelDAO = new NurseLevelDAOImpl();
            NurseLevelItemDAOImpl levelItemDAO = new NurseLevelItemDAOImpl();
            CustomerNurseItemDAOImpl cniDAO = new CustomerNurseItemDAOImpl();
            customerDAO.setConnection(conn);
            levelDAO.setConnection(conn);
            levelItemDAO.setConnection(conn);
            cniDAO.setConnection(conn);

            Customer customer = customerDAO.findById(customerId);
            if (customer == null) {
                return Result.fail(ResultCode.CUSTOMER_NOT_FOUND);
            }
            if (customer.getLevelId() != null) {
                return Result.fail(ResultCode.NURSE_LEVEL_ALREADY_SET);
            }
            if (levelDAO.findById(levelId) == null) {
                return Result.fail(ResultCode.NURSE_LEVEL_NOT_FOUND);
            }

            customerDAO.updateLevelId(customerId, levelId);
            LocalDate today = LocalDate.now();
            List<NurseContent> items = levelItemDAO.listItemsByLevelId(levelId);
            for (NurseContent item : items) {
                CustomerNurseItem cni = new CustomerNurseItem();
                cni.setItemId(item.getId());
                cni.setCustormerId(customerId);
                cni.setLevelId(levelId);
                cni.setNurseNumber(DEFAULT_NURSE_NUMBER);
                cni.setBuyTime(today);
                cni.setMaturityTime(today.plusMonths(DEFAULT_MATURITY_MONTHS));
                cniDAO.insert(cni);
            }
            return Result.success();
        });
    }

    @Override
    public Result<Void> removeLevel(int customerId) {
        return TxManager.tx(conn -> {
            CustomerDAOImpl customerDAO = new CustomerDAOImpl();
            CustomerNurseItemDAOImpl cniDAO = new CustomerNurseItemDAOImpl();
            customerDAO.setConnection(conn);
            cniDAO.setConnection(conn);

            Customer customer = customerDAO.findById(customerId);
            if (customer == null) {
                return Result.fail(ResultCode.CUSTOMER_NOT_FOUND);
            }
            Integer levelId = customer.getLevelId();
            if (levelId == null) {
                return Result.fail(ResultCode.NURSE_LEVEL_NOT_SET);
            }

            customerDAO.updateLevelId(customerId, null);
            cniDAO.deleteByCustomerAndLevel(customerId, levelId);
            return Result.success();
        });
    }

    @Override
    public List<CustomerNurseItem> listCustomerItems(int customerId) {
        return TxManager.query(conn -> {
            CustomerNurseItemDAOImpl dao = new CustomerNurseItemDAOImpl();
            dao.setConnection(conn);
            return dao.listByCustomerId(customerId);
        });
    }

    @Override
    public Result<Integer> doDailyNurse(int customerNurseItemId, int userId,
                                        LocalDateTime nursingTime, String nursingContent, int nursingCount) {
        return TxManager.tx(conn -> {
            CustomerNurseItemDAOImpl cniDAO = new CustomerNurseItemDAOImpl();
            NurseRecordDAOImpl recordDAO = new NurseRecordDAOImpl();
            cniDAO.setConnection(conn);
            recordDAO.setConnection(conn);

            CustomerNurseItem item = cniDAO.getById(customerNurseItemId);
            if (item == null) {
                return Result.fail(ResultCode.NURSE_ITEM_NOT_FOUND);
            }
            int reduced = cniDAO.reduceNumber(customerNurseItemId, nursingCount);
            if (reduced == 0) {
                // 剩余次数不足：扣减未影响任何行（无写副作用），直接返回失败
                return Result.fail(ResultCode.NURSE_NUMBER_NOT_ENOUGH);
            }
            NurseRecord record = new NurseRecord();
            record.setCustomerId(item.getCustormerId());
            record.setItemId(item.getItemId());
            record.setUserId(userId);
            record.setNursingTime(nursingTime == null ? LocalDateTime.now() : nursingTime);
            record.setNursingContent(nursingContent);
            record.setNursingCount(nursingCount);
            int recordId = recordDAO.insert(record);
            return Result.success(recordId);
        });
    }

    @Override
    public Result<Void> renewItem(int customerNurseItemId, int addNumber, LocalDate newMaturityTime) {
        return TxManager.tx(conn -> {
            CustomerNurseItemDAOImpl cniDAO = new CustomerNurseItemDAOImpl();
            cniDAO.setConnection(conn);

            CustomerNurseItem item = cniDAO.getById(customerNurseItemId);
            if (item == null) {
                return Result.fail(ResultCode.CUSTOMER_NURSE_ITEM_NOT_FOUND);
            }
            int newNumber = item.getNurseNumber() + addNumber;
            cniDAO.renewItem(customerNurseItemId, newMaturityTime, newNumber);
            return Result.success();
        });
    }

    @Override
    public Result<Void> removeItem(int customerNurseItemId) {
        return TxManager.tx(conn -> {
            CustomerNurseItemDAOImpl cniDAO = new CustomerNurseItemDAOImpl();
            cniDAO.setConnection(conn);

            CustomerNurseItem item = cniDAO.getById(customerNurseItemId);
            if (item == null) {
                return Result.fail(ResultCode.CUSTOMER_NURSE_ITEM_NOT_FOUND);
            }
            cniDAO.deleteById(customerNurseItemId);
            return Result.success();
        });
    }

    @Override
    public List<NurseContent> listAvailableItems(int customerId, String nameLike) {
        return TxManager.query(conn -> {
            NurseContentDAOImpl contentDAO = new NurseContentDAOImpl();
            CustomerNurseItemDAOImpl cniDAO = new CustomerNurseItemDAOImpl();
            contentDAO.setConnection(conn);
            cniDAO.setConnection(conn);

            List<NurseContent> allItems = contentDAO.listByStatusAndName(NurseStatusConstant.ENABLED, nameLike);
            List<Integer> purchasedIds = cniDAO.listPurchasedItemIds(customerId);
            List<NurseContent> available = new ArrayList<>();
            for (NurseContent item : allItems) {
                if (!purchasedIds.contains(item.getId())) {
                    available.add(item);
                }
            }
            return available;
        });
    }

    @Override
    public Result<Void> purchaseItem(int customerId, int itemId, int number, LocalDate maturityTime) {
        return TxManager.tx(conn -> {
            CustomerNurseItemDAOImpl cniDAO = new CustomerNurseItemDAOImpl();
            CustomerDAOImpl customerDAO = new CustomerDAOImpl();
            cniDAO.setConnection(conn);
            customerDAO.setConnection(conn);

            Customer customer = customerDAO.findById(customerId);
            if (customer == null) {
                return Result.fail(ResultCode.CUSTOMER_NOT_FOUND);
            }
            if (cniDAO.existsByCustomerAndItem(customerId, itemId)) {
                return Result.fail(ResultCode.NURSE_ITEM_ALREADY_PURCHASED);
            }
            CustomerNurseItem cni = new CustomerNurseItem();
            cni.setItemId(itemId);
            cni.setCustormerId(customerId);
            cni.setLevelId(0);
            cni.setNurseNumber(number);
            cni.setBuyTime(LocalDate.now());
            cni.setMaturityTime(maturityTime);
            cniDAO.insert(cni);
            return Result.success();
        });
    }

    @Override
    public List<User> listButlers(String nameLike) {
        return TxManager.query(conn -> {
            UserDAOImpl dao = new UserDAOImpl();
            dao.setConnection(conn);
            return dao.listByRoleIdAndNicknameLike(RoleConstant.HEALTH_BUTLER, nameLike);
        });
    }
}
