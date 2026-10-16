package com.neusoft.yyzx.service;

import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.entity.NurseContent;

import java.util.List;

/**
 * 护理项目业务接口（管理员侧）
 */
public interface NurseContentService {

    /** 组合查询：状态 + 名称模糊（null/空 表示该条件不限） */
    public List<NurseContent> listItems(Integer status, String nameLike);

    /** 新增护理项目 */
    public Result<Integer> addItem(NurseContent content);

    /**
     * 修改护理项目。
     * 若状态改为停用，级联剔除 nurselevelitem 中该项目记录（不影响 customernurseitem）。
     */
    public Result<Void> updateItem(NurseContent content);

    /**
     * 逻辑删除护理项目。
     * 删除前若在 nurselevelitem 中存在，一并剔除（不影响 customernurseitem）。
     */
    public Result<Void> logicDeleteItem(int id);
}
