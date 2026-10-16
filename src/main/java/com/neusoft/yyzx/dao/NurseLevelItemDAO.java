package com.neusoft.yyzx.dao;

import com.neusoft.yyzx.entity.NurseContent;

import java.util.List;

/**
 * nurselevelitem 表数据访问接口（级别↔项目关联，物理删除，无 is_deleted）
 */
public interface NurseLevelItemDAO {
    /**
     * 查询某级别已配置的护理项目明细（联 nursecontent）
     * @param levelId 级别ID
     * @return 项目列表
     */
    public List<NurseContent> listItemsByLevelId(int levelId);

    /**
     * 查询启用状态、且不在该级别下的护理项目（供添加）
     * @param levelId 级别ID
     * @return 可添加的项目列表
     */
    public List<NurseContent> listItemsNotInLevel(int levelId);

    /**
     * 判断某级别下是否已配置某项目
     * @param levelId 级别ID
     * @param itemId 项目ID
     * @return true 已存在
     */
    public boolean exists(int levelId, int itemId);

    /**
     * 为级别添加项目
     * @param levelId 级别ID
     * @param itemId 项目ID
     * @return 影响行数
     */
    public int insert(int levelId, int itemId);

    /**
     * 从级别移除某项目（物理删除）
     * @param levelId 级别ID
     * @param itemId 项目ID
     * @return 影响行数
     */
    public int deleteByLevelAndItem(int levelId, int itemId);

    /**
     * 项目停用/删除时级联剔除所有级别中的该项目记录（物理删除）
     * @param itemId 项目ID
     * @return 影响行数
     */
    public int deleteByItemId(int itemId);
}
