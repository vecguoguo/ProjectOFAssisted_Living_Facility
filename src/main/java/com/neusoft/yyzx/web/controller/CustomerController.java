package com.neusoft.yyzx.web.controller;

import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.common.result.ResultCode;
import com.neusoft.yyzx.entity.Customer;
import com.neusoft.yyzx.entity.User;
import com.neusoft.yyzx.service.CustomerService;
import com.neusoft.yyzx.service.impl.CustomerServiceImpl;
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
 * 客户接口：入住登记 / 客户查询 / 健康管家分配。
 */
@RestApi("/api/customers")
public class CustomerController {

    private final CustomerService customerService = new CustomerServiceImpl();

    /** 全部客户 */
    @Route
    public Result<List<Customer>> listAll() {
        return Result.success(customerService.listAll());
    }

    /** 我（当前管家）负责的客户 */
    @Route(method = Http.GET, path = "/mine")
    public Result<List<Customer>> listMine(@CurrentUser User currentUser) {
        return Result.success(customerService.listByUserId(currentUser.getId()));
    }

    /** 指定健康管家负责的客户（管理员"健康管家管理"页查看某管家名下客户） */
    @Route(method = Http.GET, path = "/by-butler")
    public Result<List<Customer>> listByButler(@Query("butlerId") int butlerId) {
        return Result.success(customerService.listByUserId(butlerId));
    }

    /** 无健康管家的客户（分配管家用） */
    @Route(method = Http.GET, path = "/without-butler")
    public Result<List<Customer>> listWithoutButler(@Query(value = "nameLike", required = false) String nameLike) {
        return Result.success(customerService.listWithoutButler(nameLike));
    }

    /** 客户详情 */
    @Route(method = Http.GET, path = "/{id}")
    public Result<Customer> findById(@Path("id") int id) {
        Customer customer = customerService.findById(id);
        if (customer == null) {
            return Result.fail(ResultCode.CUSTOMER_NOT_FOUND);
        }
        return Result.success(customer);
    }

    /** 入住登记 */
    @Route(method = Http.POST, path = "/checkin")
    public Result<Integer> checkIn(@Body Customer customer) {
        return customerService.checkIn(customer);
    }

    /** 修改客户资料 */
    @Route(method = Http.PUT, path = "/{id}")
    public Result<Void> update(@Path("id") int id, @Body Customer customer) {
        customer.setId(id);
        return customerService.update(customer);
    }

    /** 退住/删除客户 */
    @Route(method = Http.DELETE, path = "/{id}")
    public Result<Void> remove(@Path("id") int id) {
        return customerService.remove(id);
    }

    /** 为该客户分配健康管家 */
    @Route(method = Http.PUT, path = "/{id}/butler")
    public Result<Void> assignButler(@Path("id") int id, @Body Map<String, Object> body) {
        int butlerId = ParamUtil.requireInt(body, "butlerId");
        return customerService.assignButler(id, butlerId);
    }

    /** 移除该客户的健康管家 */
    @Route(method = Http.DELETE, path = "/{id}/butler")
    public Result<Void> removeButler(@Path("id") int id) {
        return customerService.removeButler(id);
    }
}
