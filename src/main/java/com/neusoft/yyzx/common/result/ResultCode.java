package com.neusoft.yyzx.common.result;

public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "系统异常"),
    PARAM_ERROR(400, "参数校验失败"),
    NOT_LOGIN(401, "未登录，请先登录"),

    LOGIN_ERROR(1001, "用户名或密码错误"),
    USERNAME_EXISTS(1002, "账号已存在"),
    USER_NOT_FOUND(1003, "用户不存在"),
    USER_NOT_ADMIN(1004, "无权限，仅管理员可操作"),
    USER_DELETE_SELF(1005, "不能删除当前登录用户"),

    BED_NOT_FREE(2001, "床位非空闲状态"),
    BED_SWAP_INVALID(2002, "目标床位不可用"),
    BED_NOT_FOUND(2003, "床位不存在"),
    BED_OCCUPIED_UNDELETABLE(2004, "床位已被占用，不可删除"),
    BED_NO_EXISTS(2005, "该房间下床位号已存在"),

    CONTRACT_DATE_INVALID(3001, "合同到期时间必须晚于入住时间"),

    CUSTOMER_NOT_FOUND(3002, "客户不存在"),
    AUDIT_STATUS_INVALID(3101, "审批状态不合法"),
    OUTWARD_NOT_FOUND(3201, "外出登记不存在"),
    BACKDOWN_NOT_FOUND(3301, "退住登记不存在"),

    /* 护理模块 4001~4099 */
    NURSE_LEVEL_EXISTS(4001, "护理级别已存在"),
    NURSE_ITEM_EXISTS(4002, "护理项目已存在"),
    NURSE_LEVEL_NOT_FOUND(4003, "护理级别不存在"),
    NURSE_ITEM_NOT_FOUND(4004, "护理项目不存在"),
    NURSE_LEVEL_ALREADY_SET(4005, "客户已有护理级别，请先移除"),
    NURSE_LEVEL_NOT_SET(4006, "客户没有护理级别"),
    NURSE_NUMBER_NOT_ENOUGH(4007, "护理数量不足"),
    CUSTOMER_NOT_FOUND_IN_NURSE(4008, "客户不存在"),
    NURSE_ITEM_DISABLED(4009, "护理项目已停用"),

    BUTLER_NOT_FOUND(4101, "健康管家不存在"),
    CUSTOMER_ALREADY_HAS_BUTLER(4102, "该客户已有健康管家，请先移除"),
    CUSTOMER_NO_BUTLER(4103, "该客户没有健康管家"),
    NURSE_ITEM_ALREADY_PURCHASED(4104, "该客户已购买此护理项目"),
    CUSTOMER_NURSE_ITEM_NOT_FOUND(4105, "客户护理项目不存在");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}