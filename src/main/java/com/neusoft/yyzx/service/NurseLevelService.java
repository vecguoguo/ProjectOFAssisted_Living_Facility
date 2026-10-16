package com.neusoft.yyzx.service;

import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.entity.NurseContent;
import com.neusoft.yyzx.entity.NurseLevel;

import java.util.List;

/**
 * 护理级别业务接口（管理员侧：级别维护 + 级别项目配置）
 */
public interface NurseLevelService {

    /** 按状态查询护理级别，status 传 null 查全部 */
    public List<NurseLevel> listLevels(Integer status);

    /** 根据ID查询护理级别 */
    public NurseLevel findLevelById(int id);

    /** 新增护理级别（名称 + 状态） */
    public Result<Integer> addLevel(NurseLevel level);

    /** 修改护理级别状态（只能改状态） */
    public Result<Void> updateLevelStatus(int id, int status);

    /** 查看某级别已配置的护理项目 */
    public List<NurseContent> listItemsInLevel(int levelId);

    /** 查看可添加进某级别的项目（启用且不在该级别下） */
    public List<NurseContent> listItemsToAdd(int levelId);

    /** 为级别添加项目（重复添加被拒） */
    public Result<Void> addItemToLevel(int levelId, int itemId);

    /** 从级别移除项目 */
    public Result<Void> removeItemFromLevel(int levelId, int itemId);
}
