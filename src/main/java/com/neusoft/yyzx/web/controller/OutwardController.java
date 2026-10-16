package com.neusoft.yyzx.web.controller;

import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.entity.Outward;
import com.neusoft.yyzx.entity.User;
import com.neusoft.yyzx.service.OutwardService;
import com.neusoft.yyzx.service.impl.OutwardServiceImpl;
import com.neusoft.yyzx.web.mvc.Body;
import com.neusoft.yyzx.web.mvc.CurrentUser;
import com.neusoft.yyzx.web.mvc.Http;
import com.neusoft.yyzx.web.mvc.Path;
import com.neusoft.yyzx.web.mvc.Query;
import com.neusoft.yyzx.web.mvc.RestApi;
import com.neusoft.yyzx.web.mvc.Route;
import com.neusoft.yyzx.web.util.ParamUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 外出接口：外出申请 / 外出审核 / 回院登记。
 */
@RestApi("/api/outwards")
public class OutwardController {

    private final OutwardService outwardService = new OutwardServiceImpl();

    /**
     * 外出记录列表。
     * mine=true 时只查当前管家负责客户的记录（管家端），否则查全部（管理员审核）。
     */
    @Route
    public Result<List<Outward>> list(@Query(value = "name", required = false) String name,
                                      @Query(value = "mine", required = false, defaultValue = "false") boolean mine,
                                      @CurrentUser User currentUser) {
        if (mine) {
            return Result.success(outwardService.listByCustomerNameAndUserId(name, currentUser.getId()));
        }
        return Result.success(outwardService.listByCustomerName(name));
    }

    /** 提交外出申请 */
    @Route(method = Http.POST)
    public Result<Integer> submit(@Body Outward outward) {
        return outwardService.submit(outward);
    }

    /** 审核外出申请（管理员） */
    @Route(method = Http.PUT, path = "/{id}/audit")
    public Result<Void> audit(@Path("id") int id, @Body Map<String, Object> body,
                              @CurrentUser User currentUser) {
        boolean agree = ParamUtil.getBool(body, "agree", true);
        return outwardService.audit(id, agree, currentUser.getNickname());
    }

    /** 登记实际回院时间（管家） */
    @Route(method = Http.PUT, path = "/{id}/return")
    public Result<Void> registerReturn(@Path("id") int id, @Body Map<String, Object> body) {
        LocalDate actualReturnTime = ParamUtil.getDate(body, "actualReturnTime");
        if (actualReturnTime == null) {
            actualReturnTime = LocalDate.now();
        }
        return outwardService.registerReturn(id, actualReturnTime);
    }
}
