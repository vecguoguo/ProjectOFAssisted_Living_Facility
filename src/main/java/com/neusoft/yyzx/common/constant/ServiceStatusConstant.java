package com.neusoft.yyzx.common.constant;

import java.time.LocalDate;

/**
 * 客户服务状态常量（健康管家日常护理列表展示用）。
 * 依据需求 5.2 推导：到期、欠费、数量正常、未到期。
 * 判定算法集中于此，后续需求方若调整定义，只改本类的 determine 方法一处。
 */
public class ServiceStatusConstant {

    /** 未到期（到期日不早于今天，且数量>0 的正常项，语义等同"数量正常"） */
    public static final String NOT_DUE = "未到期";

    /** 数量正常 */
    public static final String NORMAL = "数量正常";

    /** 欠费（数量已用完但服务未到期） */
    public static final String ARREARS = "欠费";

    /** 到期（服务到期日期早于今天） */
    public static final String EXPIRED = "到期";

    /**
     * 依据 5.2 推导的状态判定：
     * maturity_time < 今天 -> 到期；
     * 否则 nurse_number <= 0 -> 欠费；
     * 否则 -> 数量正常（未到期的正常项）。
     *
     * @param maturityTime 服务到期日期
     * @param nurseNumber  剩余护理数量
     * @return 状态文案
     */
    public static String determine(LocalDate maturityTime, Integer nurseNumber) {
        LocalDate today = LocalDate.now();
        if (maturityTime != null && maturityTime.isBefore(today)) {
            return EXPIRED;
        }
        if (nurseNumber == null || nurseNumber <= 0) {
            return ARREARS;
        }
        return NORMAL;
    }

    private ServiceStatusConstant() {
    }
}
