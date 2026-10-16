package com.neusoft.yyzx.service.impl;

import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.common.result.ResultCode;
import com.neusoft.yyzx.dao.impl.NurseContentDAOImpl;
import com.neusoft.yyzx.dao.impl.NurseLevelDAOImpl;
import com.neusoft.yyzx.dao.impl.NurseLevelItemDAOImpl;
import com.neusoft.yyzx.db.TxManager;
import com.neusoft.yyzx.entity.NurseContent;
import com.neusoft.yyzx.entity.NurseLevel;
import com.neusoft.yyzx.service.NurseLevelService;

import java.util.List;

/**
 * 护理级别业务实现。
 * 读操作走 TxManager.query，写操作走 TxManager.tx（统一提交/回滚/归还连接）。
 */
public class NurseLevelServiceImpl implements NurseLevelService {

    @Override
    public List<NurseLevel> listLevels(Integer status) {
        return TxManager.query(conn -> {
            NurseLevelDAOImpl dao = new NurseLevelDAOImpl();
            dao.setConnection(conn);
            return dao.listByStatus(status);
        });
    }

    @Override
    public NurseLevel findLevelById(int id) {
        return TxManager.query(conn -> {
            NurseLevelDAOImpl dao = new NurseLevelDAOImpl();
            dao.setConnection(conn);
            return dao.findById(id);
        });
    }

    @Override
    public Result<Integer> addLevel(NurseLevel level) {
        return TxManager.tx(conn -> {
            if (level.getLevelName() == null || level.getLevelName().isBlank()) {
                return Result.fail(ResultCode.PARAM_ERROR);
            }
            NurseLevelDAOImpl dao = new NurseLevelDAOImpl();
            dao.setConnection(conn);
            int id = dao.insert(level);
            return Result.success(id);
        });
    }

    @Override
    public Result<Void> updateLevelStatus(int id, int status) {
        return TxManager.tx(conn -> {
            NurseLevelDAOImpl dao = new NurseLevelDAOImpl();
            dao.setConnection(conn);
            if (dao.findById(id) == null) {
                return Result.fail(ResultCode.NURSE_LEVEL_NOT_FOUND);
            }
            dao.updateStatus(id, status);
            return Result.success();
        });
    }

    @Override
    public List<NurseContent> listItemsInLevel(int levelId) {
        return TxManager.query(conn -> {
            NurseLevelItemDAOImpl dao = new NurseLevelItemDAOImpl();
            dao.setConnection(conn);
            return dao.listItemsByLevelId(levelId);
        });
    }

    @Override
    public List<NurseContent> listItemsToAdd(int levelId) {
        return TxManager.query(conn -> {
            NurseLevelItemDAOImpl dao = new NurseLevelItemDAOImpl();
            dao.setConnection(conn);
            return dao.listItemsNotInLevel(levelId);
        });
    }

    @Override
    public Result<Void> addItemToLevel(int levelId, int itemId) {
        return TxManager.tx(conn -> {
            NurseLevelDAOImpl levelDAO = new NurseLevelDAOImpl();
            NurseContentDAOImpl contentDAO = new NurseContentDAOImpl();
            NurseLevelItemDAOImpl itemDAO = new NurseLevelItemDAOImpl();
            levelDAO.setConnection(conn);
            contentDAO.setConnection(conn);
            itemDAO.setConnection(conn);

            if (levelDAO.findById(levelId) == null) {
                return Result.fail(ResultCode.NURSE_LEVEL_NOT_FOUND);
            }
            NurseContent content = contentDAO.findById(itemId);
            if (content == null) {
                return Result.fail(ResultCode.NURSE_ITEM_NOT_FOUND);
            }
            if (itemDAO.exists(levelId, itemId)) {
                return Result.fail(ResultCode.NURSE_ITEM_EXISTS);
            }
            itemDAO.insert(levelId, itemId);
            return Result.success();
        });
    }

    @Override
    public Result<Void> removeItemFromLevel(int levelId, int itemId) {
        return TxManager.tx(conn -> {
            NurseLevelItemDAOImpl itemDAO = new NurseLevelItemDAOImpl();
            itemDAO.setConnection(conn);
            itemDAO.deleteByLevelAndItem(levelId, itemId);
            return Result.success();
        });
    }
}
