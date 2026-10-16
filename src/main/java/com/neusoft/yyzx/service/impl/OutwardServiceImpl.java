package com.neusoft.yyzx.service.impl;

import com.neusoft.yyzx.common.constant.AuditStatusConstant;
import com.neusoft.yyzx.common.constant.BedStatusConstant;
import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.common.result.ResultCode;
import com.neusoft.yyzx.dao.impl.BedDAOImpl;
import com.neusoft.yyzx.dao.impl.CustomerDAOImpl;
import com.neusoft.yyzx.dao.impl.OutwardDAOImpl;
import com.neusoft.yyzx.db.TxManager;
import com.neusoft.yyzx.entity.Customer;
import com.neusoft.yyzx.entity.Outward;
import com.neusoft.yyzx.service.OutwardService;

import java.time.LocalDate;
import java.util.List;

public class OutwardServiceImpl implements OutwardService {

    /**
     * 提交外出申请：校验客户存在 + 外出/预计回院时间 -> 审批状态默认"已提交"
     */
    @Override
    public Result<Integer> submit(Outward outward) {
        return TxManager.tx(conn -> {
            CustomerDAOImpl customerDAO = new CustomerDAOImpl();
            OutwardDAOImpl outwardDAO = new OutwardDAOImpl();
            customerDAO.setConnection(conn);
            outwardDAO.setConnection(conn);

            if (outward.getCustomerId() == null
                    || customerDAO.findById(outward.getCustomerId()) == null) {
                return Result.fail(ResultCode.CUSTOMER_NOT_FOUND);
            }
            if (outward.getOutgoingtime() == null || outward.getExpectedreturntime() == null) {
                return Result.fail(ResultCode.PARAM_ERROR);
            }
            // 提交时：审批状态默认已提交，实际回院时间/审批人/审批时间暂空
            outward.setAuditstatus(AuditStatusConstant.SUBMITTED);
            outward.setActualreturntime(null);
            outward.setAuditperson("");
            outward.setAudittime(null);

            int id = outwardDAO.insert(outward);
            return Result.success(id);
        });
    }

    @Override
    public List<Outward> listByCustomerName(String name) {
        return TxManager.query(conn -> {
            OutwardDAOImpl outwardDAO = new OutwardDAOImpl();
            outwardDAO.setConnection(conn);
            return outwardDAO.listByCustomerName(name);
        });
    }

    @Override
    public List<Outward> listByCustomerNameAndUserId(String name, int userId) {
        return TxManager.query(conn -> {
            OutwardDAOImpl outwardDAO = new OutwardDAOImpl();
            outwardDAO.setConnection(conn);
            return outwardDAO.listByCustomerNameAndUserId(name, userId);
        });
    }

    /**
     * 审核外出申请：通过 -> 客户正在使用的床位改为"外出"
     */
    @Override
    public Result<Void> audit(int id, boolean agree, String operatorName) {
        return TxManager.tx(conn -> {
            OutwardDAOImpl outwardDAO = new OutwardDAOImpl();
            CustomerDAOImpl customerDAO = new CustomerDAOImpl();
            BedDAOImpl bedDAO = new BedDAOImpl();
            outwardDAO.setConnection(conn);
            customerDAO.setConnection(conn);
            bedDAO.setConnection(conn);

            Outward outward = outwardDAO.findById(id);
            if (outward == null) {
                return Result.fail(ResultCode.OUTWARD_NOT_FOUND);
            }
            if (!AuditStatusConstant.SUBMITTED.equals(outward.getAuditstatus())) {
                return Result.fail(ResultCode.AUDIT_STATUS_INVALID);
            }
            LocalDate today = LocalDate.now();
            if (agree) {
                Customer customer = customerDAO.findById(outward.getCustomerId());
                if (customer == null) {
                    return Result.fail(ResultCode.CUSTOMER_NOT_FOUND);
                }
                // 审核通过：床位状态改为"外出"
                bedDAO.updateStatus(customer.getBedId(), BedStatusConstant.OUTWARD);
                outwardDAO.audit(id, AuditStatusConstant.AGREE, operatorName, today);
            } else {
                outwardDAO.audit(id, AuditStatusConstant.REJECT, operatorName, today);
            }
            return Result.success();
        });
    }

    /**
     * 登记实际回院时间：老人回院后登记，并把床位恢复为"有人"
     */
    @Override
    public Result<Void> registerReturn(int id, LocalDate actualReturnTime) {
        return TxManager.tx(conn -> {
            OutwardDAOImpl outwardDAO = new OutwardDAOImpl();
            CustomerDAOImpl customerDAO = new CustomerDAOImpl();
            BedDAOImpl bedDAO = new BedDAOImpl();
            outwardDAO.setConnection(conn);
            customerDAO.setConnection(conn);
            bedDAO.setConnection(conn);

            Outward outward = outwardDAO.findById(id);
            if (outward == null) {
                return Result.fail(ResultCode.OUTWARD_NOT_FOUND);
            }
            if (!AuditStatusConstant.AGREE.equals(outward.getAuditstatus())) {
                return Result.fail("该外出申请尚未审核通过，不能登记回院");
            }
            if (actualReturnTime == null) {
                return Result.fail(ResultCode.PARAM_ERROR);
            }
            outwardDAO.updateActualReturnTime(id, actualReturnTime);
            Customer customer = customerDAO.findById(outward.getCustomerId());
            if (customer != null) {
                // 老人回院：床位状态恢复为"有人"
                bedDAO.updateStatus(customer.getBedId(), BedStatusConstant.OCCUPIED);
            }
            return Result.success();
        });
    }
}
