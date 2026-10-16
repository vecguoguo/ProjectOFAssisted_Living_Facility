-- ===================================================================
-- 东软颐养中心（yyzx）数据库建表脚本
-- 依据：project_guide/yyzx数据字典.xls （共 18 张表）
-- 方言：MySQL 8.0 / InnoDB / utf8mb4
-- ===================================================================

CREATE DATABASE IF NOT EXISTS `yyzx`
    DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `yyzx`;

SET NAMES utf8mb4;

DROP TABLE IF EXISTS `meal`;
DROP TABLE IF EXISTS `customerpreference`;
DROP TABLE IF EXISTS `food`;
DROP TABLE IF EXISTS `backdown`;
DROP TABLE IF EXISTS `outward`;
DROP TABLE IF EXISTS `customernurseitem`;
DROP TABLE IF EXISTS `nurserecord`;
DROP TABLE IF EXISTS `nurselevelitem`;
DROP TABLE IF EXISTS `nurselevel`;
DROP TABLE IF EXISTS `nursecontent`;
DROP TABLE IF EXISTS `customer`;
DROP TABLE IF EXISTS `beddetails`;
DROP TABLE IF EXISTS `bed`;
DROP TABLE IF EXISTS `room`;
DROP TABLE IF EXISTS `rolemenu`;
DROP TABLE IF EXISTS `menu`;
DROP TABLE IF EXISTS `role`;
DROP TABLE IF EXISTS `user`;

