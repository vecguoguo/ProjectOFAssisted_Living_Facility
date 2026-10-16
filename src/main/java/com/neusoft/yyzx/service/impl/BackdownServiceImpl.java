package com.neusoft.yyzx.service.impl;

import com.neusoft.yyzx.common.constant.AuditStatusConstant;
import com.neusoft.yyzx.common.constant.BedStatusConstant;
import com.neusoft.yyzx.common.constant.RetreatTypeConstant;
import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.common.result.ResultCode;
import com.neusoft.yyzx.dao.impl.BackdownDAOImpl;
import com.neusoft.yyzx.dao.impl.BedDAOImpl;
import com.neusoft.yyzx.dao.impl.BedDetailsDAOImpl;
import com.neusoft.yyzx.dao.impl.CustomerDAOImpl;
import com.neusoft.yyzx.db.TxManager;
import com.neusoft.yyzx.entity.Backdown;
import com.neusoft.yyzx.entity.BedDetails;
import com.neusoft.yyzx.entity.Customer;
import com.neusoft.yyzx.service.BackdownService;

import java.time.LocalDate;
import java.util.List;

public class BackdownServiceImpl implements BackdownService {

    /**
     * 提交退住申请：校验客户存在 + 退住时间 -> 审批状态默认"已提交"
     */
    @Override
    public Result<Integer> submit(Backdown backdown) {
        return TxManager.tx(conn -> {
            CustomerDAOImpl customerDAO = new CustomerDAOImpl();
            BackdownDAOImpl backdownDAO = new BackdownDAOImpl();
            customerDAO.setConnection(conn);
            backdownDAO.setConnection(conn);

            if (backdown.getCustomerId() == null
                    || customerDAO.findById(backdown.getCustomerId()) == null) {
                return Result.fail(ResultCode.CUSTOMER_NOT_FOUND);
            }
            if (backdown.getRetreattype() == null || backdown.getRetreattime() == null) {
                return Result.fail(ResultCode.PARAM_ERROR);
            }
            // 提交时：审批状态默认已提交，审批人/审批时间暂空
            backdown.setAuditstatus(AuditStatusConstant.SUBMITTED);
            backdown.setAuditperson("");
            backdown.setAudittime(null);

            int id = backdownDAO.insert(backdown);
            return Result.success(id);
        });
    }

    @Override
    public List<Backdown> listByCustomerName(String name) {
        return TxManager.query(conn -> {
            BackdownDAOImpl backdownDAO = new BackdownDAOImpl();
            backdownDAO.setConnection(conn);
            return backdownDAO.listByCustomerName(name);
        });
    }

    @Override
    public List<Backdown> listByCustomerNameAndUserId(String name, int userId) {
        return TxManager.query(conn -> {
            BackdownDAOImpl backdownDAO = new BackdownDAOImpl();
            backdownDAO.setConnection(conn);
            return backdownDAO.listByCustomerNameAndUserId(name, userId);
        });
    }

    /**
     * 审核退住申请：正常/死亡退住通过 -> 结束在住床位记录、释放床位并逻辑删除客户（完成出院）；
     * 保留床位 -> 床位与客户状态不变
     */
    @Override
    public Result<Void> audit(int id, boolean agree, String operatorName) {
        return TxManager.tx(conn -> {
            BackdownDAOImpl backdownDAO = new BackdownDAOImpl();
            CustomerDAOImpl customerDAO = new CustomerDAOImpl();
            BedDAOImpl bedDAO = new BedDAOImpl();
            BedDetailsDAOImpl bedDetailsDAO = new BedDetailsDAOImpl();
            backdownDAO.setConnection(conn);
            customerDAO.setConnection(conn);
            bedDAO.setConnection(conn);
            bedDetailsDAO.setConnection(conn);

            Backdown backdown = backdownDAO.findById(id);
            if (backdown == null) {
                return Result.fail(ResultCode.BACKDOWN_NOT_FOUND);
            }
            if (!AuditStatusConstant.SUBMITTED.equals(backdown.getAuditstatus())) {
                return Result.fail(ResultCode.AUDIT_STATUS_INVALID);
            }
            LocalDate today = LocalDate.now();
            if (agree) {
                // 正常退住 / 死亡退住：完成退住并释放床位 —— 结束在住床位记录、床位改为"空闲"、逻辑删除客户；
                // 客户出院后不再出现在在住客户/外出申请/退住申请等业务列表中。保留床位：床位与客户状态不变。
                if (RetreatTypeConstant.NORMAL.equals(backdown.getRetreattype())
                        || RetreatTypeConstant.DEATH.equals(backdown.getRetreattype())) {
                    Customer customer = customerDAO.findById(backdown.getCustomerId());
                    if (customer == null) {
                        return Result.fail(ResultCode.CUSTOMER_NOT_FOUND);
                    }
                    BedDetails active = bedDetailsDAO.findActiveByCustomerId(customer.getId());
                    if (active != null) {
                        bedDetailsDAO.expire(active.getId(), today);
                    }
                    if (customer.getBedId() != null) {
                        bedDAO.updateStatus(customer.getBedId(), BedStatusConstant.FREE);
                    }
                    customerDAO.deleteById(customer.getId());
                }
                backdownDAO.audit(id, AuditStatusConstant.AGREE, operatorName, today);
            } else {
                backdownDAO.audit(id, AuditStatusConstant.REJECT, operatorName, today);
            }
            return Result.success();
        });
    }
}
