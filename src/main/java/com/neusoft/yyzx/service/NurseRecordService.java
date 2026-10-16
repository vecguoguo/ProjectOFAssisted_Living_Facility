package com.neusoft.yyzx.service;

import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.entity.NurseRecord;

import java.util.List;

/**
 * 护理记录业务接口（管理员/管家只读 + 逻辑删除）
 */
public interface NurseRecordService {

    /** 查询某客户的护理记录（按护理时间倒序，含项目名） */
    public List<NurseRecord> listByCustomerId(int customerId);

    /** 逻辑删除护理记录 */
    public Result<Void> logicDelete(int id);
}
