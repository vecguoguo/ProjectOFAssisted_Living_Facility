package com.neusoft.yyzx.service;

import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.entity.Backdown;

import java.util.List;

/**
 * 退住登记业务接口：申请、查询、审核（对应项目结构 客户管理-退住登记）
 */
public interface BackdownService {

    /**
     * 提交退住申请（审批状态默认"已提交"）
     * @param backdown 退住登记对象
     * @return 结果（data为登记ID）
     */
    public Result<Integer> submit(Backdown backdown);

    /**
     * 按客户姓名模糊查询全部退住登记（管理员用）
     * @param name 客户姓名（模糊），为空查全部
     * @return 退住登记列表
     */
    public List<Backdown> listByCustomerName(String name);

    /**
     * 按客户姓名模糊查询某管家服务客户的退住登记（健康管家用）
     * @param name 客户姓名（模糊），为空查全部
     * @param userId 管家ID
     * @return 退住登记列表
     */
    public List<Backdown> listByCustomerNameAndUserId(String name, int userId);

    /**
     * 审核退住申请：正常/死亡退住通过时客户床位改为"空闲"，保留床位状态不变
     * @param id 登记ID
     * @param agree true-同意 false-拒绝
     * @param operatorName 审批人姓名
     * @return 审核结果
     */
    public Result<Void> audit(int id, boolean agree, String operatorName);
}
