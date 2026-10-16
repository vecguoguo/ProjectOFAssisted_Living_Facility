package com.neusoft.yyzx.web.controller;

import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.common.result.ResultCode;
import com.neusoft.yyzx.entity.NurseContent;
import com.neusoft.yyzx.entity.NurseLevel;
import com.neusoft.yyzx.service.NurseLevelService;
import com.neusoft.yyzx.service.impl.NurseLevelServiceImpl;
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
 * 护理级别接口（含级别项目配置）。
 */
@RestApi("/api/nurse-levels")
public class NurseLevelController {

    private final NurseLevelService nurseLevelService = new NurseLevelServiceImpl();

    /** 级别列表 */
    @Route
    public Result<List<NurseLevel>> list(@Query(value = "status", required = false) Integer status) {
        return Result.success(nurseLevelService.listLevels(status));
    }

    /** 级别详情 */
    @Route(method = Http.GET, path = "/{id}")
    public Result<NurseLevel> findById(@Path("id") int id) {
        NurseLevel level = nurseLevelService.findLevelById(id);
        if (level == null) {
            return Result.fail(ResultCode.NURSE_LEVEL_NOT_FOUND);
        }
        return Result.success(level);
    }

    /** 新增级别 */
    @Route(method = Http.POST)
    public Result<Integer> add(@Body NurseLevel level) {
        return nurseLevelService.addLevel(level);
    }

    /** 启用/停用级别 */
    @Route(method = Http.PUT, path = "/{id}/status")
    public Result<Void> updateStatus(@Path("id") int id, @Body Map<String, Object> body) {
        int status = ParamUtil.requireInt(body, "status");
        return nurseLevelService.updateLevelStatus(id, status);
    }

    /** 级别下已配置的项目 */
    @Route(method = Http.GET, path = "/{id}/items")
    public Result<List<NurseContent>> listItemsInLevel(@Path("id") int id) {
        return Result.success(nurseLevelService.listItemsInLevel(id));
    }

    /** 级别下可添加的项目 */
    @Route(method = Http.GET, path = "/{id}/items-to-add")
    public Result<List<NurseContent>> listItemsToAdd(@Path("id") int id) {
        return Result.success(nurseLevelService.listItemsToAdd(id));
    }

    /** 为级别添加项目 */
    @Route(method = Http.POST, path = "/{id}/items")
    public Result<Void> addItem(@Path("id") int id, @Body Map<String, Object> body) {
        int itemId = ParamUtil.requireInt(body, "itemId");
        return nurseLevelService.addItemToLevel(id, itemId);
    }

    /** 从级别移除项目 */
    @Route(method = Http.DELETE, path = "/{id}/items/{itemId}")
    public Result<Void> removeItem(@Path("id") int id, @Path("itemId") int itemId) {
        return nurseLevelService.removeItemFromLevel(id, itemId);
    }
}
