package com.neusoft.yyzx.web.controller;

import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.common.result.ResultCode;
import com.neusoft.yyzx.entity.Bed;
import com.neusoft.yyzx.service.BedService;
import com.neusoft.yyzx.service.impl.BedServiceImpl;
import com.neusoft.yyzx.web.mvc.Body;
import com.neusoft.yyzx.web.mvc.Http;
import com.neusoft.yyzx.web.mvc.Path;
import com.neusoft.yyzx.web.mvc.Query;
import com.neusoft.yyzx.web.mvc.RestApi;
import com.neusoft.yyzx.web.mvc.Route;
import com.neusoft.yyzx.web.util.ParamUtil;

import java.util.List;
import java.util.Map;

/**
 * 床位接口：床位示意图 / 床位管理 / 床位调换。
 */
@RestApi("/api/beds")
public class BedController {

    private final BedService bedService = new BedServiceImpl();

    /** 全部床位（床位示意图） */
    @Route
    public Result<List<Bed>> listAll() {
        return Result.success(bedService.listAllBeds());
    }

    /** 指定房间的空闲床位（入住登记、床位调换用） */
    @Route(method = Http.GET, path = "/free")
    public Result<List<Bed>> listFree(@Query("roomNo") int roomNo) {
        return Result.success(bedService.listFreeBeds(roomNo));
    }

    /** 条件查询（床位管理） */
    @Route(method = Http.GET, path = "/query")
    public Result<List<Bed>> query(@Query(value = "roomNo", required = false) Integer roomNo,
                                   @Query(value = "bedNo", required = false) String bedNo,
                                   @Query(value = "status", required = false) Integer status) {
        return Result.success(bedService.listByCondition(roomNo, bedNo, status));
    }

    /** 床位详情 */
    @Route(method = Http.GET, path = "/{id}")
    public Result<Bed> findById(@Path("id") int id) {
        Bed bed = bedService.findById(id);
        if (bed == null) {
            return Result.fail(ResultCode.BED_NOT_FOUND);
        }
        return Result.success(bed);
    }

    /** 床位调换 */
    @Route(method = Http.PUT, path = "/swap")
    public Result<Void> swap(@Body Map<String, Object> body) {
        int customerId = ParamUtil.requireInt(body, "customerId");
        int newBedId = ParamUtil.requireInt(body, "newBedId");
        return bedService.swapBed(customerId, newBedId);
    }

    /** 新增床位 */
    @Route(method = Http.POST)
    public Result<Integer> add(@Body Bed bed) {
        return bedService.addBed(bed);
    }

    /** 修改床位 */
    @Route(method = Http.PUT, path = "/{id}")
    public Result<Void> update(@Path("id") int id, @Body Bed bed) {
        bed.setId(id);
        return bedService.updateBed(bed);
    }

    /** 删除床位（逻辑删除） */
    @Route(method = Http.DELETE, path = "/{id}")
    public Result<Void> delete(@Path("id") int id) {
        return bedService.deleteBed(id);
    }
}
