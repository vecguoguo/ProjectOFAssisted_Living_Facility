package com.neusoft.yyzx.service.impl;

import com.neusoft.yyzx.common.constant.NurseStatusConstant;
import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.common.result.ResultCode;
import com.neusoft.yyzx.dao.impl.NurseContentDAOImpl;
import com.neusoft.yyzx.dao.impl.NurseLevelItemDAOImpl;
import com.neusoft.yyzx.db.TxManager;
import com.neusoft.yyzx.entity.NurseContent;
import com.neusoft.yyzx.service.NurseContentService;

import java.util.List;

/**
 * 护理项目业务实现。
 * 停用/逻辑删除仅级联清理 nurselevelitem，绝不动 customernurseitem（需求附录强约束）。
 */
public class NurseContentServiceImpl implements NurseContentService {

    @Override
    public List<NurseContent> listItems(Integer status, String nameLike) {
        return TxManager.query(conn -> {
            NurseContentDAOImpl dao = new NurseContentDAOImpl();
            dao.setConnection(conn);
            return dao.listByStatusAndName(status, nameLike);
        });
    }

    @Override
    public Result<Integer> addItem(NurseContent content) {
        return TxManager.tx(conn -> {
            if (content.getNursingName() == null || content.getNursingName().isBlank()
                    || content.getSerialNumber() == null || content.getSerialNumber().isBlank()) {
                return Result.fail(ResultCode.PARAM_ERROR);
            }
            NurseContentDAOImpl dao = new NurseContentDAOImpl();
            dao.setConnection(conn);
            int id = dao.insert(content);
            return Result.success(id);
        });
    }

    @Override
    public Result<Void> updateItem(NurseContent content) {
        return TxManager.tx(conn -> {
            NurseContentDAOImpl contentDAO = new NurseContentDAOImpl();
            NurseLevelItemDAOImpl levelItemDAO = new NurseLevelItemDAOImpl();
            contentDAO.setConnection(conn);
            levelItemDAO.setConnection(conn);

            NurseContent exist = contentDAO.findById(content.getId());
            if (exist == null) {
                return Result.fail(ResultCode.NURSE_ITEM_NOT_FOUND);
            }
            contentDAO.update(content);
            // 状态改为停用：级联剔除各级别中的该项目记录
            if (NurseStatusConstant.DISABLED.equals(content.getStatus())) {
                levelItemDAO.deleteByItemId(content.getId());
            }
            return Result.success();
        });
    }

    @Override
    public Result<Void> logicDeleteItem(int id) {
        return TxManager.tx(conn -> {
            NurseContentDAOImpl contentDAO = new NurseContentDAOImpl();
            NurseLevelItemDAOImpl levelItemDAO = new NurseLevelItemDAOImpl();
            contentDAO.setConnection(conn);
            levelItemDAO.setConnection(conn);

            if (contentDAO.findById(id) == null) {
                return Result.fail(ResultCode.NURSE_ITEM_NOT_FOUND);
            }
            contentDAO.logicDelete(id);
            // 删除前若在 nurselevelitem 中存在，一并剔除
            levelItemDAO.deleteByItemId(id);
            return Result.success();
        });
    }
}
