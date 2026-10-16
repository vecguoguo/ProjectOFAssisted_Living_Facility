package com.neusoft.yyzx.dao.impl;

import com.neusoft.yyzx.dao.NurseLevelItemDAO;
import com.neusoft.yyzx.entity.NurseContent;
import com.neusoft.yyzx.mapping.EntityMapping;
import com.neusoft.yyzx.mapping.MappingFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * nurselevelitem 表数据访问实现（关联表，物理删除）
 */
public class NurseLevelItemDAOImpl extends BaseDao implements NurseLevelItemDAO {

    @Override
    public List<NurseContent> listItemsByLevelId(int levelId) {
        String SQL = "select nc.* from nurselevelitem nli"
                + " join nursecontent nc on nli.item_id = nc.id"
                + " where nli.level_id = ? and nc.is_deleted = 0 order by nc.id";
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.NURSE_CONTENT_MAPPING);
        return toContentList(jt.query(SQL, mapping, levelId));
    }

    @Override
    public List<NurseContent> listItemsNotInLevel(int levelId) {
        String SQL = "select nc.* from nursecontent nc"
                + " where nc.is_deleted = 0 and nc.status = 1"
                + " and nc.id not in (select item_id from nurselevelitem where level_id = ?)"
                + " order by nc.id";
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.NURSE_CONTENT_MAPPING);
        return toContentList(jt.query(SQL, mapping, levelId));
    }

    @Override
    public boolean exists(int levelId, int itemId) {
        String SQL = "select * from nurselevelitem where level_id = ? and item_id = ?";
        EntityMapping mapping = mappingFactory.getMapping(MappingFactory.NURSE_LEVEL_ITEM_MAPPING);
        return jt.queryOne(SQL, mapping, levelId, itemId) != null;
    }

    @Override
    public int insert(int levelId, int itemId) {
        String SQL = "insert into nurselevelitem(level_id, item_id) values(?,?)";
        return jt.update(SQL, levelId, itemId);
    }

    @Override
    public int deleteByLevelAndItem(int levelId, int itemId) {
        String SQL = "delete from nurselevelitem where level_id = ? and item_id = ?";
        return jt.update(SQL, levelId, itemId);
    }

    @Override
    public int deleteByItemId(int itemId) {
        String SQL = "delete from nurselevelitem where item_id = ?";
        return jt.update(SQL, itemId);
    }

    private List<NurseContent> toContentList(List<Object> list) {
        List<NurseContent> contents = new ArrayList<>();
        for (Object o : list) {
            contents.add((NurseContent) o);
        }
        return contents;
    }
}
