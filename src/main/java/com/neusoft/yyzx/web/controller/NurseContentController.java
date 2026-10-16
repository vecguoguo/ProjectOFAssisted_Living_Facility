package com.neusoft.yyzx.web.controller;

import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.entity.NurseContent;
import com.neusoft.yyzx.service.NurseContentService;
import com.neusoft.yyzx.service.impl.NurseContentServiceImpl;
import com.neusoft.yyzx.web.mvc.Body;
import com.neusoft.yyzx.web.mvc.Http;
import com.neusoft.yyzx.web.mvc.Path;
import com.neusoft.yyzx.web.mvc.Query;
import com.neusoft.yyzx.web.mvc.RestApi;
import com.neusoft.yyzx.web.mvc.Route;

import java.util.List;

/**
 * 护理项目接口。
 */
@RestApi("/api/nurse-contents")
public class NurseContentController {

    private final NurseContentService nurseContentService = new NurseContentServiceImpl();

    /** 项目列表（可按状态、名称查询） */
    @Route
    public Result<List<NurseContent>> list(@Query(value = "status", required = false) Integer status,
                                           @Query(value = "nameLike", required = false) String nameLike) {
        return Result.success(nurseContentService.listItems(status, nameLike));
    }

    /** 新增项目 */
    @Route(method = Http.POST)
    public Result<Integer> add(@Body NurseContent content) {
        return nurseContentService.addItem(content);
    }

    /** 修改项目 */
    @Route(method = Http.PUT, path = "/{id}")
    public Result<Void> update(@Path("id") int id, @Body NurseContent content) {
        content.setId(id);
        return nurseContentService.updateItem(content);
    }

    /** 删除项目（逻辑删除） */
    @Route(method = Http.DELETE, path = "/{id}")
    public Result<Void> delete(@Path("id") int id) {
        return nurseContentService.logicDeleteItem(id);
    }
}
