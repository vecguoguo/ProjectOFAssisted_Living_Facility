package com.neusoft.yyzx.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * 控制台输入/输出工具
 */
public final class ConsoleUtil {

    private static final Scanner SCANNER = new Scanner(System.in, "UTF-8");

    private ConsoleUtil() {
    }

    /** 打印菜单 */
    public static void menu(String title, String... items) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
        for (int i = 0; i < items.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, items[i]);
        }
        System.out.println("  0. 返回上一级");
        System.out.print("请选择: ");
    }

    /** 读取菜单选择 */
    public static int choice() {
        String line = SCANNER.nextLine().trim();
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /** 读取非空字符串 */
    public static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = SCANNER.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("  >> 输入不能为空，请重新输入");
        }
    }

    /** 读取可空字符串（用于模糊查询，回车表示不限制） */
    public static String readOptional(String prompt) {
        System.out.print(prompt);
        return SCANNER.nextLine().trim();
    }

    /** 读取整数 */
    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(SCANNER.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  >> 请输入合法整数");
            }
        }
    }

    /** 读取可选整数（直接回车返回 null，非整数提示重输，用于条件查询） */
    public static Integer readOptionalInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = SCANNER.nextLine().trim();
            if (value.isEmpty()) {
                return null;
            }
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println("  >> 请输入合法整数或直接回车跳过");
            }
        }
    }

    /** 读取日期 yyyy-MM-dd（直接回车取当天） */
    public static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = SCANNER.nextLine().trim();
            if (value.isEmpty()) {
                return LocalDate.now();
            }
            try {
                return LocalDate.parse(value);
            } catch (DateTimeParseException e) {
                System.out.println("  >> 日期格式应为 yyyy-MM-dd");
            }
        }
    }

    /** 读取日期时间 yyyy-MM-dd HH:mm:ss，直接回车则取当前系统时间 */
    public static LocalDateTime readDateTime(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = SCANNER.nextLine().trim();
            if (value.isEmpty()) {
                return LocalDateTime.now();
            }
            try {
                return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (DateTimeParseException e) {
                System.out.println("  >> 时间格式应为 yyyy-MM-dd HH:mm:ss（直接回车取当前时间）");
            }
        }
    }

    /** 等待回车 */
    public static void pause() {
        System.out.print("按回车继续...");
        SCANNER.nextLine();
    }
}