package com.neusoft.yyzx.dao;

import com.neusoft.yyzx.entity.NurseContent;

import java.util.List;

/**
 * nursecontent 表数据访问接口
 */
public interface NurseContentDAO {
    /**
     * 组合查询：状态 + 名称模糊（任一为 null/空则不拼该条件）
     * @param status 1-启用 2-停用，null 不限
     * @param nameLike 名称模糊关键字，null/空 不限
     * @return 项目列表
     */
    public List<NurseContent> listByStatusAndName(Integer status, String nameLike);

    /**
     * 根据ID查询护理项目
     * @param id 项目ID
     * @return 项目对象，不存在返回null
     */
    public NurseContent findById(int id);

    /**
     * 新增护理项目
     * @param content 项目对象
     * @return 自增主键
     */
    public int insert(NurseContent content);

    /**
     * 修改护理项目（全字段）
     * @param content 项目对象
     * @return 影响行数
     */
    public int update(NurseContent content);

    /**
     * 逻辑删除护理项目
     * @param id 项目ID
     * @return 影响行数
     */
    public int logicDelete(int id);
}
