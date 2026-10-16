package com.neusoft.yyzx.dao;

import com.neusoft.yyzx.entity.NurseLevel;

import java.util.List;

/**
 * nurselevel 表数据访问接口
 */
public interface NurseLevelDAO {
    /**
     * 按状态查询护理级别（status 传 null 查全部）
     * @param status 1-启用 2-停用，null 不限
     * @return 级别列表
     */
    public List<NurseLevel> listByStatus(Integer status);

    /**
     * 根据ID查询护理级别
     * @param id 级别ID
     * @return 级别对象，不存在返回null
     */
    public NurseLevel findById(int id);

    /**
     * 新增护理级别
     * @param level 级别对象
     * @return 自增主键
     */
    public int insert(NurseLevel level);

    /**
     * 修改护理级别状态（只能改状态）
     * @param id 级别ID
     * @param status 目标状态
     * @return 影响行数
     */
    public int updateStatus(int id, int status);
}
