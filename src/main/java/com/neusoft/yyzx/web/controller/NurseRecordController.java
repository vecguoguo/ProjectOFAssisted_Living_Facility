package com.neusoft.yyzx.web.controller;

import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.entity.NurseRecord;
import com.neusoft.yyzx.service.NurseRecordService;
import com.neusoft.yyzx.service.impl.NurseRecordServiceImpl;
import com.neusoft.yyzx.web.mvc.Http;
import com.neusoft.yyzx.web.mvc.Path;
import com.neusoft.yyzx.web.mvc.Query;
import com.neusoft.yyzx.web.mvc.RestApi;
import com.neusoft.yyzx.web.mvc.Route;

import java.util.List;

/**
 * 护理记录接口。
 */
@RestApi("/api/nurse-records")
public class NurseRecordController {

    private final NurseRecordService nurseRecordService = new NurseRecordServiceImpl();

    /** 某客户的护理记录 */
    @Route
    public Result<List<NurseRecord>> listByCustomer(@Query("customerId") int customerId) {
        return Result.success(nurseRecordService.listByCustomerId(customerId));
    }

    /** 删除护理记录（逻辑删除） */
    @Route(method = Http.DELETE, path = "/{id}")
    public Result<Void> delete(@Path("id") int id) {
        return nurseRecordService.logicDelete(id);
    }
}
