package com.neusoft.yyzx.dao;

import com.neusoft.yyzx.entity.NurseRecord;

import java.util.List;

/**
 * nurserecord 表数据访问接口
 */
public interface NurseRecordDAO {
    /**
     * 查询某客户的护理记录（联 nursecontent 带项目名，按护理时间倒序）
     * @param customerId 客户ID
     * @return 记录列表
     */
    public List<NurseRecord> listByCustomerId(int customerId);

    /**
     * 新增护理记录
     * @param record 记录对象
     * @return 自增主键
     */
    public int insert(NurseRecord record);

    /**
     * 逻辑删除护理记录
     * @param id 记录ID
     * @return 影响行数
     */
    public int logicDelete(int id);
}
