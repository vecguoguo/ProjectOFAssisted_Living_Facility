package com.neusoft.yyzx.web.controller;

import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.entity.Backdown;
import com.neusoft.yyzx.entity.User;
import com.neusoft.yyzx.service.BackdownService;
import com.neusoft.yyzx.service.impl.BackdownServiceImpl;
import com.neusoft.yyzx.web.mvc.Body;
import com.neusoft.yyzx.web.mvc.CurrentUser;
import com.neusoft.yyzx.web.mvc.Http;
import com.neusoft.yyzx.web.mvc.Path;
import com.neusoft.yyzx.web.mvc.Query;
import com.neusoft.yyzx.web.mvc.RestApi;
import com.neusoft.yyzx.web.mvc.Route;
import com.neusoft.yyzx.web.util.ParamUtil;

import java.util.List;
import java.util.Map;

/**
 * 退住接口：退住申请 / 退住审核。
 */
@RestApi("/api/backdowns")
public class BackdownController {

    private final BackdownService backdownService = new BackdownServiceImpl();

    /**
     * 退住记录列表。
     * mine=true 时只查当前管家负责客户的记录（管家端），否则查全部（管理员审核）。
     */
    @Route
    public Result<List<Backdown>> list(@Query(value = "name", required = false) String name,
                                       @Query(value = "mine", required = false, defaultValue = "false") boolean mine,
                                       @CurrentUser User currentUser) {
        if (mine) {
            return Result.success(backdownService.listByCustomerNameAndUserId(name, currentUser.getId()));
        }
        return Result.success(backdownService.listByCustomerName(name));
    }

    /** 提交退住申请 */
    @Route(method = Http.POST)
    public Result<Integer> submit(@Body Backdown backdown) {
        return backdownService.submit(backdown);
    }

    /** 审核退住申请（管理员） */
    @Route(method = Http.PUT, path = "/{id}/audit")
    public Result<Void> audit(@Path("id") int id, @Body Map<String, Object> body,
                              @CurrentUser User currentUser) {
        boolean agree = ParamUtil.getBool(body, "agree", true);
        return backdownService.audit(id, agree, currentUser.getNickname());
    }
}
