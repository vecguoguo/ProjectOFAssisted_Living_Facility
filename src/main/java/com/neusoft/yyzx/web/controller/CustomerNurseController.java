package com.neusoft.yyzx.web.controller;

import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.entity.Customer;
import com.neusoft.yyzx.entity.CustomerNurseItem;
import com.neusoft.yyzx.entity.NurseContent;
import com.neusoft.yyzx.entity.User;
import com.neusoft.yyzx.service.CustomerNurseService;
import com.neusoft.yyzx.service.impl.CustomerNurseServiceImpl;
import com.neusoft.yyzx.web.mvc.Body;
import com.neusoft.yyzx.web.mvc.CurrentUser;
import com.neusoft.yyzx.web.mvc.Http;
import com.neusoft.yyzx.web.mvc.Path;
import com.neusoft.yyzx.web.mvc.Query;
import com.neusoft.yyzx.web.mvc.RestApi;
import com.neusoft.yyzx.web.mvc.Route;
import com.neusoft.yyzx.web.util.ParamUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 客户护理接口：护理级别设置 / 购买护理项目 / 日常护理 / 服务关注。
 */
@RestApi("/api/customer-nurse")
public class CustomerNurseController {

    private final CustomerNurseService customerNurseService = new CustomerNurseServiceImpl();

    /** 全部客户（服务关注、客户护理设置用） */
    @Route(method = Http.GET, path = "/customers")
    public Result<List<Customer>> listCustomers(@Query(value = "nameLike", required = false) String nameLike) {
        return Result.success(customerNurseService.listCustomers(nameLike));
    }

    /** 我（当前管家）负责的客户（日常护理用） */
    @Route(method = Http.GET, path = "/my-customers")
    public Result<List<Customer>> listMyCustomers(@CurrentUser User currentUser,
                                                  @Query(value = "nameLike", required = false) String nameLike) {
        return Result.success(customerNurseService.listMyCustomers(currentUser.getId(), nameLike));
    }

    /** 健康管家列表（分配管家用） */
    @Route(method = Http.GET, path = "/butlers")
    public Result<List<User>> listButlers(@Query(value = "nameLike", required = false) String nameLike) {
        return Result.success(customerNurseService.listButlers(nameLike));
    }

    /** 设置客户护理级别 */
    @Route(method = Http.POST, path = "/level")
    public Result<Void> setLevel(@Body Map<String, Object> body) {
        int customerId = ParamUtil.requireInt(body, "customerId");
        int levelId = ParamUtil.requireInt(body, "levelId");
        return customerNurseService.setLevel(customerId, levelId);
    }

    /** 移除客户护理级别 */
    @Route(method = Http.DELETE, path = "/level/{customerId}")
    public Result<Void> removeLevel(@Path("customerId") int customerId) {
        return customerNurseService.removeLevel(customerId);
    }

    /** 客户的护理项目清单（含剩余次数） */
    @Route(method = Http.GET, path = "/items/{customerId}")
    public Result<List<CustomerNurseItem>> listCustomerItems(@Path("customerId") int customerId) {
        return Result.success(customerNurseService.listCustomerItems(customerId));
    }

    /** 客户可购买（尚未购买）的护理项目 */
    @Route(method = Http.GET, path = "/available-items")
    public Result<List<NurseContent>> listAvailableItems(@Query("customerId") int customerId,
                                                         @Query(value = "nameLike", required = false) String nameLike) {
        return Result.success(customerNurseService.listAvailableItems(customerId, nameLike));
    }

    /** 购买护理项目 */
    @Route(method = Http.POST, path = "/purchase")
    public Result<Void> purchase(@Body Map<String, Object> body) {
        int customerId = ParamUtil.requireInt(body, "customerId");
        int itemId = ParamUtil.requireInt(body, "itemId");
        int number = ParamUtil.requireInt(body, "number");
        LocalDate maturityTime = ParamUtil.getDate(body, "maturityTime");
        return customerNurseService.purchaseItem(customerId, itemId, number, maturityTime);
    }

    /** 续费护理项目 */
    @Route(method = Http.PUT, path = "/item/renew")
    public Result<Void> renew(@Body Map<String, Object> body) {
        int itemId = ParamUtil.requireInt(body, "customerNurseItemId");
        int addNumber = ParamUtil.requireInt(body, "addNumber");
        LocalDate newMaturityTime = ParamUtil.getDate(body, "newMaturityTime");
        return customerNurseService.renewItem(itemId, addNumber, newMaturityTime);
    }

    /** 移除客户护理项目 */
    @Route(method = Http.DELETE, path = "/item/{customerNurseItemId}")
    public Result<Void> removeItem(@Path("customerNurseItemId") int customerNurseItemId) {
        return customerNurseService.removeItem(customerNurseItemId);
    }

    /** 日常护理：扣减次数 + 生成护理记录（同一事务） */
    @Route(method = Http.POST, path = "/daily")
    public Result<Integer> doDailyNurse(@CurrentUser User currentUser, @Body Map<String, Object> body) {
        int customerNurseItemId = ParamUtil.requireInt(body, "customerNurseItemId");
        int nursingCount = ParamUtil.getInt(body, "nursingCount", 1);
        String nursingContent = ParamUtil.getStr(body, "nursingContent");
        LocalDateTime nursingTime = ParamUtil.getDateTime(body, "nursingTime");
        if (nursingTime == null) {
            nursingTime = LocalDateTime.now();
        }
        // 登记人一律取当前登录用户（由 token 解析），不再依赖请求体传 userId，
        // 避免前端漏传 userId 时抛“缺少必要参数”导致登记护理无法运行。
        return customerNurseService.doDailyNurse(customerNurseItemId, currentUser.getId(),
                nursingTime, nursingContent, nursingCount);
    }
}
