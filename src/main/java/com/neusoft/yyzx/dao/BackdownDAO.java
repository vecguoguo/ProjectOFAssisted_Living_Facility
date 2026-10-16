package com.neusoft.yyzx.dao;

import com.neusoft.yyzx.entity.Backdown;

import java.time.LocalDate;
import java.util.List;

/**
 * backdown 表数据访问接口
 */
public interface BackdownDAO {
    /**
     * 新增退住登记（申请），返回自增主键
     * @param b 退住登记对象
     * @return 自增主键
     */
    public int insert(Backdown b);

    /**
     * 根据ID查询退住登记（含客户姓名）
     * @param id 登记ID
     * @return 退住登记对象，不存在返回null
     */
    public Backdown findById(int id);

    /**
     * 按客户姓名模糊查询全部退住登记（管理员用）
     * @param name 客户姓名（模糊），为空查全部
     * @return 退住登记列表
     */
    public List<Backdown> listByCustomerName(String name);

    /**
     * 按客户姓名模糊查询某管家服务的客户的退住登记（健康管家用）
     * @param name 客户姓名（模糊），为空查全部
     * @param userId 管家ID
     * @return 退住登记列表
     */
    public List<Backdown> listByCustomerNameAndUserId(String name, int userId);

    /**
     * 审批：更新审批状态、审批人、审批时间
     * @param id 登记ID
     * @param auditstatus 审批状态
     * @param auditperson 审批人
     * @param audittime 审批时间
     * @return 影响行数
     */
    public int audit(int id, int auditstatus, String auditperson, LocalDate audittime);
}
