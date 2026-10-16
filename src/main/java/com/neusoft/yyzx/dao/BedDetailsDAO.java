package com.neusoft.yyzx.dao;

import com.neusoft.yyzx.entity.BedDetails;

import java.time.LocalDate;
import java.util.List;

/**
 * beddetails 表数据访问接口
 */
public interface BedDetailsDAO {
    /**
     * 查询客户当前生效的床位详情
     * @param customerId 客户ID
     * @return 床位详情，不存在返回null
     */
    public BedDetails findActiveByCustomerId(int customerId);

    /**
     * 按床位ID查询床位详情
     * @param bedId 床位ID
     * @return 床位详情列表
     */
    public List<BedDetails> listByBedId(int bedId);

    /**
     * 新增床位详情（入住登记/调换）
     * @param d 床位详情
     * @return 自增主键
     */
    public int insert(BedDetails d);

    /**
     * 置为失效：结束时间=当天（退住/调换）
     * @param id 床位详情ID
     * @param endDate 结束时间
     * @return 影响行数
     */
    public int expire(int id, LocalDate endDate);
}
