package com.neusoft.yyzx.common.utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/**
 * 日期工具
 */
public class DateUtil {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** 根据出生日期计算年龄（入住登记时自动计算） */
    public static Integer calcAge(LocalDate birthday) {
        if (birthday == null) {
            return null;
        }
        return Period.between(birthday, LocalDate.now()).getYears();
    }

    public static String format(LocalDate date) {
        return date == null ? null : date.format(DATE_FORMATTER);
    }

    private DateUtil() {
    }
}
