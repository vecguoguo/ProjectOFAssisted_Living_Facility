package com.neusoft.yyzx.dao;

import com.neusoft.yyzx.entity.Outward;

import java.time.LocalDate;
import java.util.List;

/**
 * outward 表数据访问接口
 */
public interface OutwardDAO {
    /**
     * 新增外出登记（申请），返回自增主键
     * @param o 外出登记对象
     * @return 自增主键
     */
    public int insert(Outward o);

    /**
     * 根据ID查询外出登记（含客户姓名）
     * @param id 登记ID
     * @return 外出登记对象，不存在返回null
     */
    public Outward findById(int id);

    /**
     * 按客户姓名模糊查询全部外出登记（管理员用）
     * @param name 客户姓名（模糊），为空查全部
     * @return 外出登记列表
     */
    public List<Outward> listByCustomerName(String name);

    /**
     * 按客户姓名模糊查询某管家服务的客户的外出登记（健康管家用）
     * @param name 客户姓名（模糊），为空查全部
     * @param userId 管家ID
     * @return 外出登记列表
     */
    public List<Outward> listByCustomerNameAndUserId(String name, int userId);

    /**
     * 审批：更新审批状态、审批人、审批时间
     * @param id 登记ID
     * @param auditstatus 审批状态
     * @param auditperson 审批人
     * @param audittime 审批时间
     * @return 影响行数
     */
    public int audit(int id, int auditstatus, String auditperson, LocalDate audittime);

    /**
     * 登记实际回院时间
     * @param id 登记ID
     * @param actualReturnTime 实际回院时间
     * @return 影响行数
     */
    public int updateActualReturnTime(int id, LocalDate actualReturnTime);
}
