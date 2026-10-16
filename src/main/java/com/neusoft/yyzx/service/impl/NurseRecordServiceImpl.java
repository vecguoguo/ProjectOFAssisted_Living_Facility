package com.neusoft.yyzx.service.impl;

import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.dao.impl.NurseRecordDAOImpl;
import com.neusoft.yyzx.db.TxManager;
import com.neusoft.yyzx.entity.NurseRecord;
import com.neusoft.yyzx.service.NurseRecordService;

import java.util.List;

/**
 * 护理记录业务实现（管理员/管家只读 + 逻辑删除）
 */
public class NurseRecordServiceImpl implements NurseRecordService {

    @Override
    public List<NurseRecord> listByCustomerId(int customerId) {
        return TxManager.query(conn -> {
            NurseRecordDAOImpl dao = new NurseRecordDAOImpl();
            dao.setConnection(conn);
            return dao.listByCustomerId(customerId);
        });
    }

    @Override
    public Result<Void> logicDelete(int id) {
        return TxManager.tx(conn -> {
            NurseRecordDAOImpl dao = new NurseRecordDAOImpl();
            dao.setConnection(conn);
            dao.logicDelete(id);
            return Result.success();
        });
    }
}
