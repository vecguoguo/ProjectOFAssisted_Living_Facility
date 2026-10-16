package com.neusoft.yyzx.service;

import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.entity.Outward;

import java.time.LocalDate;
import java.util.List;

/**
 * 外出登记业务接口：申请、查询、审核、回院登记（对应项目结构 客户管理-外出登记）
 */
public interface OutwardService {

    /**
     * 提交外出申请（审批状态默认"已提交"）
     * @param outward 外出登记对象
     * @return 结果（data为登记ID）
     */
    public Result<Integer> submit(Outward outward);

    /**
     * 按客户姓名模糊查询全部外出登记（管理员用）
     * @param name 客户姓名（模糊），为空查全部
     * @return 外出登记列表
     */
    public List<Outward> listByCustomerName(String name);

    /**
     * 按客户姓名模糊查询某管家服务客户的外出登记（健康管家用）
     * @param name 客户姓名（模糊），为空查全部
     * @param userId 管家ID
     * @return 外出登记列表
     */
    public List<Outward> listByCustomerNameAndUserId(String name, int userId);

    /**
     * 审核外出申请：通过时客户床位改为"外出"
     * @param id 登记ID
     * @param agree true-同意 false-拒绝
     * @param operatorName 审批人姓名
     * @return 审核结果
     */
    public Result<Void> audit(int id, boolean agree, String operatorName);

    /**
     * 登记实际回院时间（老人回院后），并将床位恢复为"有人"
     * @param id 登记ID
     * @param actualReturnTime 实际回院时间
     * @return 登记结果
     */
    public Result<Void> registerReturn(int id, LocalDate actualReturnTime);
}
