-- ===================================================================
-- 东软颐养中心 清空脚本：TRUNCATE 全部 18 张表
-- 用途：重置 yyzx 库，为 data_insert.sql 灌数据做准备（可重复执行）
-- 生成来源：build_dataset.py（DB / SQL / Excel 三者同源）
-- ===================================================================
USE `yyzx`;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS=0;

TRUNCATE TABLE `meal`;
TRUNCATE TABLE `customerpreference`;
TRUNCATE TABLE `food`;
TRUNCATE TABLE `backdown`;
TRUNCATE TABLE `outward`;
TRUNCATE TABLE `nurserecord`;
TRUNCATE TABLE `customernurseitem`;
TRUNCATE TABLE `nurselevelitem`;
TRUNCATE TABLE `nurselevel`;
TRUNCATE TABLE `nursecontent`;
TRUNCATE TABLE `beddetails`;
TRUNCATE TABLE `customer`;
TRUNCATE TABLE `bed`;
TRUNCATE TABLE `room`;
TRUNCATE TABLE `rolemenu`;
TRUNCATE TABLE `menu`;
TRUNCATE TABLE `role`;
TRUNCATE TABLE `user`;

SET FOREIGN_KEY_CHECKS=1;