-- -------------------------------------------------------------------
-- 1.0 user 用户表：存储用户信息
-- -------------------------------------------------------------------
CREATE TABLE `user` (
    `id`           int          NOT NULL AUTO_INCREMENT COMMENT '主键',
    `nickname`     varchar(20)  NOT NULL COMMENT '真实姓名',
    `username`     varchar(20)  NOT NULL COMMENT '系统账号',
    `password`     varchar(255) NOT NULL COMMENT '登录密码',
    `sex`          int          NOT NULL COMMENT '性别：0-女，1-男',
    `email`        varchar(254) NOT NULL COMMENT '邮箱',
    `phone_number` varchar(20)  NOT NULL COMMENT '电话号码',
    `role_id`      int          NOT NULL COMMENT '系统角色编号：1-管理员，2-生活管家',
    `create_time`  datetime     NOT NULL COMMENT '创建时间',
    `create_by`    int          NOT NULL COMMENT '创建人',
    `update_time`  datetime     NOT NULL COMMENT '更新时间',
    `update_by`    int          NOT NULL COMMENT '更新人',
    `is_deleted`   int          NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-显示，1-隐藏',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_username` (`username`),
    KEY `idx_user_role_id` (`role_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT '用户表';

-- -------------------------------------------------------------------
-- 5.0 role 角色表：存储角色信息
-- -------------------------------------------------------------------
CREATE TABLE `role` (
    `id`          int         NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        varchar(50) NOT NULL COMMENT '系统角色名',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    `update_by`   int         NOT NULL COMMENT '更新人',
    `is_deleted`  int         NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-显示，1-隐藏',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT '角色表';

-- -------------------------------------------------------------------
-- 6.0 menu 菜单表：存储前端路由菜单信息
-- -------------------------------------------------------------------
CREATE TABLE `menu` (
    `id`          int          NOT NULL AUTO_INCREMENT COMMENT '主键',
    `menus_index` varchar(5)   NOT NULL COMMENT '一级菜单索引',
    `title`       varchar(50)  NOT NULL COMMENT '菜单名称',
    `icon`        varchar(50)  NOT NULL COMMENT '菜单图标',
    `path`        varchar(100) NOT NULL COMMENT '菜单路径',
    `parent_id`   int          NOT NULL COMMENT '父级Id',
    PRIMARY KEY (`id`),
    KEY `idx_menu_parent_id` (`parent_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT '菜单表';

-- -------------------------------------------------------------------
-- 7.0 rolemenu 角色菜单对应表：存储角色和菜单的对应关系
-- -------------------------------------------------------------------
CREATE TABLE `rolemenu` (
    `id`      int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `role_id` int NOT NULL COMMENT '角色外键',
    `menu`    int NOT NULL COMMENT '菜单外键',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_rolemenu_role_menu` (`role_id`, `menu`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT '角色菜单对应表';

-- -------------------------------------------------------------------
-- 4.0 room 房屋表：存储房间楼栋信息
-- -------------------------------------------------------------------
CREATE TABLE `room` (
    `id`         int         NOT NULL AUTO_INCREMENT COMMENT '主键',
    `room_floor` varchar(10) NOT NULL COMMENT '房间楼层',
    `room_no`    int         NOT NULL COMMENT '房间号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_room_floor_no` (`room_floor`, `room_no`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT '房屋表';

-- -------------------------------------------------------------------
-- 2.0 bed 床位表：存储房间床位信息
-- -------------------------------------------------------------------
CREATE TABLE `bed` (
    `id`         int          NOT NULL AUTO_INCREMENT COMMENT '主键',
    `room_no`    int          NOT NULL COMMENT '房间号',
    `bed_no`     varchar(20)  NOT NULL COMMENT '床位号',
    `bed_status` int          NOT NULL COMMENT '床铺状态：1-空闲，2-占用，3-维修',
    `remarks`    varchar(255) NOT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_bed_room_no` (`room_no`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT '床位表';

-- -------------------------------------------------------------------
-- 3.0 beddetails 床位详细信息表：存储床位详细信息
-- -------------------------------------------------------------------
CREATE TABLE `beddetails` (
    `id`          int          NOT NULL AUTO_INCREMENT COMMENT '主键',
    `start_date`  date         NOT NULL COMMENT '床位起始日期',
    `end_date`    date         NOT NULL COMMENT '床位结束日期',
    `bed_details` varchar(255) NOT NULL COMMENT '床位详细信息',
    `customer_id` int          NOT NULL COMMENT '房客ID',
    `bed_id`      int          NOT NULL COMMENT '床位ID',
    `is_deleted`  int          NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-显示，1-隐藏',
    PRIMARY KEY (`id`),
    KEY `idx_beddetails_bed_id` (`bed_id`),
    KEY `idx_beddetails_customer_id` (`customer_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT '床位详细信息表';

-- -------------------------------------------------------------------
-- 18.0 customer 客户表：存储客户信息
-- -------------------------------------------------------------------
CREATE TABLE `customer` (
    `id`                 int          NOT NULL AUTO_INCREMENT COMMENT '主键',
    `customer_name`      varchar(20)  NOT NULL COMMENT '客户姓名',
    `customer_age`       int          NOT NULL COMMENT '年龄',
    `customer_sex`       int          NOT NULL COMMENT '性别：0-男，1-女',
    `idcard`             varchar(20)  NOT NULL COMMENT '身份证号',
    `room_no`            varchar(20)  NOT NULL COMMENT '房间号',
    `building_no`        varchar(11)  NOT NULL COMMENT '房屋楼栋',
    `bed_id`             int          NOT NULL COMMENT '床位',
    `level_id`           int          NULL DEFAULT NULL COMMENT '护理等级（可为空：移除护理级别后置空）',
    `user_id`            int          NOT NULL DEFAULT -1 COMMENT '归属系统生活管家（外键），无管家则 -1',
    `checkin_date`       date         NOT NULL COMMENT '入住时间',
    `expiration_date`    date         NOT NULL COMMENT '合同到期时间',
    `birthday`           date         NOT NULL COMMENT '出生日期',
    `contact_tel`        varchar(20)  NOT NULL COMMENT '联系电话',
    `family_member`      varchar(20)  NOT NULL COMMENT '家属',
    `height`             varchar(20)  NOT NULL COMMENT '身高',
    `weight`             varchar(20)  NOT NULL COMMENT '体重',
    `blood_type`         varchar(20)  NOT NULL COMMENT '血型',
    `psychosomatic_state` varchar(255) NOT NULL COMMENT '身体状况',
    `attention`          varchar(255) NOT NULL COMMENT '注意事项',
    `filepath`           varchar(50)  NOT NULL COMMENT '头像路径',
    `is_deleted`         int          NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-显示，1-隐藏',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_customer_idcard` (`idcard`),
    KEY `idx_customer_bed_id` (`bed_id`),
    KEY `idx_customer_level_id` (`level_id`),
    KEY `idx_customer_user_id` (`user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT '客户表';

-- -------------------------------------------------------------------
-- 8.0 nursecontent 护理内容表：存储护理内容信息
-- -------------------------------------------------------------------
CREATE TABLE `nursecontent` (
    `id`              int          NOT NULL AUTO_INCREMENT COMMENT '主键',
    `serial_number`   varchar(20)  NOT NULL COMMENT '序号',
    `nursing_name`    varchar(55)  NOT NULL COMMENT '名称',
    `service_price`   varchar(55)  NOT NULL COMMENT '价格',
    `message`         varchar(100) NOT NULL COMMENT '描述',
    `status`          int          NOT NULL DEFAULT 1 COMMENT '状态：1-启用，2-停用',
    `execution_cycle` varchar(20)  NOT NULL COMMENT '执行周期',
    `execution_times` varchar(20)  NOT NULL COMMENT '执行次数',
    `is_deleted`      int          NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-显示，1-隐藏',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT '护理内容表';

-- -------------------------------------------------------------------
-- 9.0 nurselevel 护理等级表：存储护理等级信息
-- -------------------------------------------------------------------
CREATE TABLE `nurselevel` (
    `id`           int         NOT NULL AUTO_INCREMENT COMMENT '主键',
    `level_name`   varchar(20) NOT NULL COMMENT '护理等级',
    `level_status` int         NOT NULL DEFAULT 1 COMMENT '护理状态：1-启用，2-停用',
    `is_deleted`   int         NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-显示，1-隐藏',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT '护理等级表';

-- -------------------------------------------------------------------
-- 10.0 nurselevelitem 护理等级项目对应表：存储护理等级与项目对应信息
-- -------------------------------------------------------------------
CREATE TABLE `nurselevelitem` (
    `id`       int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `level_id` int NOT NULL COMMENT '护理等级外键',
    `item_id`  int NOT NULL COMMENT '护理内容项目外键',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_nurselevelitem_level_item` (`level_id`, `item_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT '护理等级项目对应表';

-- -------------------------------------------------------------------
-- 12.0 customernurseitem 客户护理项目表：存储客户护理项目
-- -------------------------------------------------------------------
CREATE TABLE `customernurseitem` (
    `id`             int  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `item_id`        int  NOT NULL COMMENT '护理项目外键',
    `custormer_id`   int  NOT NULL COMMENT '客户外键（字典原拼写）',
    `level_id`       int  NOT NULL COMMENT '护理等级外键',
    `nurse_number`   int  NOT NULL COMMENT '护理数量',
    `buy_time`       date NOT NULL COMMENT '护理购买日期',
    `maturity_time`  date NOT NULL COMMENT '护理到期日期',
    `is_deleted`     int  NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-显示，1-隐藏',
    PRIMARY KEY (`id`),
    KEY `idx_cni_custormer_id` (`custormer_id`),
    KEY `idx_cni_item_id` (`item_id`),
    KEY `idx_cni_level_id` (`level_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT '客户护理项目表';

-- -------------------------------------------------------------------
-- 11.0 nurserecord 护理记录表：存储护理记录信息
-- -------------------------------------------------------------------
CREATE TABLE `nurserecord` (
    `id`               int          NOT NULL AUTO_INCREMENT COMMENT '主键',
    `customer_id`      int          NOT NULL COMMENT '客户ID',
    `item_id`          int          NOT NULL COMMENT '护理项目ID',
    `user_id`          int          NOT NULL COMMENT '护理人员ID',
    `nursing_time`     datetime     NOT NULL COMMENT '护理时间',
    `nursing_content`  varchar(255) NOT NULL COMMENT '护理内容',
    `nursing_count`    int          NOT NULL COMMENT '护理次数',
    `is_deleted`       int          NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-显示，1-隐藏',
    PRIMARY KEY (`id`),
    KEY `idx_nurserecord_customer_id` (`customer_id`),
    KEY `idx_nurserecord_item_id` (`item_id`),
    KEY `idx_nurserecord_user_id` (`user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT '护理记录表';

-- -------------------------------------------------------------------
-- 13.0 outward 外出登记表：存储外出登记信息
-- -------------------------------------------------------------------
CREATE TABLE `outward` (
    `id`                  int          NOT NULL AUTO_INCREMENT COMMENT '主键',
    `customer_id`         int          NOT NULL COMMENT '客户ID',
    `outgoingreason`      varchar(100) NOT NULL COMMENT '外出原因',
    `outgoingtime`        date         NOT NULL COMMENT '外出时间',
    `expectedreturntime`  date         NOT NULL COMMENT '预计回院时间',
    `actualreturntime`    date         NOT NULL COMMENT '实际回院时间',
    `escorted`            varchar(50)  NOT NULL COMMENT '陪同人',
    `relation`            varchar(50)  NOT NULL COMMENT '与陪同人关系',
    `escortedtel`         varchar(50)  NOT NULL COMMENT '陪同人电话',
    `auditstatus`         int          NOT NULL DEFAULT 0 COMMENT '审核状态：0-未提交，1-同意，2-拒绝',
    `auditperson`         varchar(50)  NOT NULL COMMENT '审核人',
    `audittime`           date         NOT NULL COMMENT '审核时间',
    `remarks`             varchar(100) NOT NULL COMMENT '备注',
    `is_deleted`          int          NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-显示，1-隐藏',
    PRIMARY KEY (`id`),
    KEY `idx_outward_customer_id` (`customer_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT '外出登记表';

-- -------------------------------------------------------------------
-- 14.0 backdown 退住登记表：存储退住登记信息
-- -------------------------------------------------------------------
CREATE TABLE `backdown` (
    `id`             int          NOT NULL AUTO_INCREMENT COMMENT '主键',
    `customer_id`    int          NOT NULL COMMENT '客户ID',
    `retreattime`    date         NOT NULL COMMENT '退住时间',
    `retreattype`    int          NOT NULL COMMENT '退住类型：0-到期退住，1-自愿退住，2-占床床位',
    `retreatreason`  varchar(100) NOT NULL COMMENT '退住原因',
    `auditstatus`    int          NOT NULL DEFAULT 0 COMMENT '审核状态：0-未提交，1-同意，2-拒绝',
    `auditperson`    varchar(50)  NOT NULL COMMENT '审核人',
    `audittime`      date         NOT NULL COMMENT '审核时间',
    `remarks`        varchar(100) NOT NULL COMMENT '备注',
    `is_deleted`     int          NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-显示，1-隐藏',
    PRIMARY KEY (`id`),
    KEY `idx_backdown_customer_id` (`customer_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT '退住登记表';

-- -------------------------------------------------------------------
-- 15.0 food 食品表：存储食品信息
-- -------------------------------------------------------------------
CREATE TABLE `food` (
    `id`        int           NOT NULL AUTO_INCREMENT COMMENT '主键',
    `food_name` varchar(30)   NOT NULL COMMENT '食品名称',
    `food_type` varchar(30)   NOT NULL COMMENT '食品类型',
    `price`     decimal(10,2) NOT NULL COMMENT '价格',
    `food_img`  varchar(100)  NOT NULL COMMENT '图片路径',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT '食品表';

-- -------------------------------------------------------------------
-- 17.0 meal 膳食搭配表：存储膳食搭配信息
-- -------------------------------------------------------------------
CREATE TABLE `meal` (
    `id`         int         NOT NULL AUTO_INCREMENT COMMENT '主键',
    `week_day`   varchar(10) NOT NULL COMMENT '周几',
    `food_id`    int         NOT NULL COMMENT '食品ID',
    `meal_type`  int         NOT NULL COMMENT '餐次类型：1-早餐，2-午餐，3-晚餐',
    `taste`      varchar(30) NOT NULL COMMENT '味道：甜、咸、苦、辣',
    `is_deleted` int         NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-显示，1-隐藏',
    PRIMARY KEY (`id`),
    KEY `idx_meal_food_id` (`food_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT '膳食搭配表';

-- -------------------------------------------------------------------
-- 16.0 customerpreference 客户喜好表：存储客户喜好信息
-- -------------------------------------------------------------------
CREATE TABLE `customerpreference` (
    `id`          int         NOT NULL AUTO_INCREMENT COMMENT '主键',
    `customer_id` int         NOT NULL COMMENT '房客ID',
    `preferences` varchar(30) NOT NULL COMMENT '饮食喜好',
    `attention`   varchar(30) NOT NULL COMMENT '注意事项',
    `remark`      varchar(30) NOT NULL COMMENT '备注',
    `is_deleted`  int         NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-显示，1-隐藏',
    PRIMARY KEY (`id`),
    KEY `idx_customerpreference_customer_id` (`customer_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT '客户喜好表';

-- ===================================================================
-- 与数据字典的差异说明
-- 1. user.password：字典为 varchar(20)，不足以存放加密后的密码，此处放宽为 varchar(255)。
-- 2. user.create_time / update_time：字典标注 date，按时间戳语义建为 datetime。
-- 3. customer.user_id：字典为 varchar(20)，作为管家外键此处按 int 建表。
-- 4. 主键 id 统一使用 AUTO_INCREMENT；字典中所有字段均标注非空，故统一 NOT NULL，
--    逻辑删除标记与状态类字段补充了默认值。
-- 5. 字典未定义外键约束，脚本仅建立普通索引，避免与逻辑删除冲突。
-- 6. customer.level_id：字典标注 NOT NULL，但护理模块"移除护理级别"需将该列置空，
--    故此处改为 NULL DEFAULT NULL（见护理模块工作指导 §6.1）。若数据库已按旧脚本建好，
--    请执行：ALTER TABLE customer MODIFY level_id int NULL;
-- ===================================================================
