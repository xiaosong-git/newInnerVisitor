/*
Navicat MySQL Data Transfer

Source Server         : 192.168.10.43
Source Server Version : 50718
Source Host           : 192.168.10.43:3306
Source Database       : innervisitor

Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001

Date: 2020-09-20 19:26:10
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for v_ad_banner
-- ----------------------------
DROP TABLE IF EXISTS `v_ad_banner`;
CREATE TABLE `v_ad_banner` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL COMMENT '广告标题',
  `imgUrl` varchar(255) DEFAULT NULL COMMENT '广告图片路径',
  `hrefUrl` varchar(255) DEFAULT NULL COMMENT '超链接',
  `status` int(1) DEFAULT NULL COMMENT '广告状态 0无效 1有效',
  `createTime` datetime DEFAULT NULL COMMENT '生产时间',
  `updateTime` varchar(20) DEFAULT NULL COMMENT '修改时间',
  `orders` int(11) DEFAULT NULL COMMENT '排序，数值越高，越靠前',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='广告轮播图';

-- ----------------------------
-- Records of v_ad_banner
-- ----------------------------
INSERT INTO `v_ad_banner` VALUES ('16', '中央气象台继续发布暴雨橙色预警 四川局地有特大暴雨', 'ad/1597656896623.jpeg', 'https://baijiahao.baidu.com/s?id=1675228189268670870&wfr=spider&for=pc', '1', '2020-08-17 17:35:04', null, '4');
INSERT INTO `v_ad_banner` VALUES ('18', '深圳实现5G独立组网全覆盖', 'ad/1597657125516.jpeg', 'https://baijiahao.baidu.com/s?id=1675238105712531699&wfr=spider&for=pc', '1', '2020-08-17 17:35:26', '2020-08-17 17:38:47', '1');

-- ----------------------------
-- Table structure for v_app_checkindate
-- ----------------------------
DROP TABLE IF EXISTS `v_app_checkindate`;
CREATE TABLE `v_app_checkindate` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '考勤打卡',
  `group_id` bigint(20) DEFAULT NULL COMMENT '规则id',
  `workdays` varchar(255) DEFAULT NULL COMMENT '工作日。若为固定时间上下班或自由上下班，则1到6分别表示星期一到星期六，0表示星期日；若为按班次上下班，则表示拉取班次的日期。',
  `flex_time` int(10) DEFAULT NULL COMMENT '弹性时间（毫秒）',
  `noneed_offwork` varchar(5) DEFAULT NULL COMMENT '下班不需要打卡 T--是  F--否',
  `limit_aheadtime` int(10) DEFAULT NULL COMMENT '打卡时间限制（毫秒）',
  `exp1` varchar(100) DEFAULT NULL COMMENT '扩展字段1',
  `exp2` varchar(100) DEFAULT NULL COMMENT '扩展字段2',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of v_app_checkindate
-- ----------------------------

-- ----------------------------
-- Table structure for v_app_checkintime
-- ----------------------------
DROP TABLE IF EXISTS `v_app_checkintime`;
CREATE TABLE `v_app_checkintime` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '考勤打卡',
  `work_sec` int(10) DEFAULT NULL COMMENT '上班时间，表示为距离当天0点的秒数。',
  `off_work_sec` int(10) DEFAULT NULL COMMENT '下班时间，表示为距离当天0点的秒数。',
  `remind_work_sec` int(10) DEFAULT NULL COMMENT '上班提醒时间，表示为距离当天0点的秒数。',
  `remind_off_work_sec` int(10) DEFAULT NULL COMMENT '下班提醒时间，表示为距离当天0点的秒数。',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of v_app_checkintime
-- ----------------------------

-- ----------------------------
-- Table structure for v_app_menu
-- ----------------------------
DROP TABLE IF EXISTS `v_app_menu`;
CREATE TABLE `v_app_menu` (
  `id` bigint(11) NOT NULL,
  `function_name` varchar(20) DEFAULT NULL COMMENT '功能名',
  `menu_code` varchar(60) DEFAULT '' COMMENT '菜单代码',
  `menu_name` varchar(60) DEFAULT '' COMMENT '菜单名',
  `menu_url` varchar(60) DEFAULT NULL COMMENT '菜单地址',
  `sid` bigint(20) DEFAULT NULL COMMENT '父id',
  `istop` varchar(10) DEFAULT NULL COMMENT '是否制定',
  `menu_icon` varchar(255) DEFAULT NULL COMMENT '菜单控件图地址',
  `relation_no` varchar(512) DEFAULT NULL COMMENT '菜单关联',
  `sstatus` varchar(60) DEFAULT NULL COMMENT '菜单状态',
  `updateTime` varchar(20) DEFAULT NULL COMMENT '修改时间',
  `ext1` varchar(255) DEFAULT NULL COMMENT '拓展字段1',
  `ext2` varchar(255) DEFAULT NULL COMMENT '拓展字段2',
  `ext3` varchar(255) DEFAULT NULL COMMENT '拓展字段3',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of v_app_menu
-- ----------------------------
INSERT INTO `v_app_menu` VALUES ('1', null, '', '我的', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `v_app_menu` VALUES ('2', null, '', '首页', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `v_app_menu` VALUES ('3', null, '', '访客', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `v_app_menu` VALUES ('4', null, '', '通讯录', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `v_app_menu` VALUES ('5', null, '', '访问', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `v_app_menu` VALUES ('6', null, '', '邀约', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `v_app_menu` VALUES ('7', null, '', '好友', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `v_app_menu` VALUES ('8', null, '', '实名认证', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `v_app_menu` VALUES ('9', null, '', '会议室', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `v_app_menu` VALUES ('10', null, '', '公司管理', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `v_app_menu` VALUES ('11', null, '', '会议室', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `v_app_menu` VALUES ('12', null, '', '发起访问', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `v_app_menu` VALUES ('13', null, '', '访客二维码', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `v_app_menu` VALUES ('14', null, '', '新的朋友', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `v_app_menu` VALUES ('15', null, '', '门禁卡', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `v_app_menu` VALUES ('16', null, '', '茶室', null, null, null, null, null, null, null, null, null, null);

-- ----------------------------
-- Table structure for v_app_role
-- ----------------------------
DROP TABLE IF EXISTS `v_app_role`;
CREATE TABLE `v_app_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(60) CHARACTER SET gbk NOT NULL COMMENT '角色名',
  `sid` bigint(20) NOT NULL COMMENT '父id',
  `role_relation_no` varchar(512) CHARACTER SET gbk DEFAULT NULL COMMENT '角色关系',
  `update_time` varchar(20) CHARACTER SET gbk DEFAULT NULL COMMENT '修改时间',
  `description` varchar(255) CHARACTER SET gbk DEFAULT NULL COMMENT '角色描述',
  `ext1` varchar(255) DEFAULT NULL COMMENT '拓展字段1',
  `ext2` varchar(255) DEFAULT NULL COMMENT '拓展字段2',
  `ext3` varchar(255) DEFAULT NULL COMMENT '拓展字段3',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of v_app_role
-- ----------------------------
INSERT INTO `v_app_role` VALUES ('1', '管理员', '0', '1.', '2019-09-14 16:55', '管理员', null, null, null);
INSERT INTO `v_app_role` VALUES ('2', '普通员工', '1', '1.2', '2019-09-14 16:55', '普通员工', null, null, null);
INSERT INTO `v_app_role` VALUES ('8', '访客', '1', '1.20191126152452.', '2019-11-26 15:24:52', '访客角色', null, null, null);
INSERT INTO `v_app_role` VALUES ('9', '标准大楼', '1', '1.20191126152512.', '2019-11-26 15:28:59', '标准大楼角色', null, null, null);

-- ----------------------------
-- Table structure for v_app_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `v_app_role_menu`;
CREATE TABLE `v_app_role_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `role_id` bigint(20) NOT NULL COMMENT '角色id',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单id',
  `isOpen` varchar(5) DEFAULT 'T' COMMENT 'T--开启  F--未开启',
  `update_time` varchar(20) DEFAULT NULL COMMENT '更新时间',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作者id',
  `ext1` varchar(255) DEFAULT NULL COMMENT '拓展字段1',
  `ext2` varchar(255) DEFAULT NULL COMMENT '拓展字段2',
  `ext3` varchar(255) DEFAULT NULL COMMENT '拓展字段3',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `user_role_id` (`role_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=168 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of v_app_role_menu
-- ----------------------------
INSERT INTO `v_app_role_menu` VALUES ('1', '5', '1', 'T', null, null, null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('2', '5', '2', 'T', null, null, null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('3', '5', '3', 'T', null, null, null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('4', '5', '4', 'T', null, null, null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('5', '5', '5', 'T', null, null, null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('6', '5', '6', 'T', null, null, null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('7', '5', '7', 'T', null, null, null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('8', '5', '8', 'T', null, null, null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('10', '5', '10', 'T', null, null, null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('15', '5', '15', 'F', null, null, null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('108', '6', '1', 'T', '2019-09-27 19:32:26', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('109', '6', '5', 'T', '2019-09-27 19:32:26', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('110', '6', '6', 'T', '2019-09-27 19:32:26', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('111', '6', '7', 'T', '2019-09-27 19:32:26', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('112', '6', '8', 'T', '2019-09-27 19:32:26', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('114', '6', '10', 'T', '2019-09-27 19:32:26', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('115', '6', '2', 'T', '2019-09-27 19:32:26', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('117', '6', '12', 'T', '2019-09-27 19:32:26', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('118', '6', '13', 'T', '2019-09-27 19:32:26', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('119', '6', '4', 'T', '2019-09-27 19:32:26', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('120', '6', '14', 'T', '2019-09-27 19:32:26', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('137', '9', '1', 'T', '2019-11-26 15:26:03', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('138', '9', '5', 'T', '2019-11-26 15:26:03', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('139', '9', '6', 'T', '2019-11-26 15:26:03', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('140', '9', '7', 'T', '2019-11-26 15:26:03', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('141', '9', '8', 'T', '2019-11-26 15:26:03', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('143', '9', '10', 'T', '2019-11-26 15:26:03', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('145', '9', '2', 'T', '2019-11-26 15:26:03', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('147', '9', '12', 'T', '2019-11-26 15:26:03', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('148', '9', '13', 'T', '2019-11-26 15:26:03', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('149', '9', '3', 'T', '2019-11-26 15:26:03', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('150', '9', '4', 'T', '2019-11-26 15:26:03', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('151', '9', '14', 'T', '2019-11-26 15:26:03', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('152', '8', '1', 'T', '2019-11-26 15:28:42', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('153', '8', '5', 'T', '2019-11-26 15:28:42', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('154', '8', '6', 'T', '2019-11-26 15:28:42', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('155', '8', '7', 'T', '2019-11-26 15:28:42', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('156', '8', '8', 'T', '2019-11-26 15:28:42', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('158', '8', '10', 'T', '2019-11-26 15:28:42', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('160', '8', '2', 'T', '2019-11-26 15:28:42', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('162', '8', '12', 'T', '2019-11-26 15:28:42', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('163', '8', '13', 'T', '2019-11-26 15:28:42', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('164', '8', '3', 'T', '2019-11-26 15:28:42', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('165', '8', '4', 'T', '2019-11-26 15:28:42', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('166', '8', '14', 'T', '2019-11-26 15:28:42', '1', null, null, null);
INSERT INTO `v_app_role_menu` VALUES ('167', '9', '15', 'T', null, null, null, null, null);

-- ----------------------------
-- Table structure for v_app_user_message
-- ----------------------------
DROP TABLE IF EXISTS `v_app_user_message`;
CREATE TABLE `v_app_user_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `fromUserId` bigint(20) NOT NULL COMMENT '消息来源',
  `toUserId` bigint(20) NOT NULL COMMENT '消息去向',
  `message` varchar(512) CHARACTER SET utf8 DEFAULT NULL COMMENT '内容',
  `update_time` varchar(20) DEFAULT NULL COMMENT '上传时间',
  `type` int(10) DEFAULT NULL COMMENT '消息类型 ',
  `ext1` varchar(200) CHARACTER SET utf8 DEFAULT NULL COMMENT '扩展字段1',
  `ext2` varchar(200) CHARACTER SET utf8 DEFAULT NULL COMMENT '扩展字段2',
  `ext3` varchar(200) CHARACTER SET utf8 DEFAULT NULL COMMENT '扩展字段3',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `from_userid` (`fromUserId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of v_app_user_message
-- ----------------------------
INSERT INTO `v_app_user_message` VALUES ('1', '125', '126', 'value', '2020-01-02 20:27', '1', null, null, null);
INSERT INTO `v_app_user_message` VALUES ('2', '125', '126', 'value', '2020-01-02 20:30', '1', null, null, null);
INSERT INTO `v_app_user_message` VALUES ('3', '125', '126', 'value', '2020-01-02 20:40', '1', null, null, null);
INSERT INTO `v_app_user_message` VALUES ('5', '125', '126', 'value', '2020-01-03 15:31', '1', null, null, null);
INSERT INTO `v_app_user_message` VALUES ('6', '125', '126', 'value', '2020-01-03 15:33', '1', null, null, null);
INSERT INTO `v_app_user_message` VALUES ('7', '125', '126', 'value', '2020-01-03 15:35', '1', null, null, null);

-- ----------------------------
-- Table structure for v_app_user_notice
-- ----------------------------
DROP TABLE IF EXISTS `v_app_user_notice`;
CREATE TABLE `v_app_user_notice` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) DEFAULT NULL,
  `maxNoticeId` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_userId` (`userId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=393 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of v_app_user_notice
-- ----------------------------
INSERT INTO `v_app_user_notice` VALUES ('372', '6', '16');
INSERT INTO `v_app_user_notice` VALUES ('374', '5', '16');
INSERT INTO `v_app_user_notice` VALUES ('376', '10', '16');
INSERT INTO `v_app_user_notice` VALUES ('378', '4', '16');
INSERT INTO `v_app_user_notice` VALUES ('382', '20', '20');
INSERT INTO `v_app_user_notice` VALUES ('384', '8', '20');
INSERT INTO `v_app_user_notice` VALUES ('386', '1', '20');
INSERT INTO `v_app_user_notice` VALUES ('388', '12', '20');
INSERT INTO `v_app_user_notice` VALUES ('390', '16', '20');
INSERT INTO `v_app_user_notice` VALUES ('392', '24', '20');

-- ----------------------------
-- Table structure for v_app_version
-- ----------------------------
DROP TABLE IF EXISTS `v_app_version`;
CREATE TABLE `v_app_version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `appType` varchar(60) DEFAULT NULL,
  `channel` varchar(60) DEFAULT NULL,
  `versionName` varchar(60) DEFAULT NULL,
  `versionNum` varchar(60) DEFAULT NULL,
  `isImmediatelyUpdate` varchar(60) DEFAULT NULL,
  `updateUrl` varchar(255) DEFAULT NULL,
  `memo` varchar(512) DEFAULT NULL,
  `createDate` varchar(10) DEFAULT NULL,
  `updateTime` varchar(20) DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of v_app_version
-- ----------------------------
INSERT INTO `v_app_version` VALUES ('12', 'ios', 'AppStore', '1.0.0', '1.0.0', 'T', 'https://pgyer.com/8vPc', '1.优化性能', null, null);
INSERT INTO `v_app_version` VALUES ('13', 'android', 'visitor', '2.0.0', '14', 'T', 'https://www.pgyer.com/sX9W', '1.优化性能', '2018-11-26', null);

-- ----------------------------
-- Table structure for v_business
-- ----------------------------
DROP TABLE IF EXISTS `v_business`;
CREATE TABLE `v_business` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `business_name` varchar(20) DEFAULT NULL COMMENT '商家店名',
  `tel` varchar(20) DEFAULT NULL COMMENT '电话',
  `true_name` varchar(20) DEFAULT NULL COMMENT '真实姓名',
  `createtime` varbinary(20) DEFAULT NULL COMMENT '创建时间',
  `update_time` varchar(20) DEFAULT NULL COMMENT '修改时间',
  `extra1` varchar(255) DEFAULT NULL,
  `extra2` varchar(255) DEFAULT NULL,
  `extra3` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of v_business
-- ----------------------------

-- ----------------------------
-- Table structure for v_comp_vip_user
-- ----------------------------
DROP TABLE IF EXISTS `v_comp_vip_user`;
CREATE TABLE `v_comp_vip_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'VIP管理',
  `orgId` bigint(20) DEFAULT NULL COMMENT '大楼id',
  `updateTime` varchar(100) DEFAULT NULL COMMENT '更新时间HH:mm:ss hh"ii:ss',
  `userId` bigint(20) DEFAULT NULL COMMENT '用户Id',
  `postId` bigint(20) DEFAULT NULL COMMENT '请求id',
  `userName` varchar(60) DEFAULT NULL COMMENT '用户姓名',
  `createtime` varchar(30) DEFAULT NULL COMMENT '创建日期yy:MM:dd',
  `endtime` varchar(30) DEFAULT NULL COMMENT '过期时间',
  `roleType` varchar(60) DEFAULT NULL COMMENT '角色:(staff:普通员工,manage:管理员,front:前台)',
  `status` varchar(60) DEFAULT NULL COMMENT '状态：确认:applySuc/未确认:applying/确认不通过:applyFail',
  `currentStatus` varchar(60) DEFAULT NULL COMMENT 'normal为正常，deleted为删除',
  `phone` varchar(30) DEFAULT NULL COMMENT '电话',
  `sex` varchar(10) DEFAULT NULL COMMENT '性别',
  `company` varchar(30) DEFAULT NULL COMMENT '所属公司',
  `position` varchar(30) DEFAULT NULL COMMENT '职位',
  `authorize_reason` varchar(100) DEFAULT NULL COMMENT '授权原因',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of v_comp_vip_user
-- ----------------------------
INSERT INTO `v_comp_vip_user` VALUES ('1', null, null, null, null, 'dea23', '2020-01-01', '2020-01-01', null, null, null, '15306928698', '1', '胜多负少', '士大夫撒', '三万多人服务');
INSERT INTO `v_comp_vip_user` VALUES ('2', null, null, null, null, '232', '2020-01-21', '2020-01-30', null, null, null, '234', '1', '为首的分散', '手动阀', '是否认为');
INSERT INTO `v_comp_vip_user` VALUES ('3', null, null, null, null, '松岛枫的三发', '2020-01-07', '2020-01-13', null, null, null, '15306986987', '1', '恶的乳房', '物是人非挖', '士大夫撒');
INSERT INTO `v_comp_vip_user` VALUES ('4', null, null, null, null, '张三', '2020-08-07', '2020-08-31', null, null, null, '15189652026', '1', '没头脑', '开发工程师', null);

-- ----------------------------
-- Table structure for v_d_inout
-- ----------------------------
DROP TABLE IF EXISTS `v_d_inout`;
CREATE TABLE `v_d_inout` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '上位机进出日志',
  `orgCode` varchar(10) DEFAULT NULL COMMENT '大楼编号',
  `pospCode` varchar(20) DEFAULT NULL COMMENT '上位机编号',
  `scanDate` varchar(20) DEFAULT NULL COMMENT '设备扫描日期：yyyy:MM:dd',
  `scanTime` varchar(20) DEFAULT NULL COMMENT '设备扫描时间：HH:mm:ss',
  `inOrOut` varchar(5) NOT NULL COMMENT '进出类型：in/out',
  `outNumber` varchar(10) DEFAULT NULL COMMENT '通道编号',
  `deviceType` varchar(20) DEFAULT NULL COMMENT '通行设备的类型：QRCODE/FACE',
  `deviceIp` varchar(20) DEFAULT NULL COMMENT '设备的IP地址',
  `userType` varchar(10) DEFAULT NULL COMMENT '通行人员类型：staff/visitor',
  `userName` varchar(10) NOT NULL COMMENT '通行人员名字',
  `idCard` varchar(255) DEFAULT NULL COMMENT '通行人员证件号',
  `isSendFlag` varchar(2) DEFAULT NULL COMMENT '发送记录的标识：已发送(T)/未发送(F)',
  `expt1` varchar(255) DEFAULT NULL COMMENT '拓展字段1',
  `expt2` varchar(255) DEFAULT NULL COMMENT '拓展字段2',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=212 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of v_d_inout
-- ----------------------------
INSERT INTO `v_d_inout` VALUES ('64', 'yddl', null, '2020-09-08', '14:25:33', 'in', null, 'FACE', '192.168.1.11', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('66', 'yddl', null, '2020-09-08', '14:25:43', 'in', null, 'FACE', '192.168.1.10', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('68', 'yddl', null, '2020-09-08', '14:25:46', 'in', null, 'FACE', '192.168.1.10', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('70', 'yddl', null, '2020-09-08', '14:25:48', 'in', null, 'FACE', '192.168.1.10', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('72', 'yddl', null, '2020-09-08', '14:25:50', 'in', null, 'FACE', '192.168.1.10', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('74', 'yddl', null, '2020-09-08', '14:25:53', 'in', null, 'FACE', '192.168.1.10', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('76', 'yddl', null, '2020-09-08', '14:25:54', 'in', null, 'FACE', '192.168.1.10', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('78', 'yddl', null, '2020-09-08', '14:25:57', 'in', null, 'FACE', '192.168.1.10', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('80', 'yddl', null, '2020-09-08', '14:25:59', 'in', null, 'FACE', '192.168.1.10', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('82', 'yddl', null, '2020-09-08', '14:26:34', 'in', null, 'FACE', '192.168.1.10', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('84', 'yddl', null, '2020-09-08', '14:27:12', 'in', null, 'FACE', '192.168.1.10', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('86', 'yddl', null, '2020-09-08', '14:27:23', 'in', null, 'FACE', '192.168.1.10', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('88', 'yddl', null, '2020-09-08', '14:27:25', 'in', null, 'FACE', '192.168.1.10', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('90', 'yddl', null, '2020-09-08', '14:27:30', 'in', null, 'FACE', '192.168.1.10', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('92', 'yddl', null, '2020-09-08', '14:31:52', 'in', null, 'FACE', '192.168.1.10', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('94', 'yddl', null, '2020-09-08', '15:42:36', 'in', null, 'FACE', '192.168.1.10', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('96', '101', null, '2020-09-10', '10:09:00', 'in', null, 'FACE', '192.168.0.10', 'staff', '雷磊', 'C2BB3553446EB521A85BBDFE0B073DF38453EAC3E88D6C35', null, null, null);
INSERT INTO `v_d_inout` VALUES ('98', '101', null, '2020-09-10', '10:09:11', 'in', null, 'FACE', '192.168.0.10', 'staff', '雷磊', 'C2BB3553446EB521A85BBDFE0B073DF38453EAC3E88D6C35', null, null, null);
INSERT INTO `v_d_inout` VALUES ('100', '101', null, '2020-09-10', '10:09:25', 'in', null, 'FACE', '192.168.0.10', 'staff', '雷磊', 'C2BB3553446EB521A85BBDFE0B073DF38453EAC3E88D6C35', null, null, null);
INSERT INTO `v_d_inout` VALUES ('102', '101', null, '2020-09-10', '10:12:18', 'in', null, 'FACE', '192.168.0.10', 'staff', '雷磊', 'C2BB3553446EB521A85BBDFE0B073DF38453EAC3E88D6C35', null, null, null);
INSERT INTO `v_d_inout` VALUES ('104', '101', null, '2020-09-10', '11:23:11', 'in', null, 'FACE', '192.168.0.10', 'staff', '雷磊', 'C2BB3553446EB521A85BBDFE0B073DF38453EAC3E88D6C35', null, null, null);
INSERT INTO `v_d_inout` VALUES ('106', '101', null, '2020-09-10', '11:23:17', 'in', null, 'FACE', '192.168.0.10', 'staff', '雷磊', 'C2BB3553446EB521A85BBDFE0B073DF38453EAC3E88D6C35', null, null, null);
INSERT INTO `v_d_inout` VALUES ('108', '101', null, '2020-09-10', '11:23:20', 'in', null, 'FACE', '192.168.0.10', 'staff', '雷磊', 'C2BB3553446EB521A85BBDFE0B073DF38453EAC3E88D6C35', null, null, null);
INSERT INTO `v_d_inout` VALUES ('110', '101', null, '2020-09-10', '11:38:03', 'in', null, 'FACE', '192.168.0.10', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('112', '101', null, '2020-09-10', '11:38:05', 'in', null, 'FACE', '192.168.0.10', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('114', '101', null, '2020-09-10', '11:38:07', 'in', null, 'FACE', '192.168.0.10', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('116', '101', null, '2020-09-10', '11:41:24', 'in', null, 'FACE', '192.168.0.10', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('118', '101', null, '2020-09-10', '14:00:37', 'in', null, 'FACE', '192.168.0.10', 'staff', '林福', '080804E5852B71E9E3394E506740612B60B34A084EE7006D', null, null, null);
INSERT INTO `v_d_inout` VALUES ('120', '101', null, '2020-09-11', '13:23:07', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('122', '101', null, '2020-09-10', '14:03:42', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('124', '101', null, '2020-09-10', '14:24:45', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('126', '101', null, '2020-09-10', '14:24:46', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('128', '101', null, '2020-09-10', '14:24:49', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('130', '101', null, '2020-09-10', '14:24:55', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('132', '101', null, '2020-09-10', '14:25:06', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('134', '101', null, '2020-09-10', '14:25:11', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('136', '101', null, '2020-09-10', '14:25:15', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('138', '101', null, '2020-09-10', '14:25:21', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('140', '101', null, '2020-09-10', '15:27:14', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('142', '101', null, '2020-09-10', '13:27:15', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('144', '101', null, '2020-09-11', '13:51:12', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('146', '101', null, '2020-09-11', '18:01:08', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('148', '101', null, '2020-09-14', '13:46:02', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('150', '101', null, '2020-09-14', '14:54:48', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('152', '101', null, '2020-09-14', '14:54:55', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('154', '101', null, '2020-09-14', '14:54:59', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('156', '101', null, '2020-09-14', '14:54:59', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('158', '101', null, '2020-09-14', '14:55:04', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('160', '101', null, '2020-09-14', '14:55:04', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('162', '101', null, '2020-09-14', '14:55:08', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('164', '101', null, '2020-09-14', '14:55:08', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('166', '101', null, '2020-09-14', '14:55:19', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('168', '101', null, '2020-09-14', '14:57:10', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('170', '101', null, '2020-09-14', '14:57:44', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('172', '101', null, '2020-09-14', '15:28:09', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('174', '101', null, '2020-09-14', '15:28:10', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('176', '101', null, '2020-09-15', '11:15:11', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊1', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('178', '101', null, '2020-09-15', '11:16:15', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊1', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('180', '101', null, '2020-09-15', '14:17:34', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊1', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('182', '101', null, '2020-09-15', '14:17:37', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊1', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('184', '101', null, '2020-09-15', '14:54:46', 'in', null, 'FACE', '192.168.0.10', 'visitor', '雷磊1', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('186', '101', null, '2020-09-15', '18:08:59', 'in', null, 'FACE', '192.168.0.10', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('188', '101', null, '2020-09-15', '18:08:56', 'in', null, 'FACE', '192.168.0.10', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('190', '101', null, '2020-09-15', '18:08:56', 'in', null, 'FACE', '192.168.0.10', 'visitor', '陈乃亮', 'F39A27843EB5426502F323BD0F85D51BB878422DD6D94DE6', null, null, null);
INSERT INTO `v_d_inout` VALUES ('192', '101', null, '2020-09-15', '18:08:59', 'in', null, 'FACE', '192.168.0.10', 'visitor', '陈乃亮', 'F39A27843EB5426502F323BD0F85D51BB878422DD6D94DE6', null, null, null);
INSERT INTO `v_d_inout` VALUES ('194', '101', null, '2020-09-16', '08:39:23', 'in', null, 'FACE', '192.168.0.10', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('196', 'ifc', null, '2020-09-16', '11:15:35', 'in', null, 'FACE', '192.168.0.10', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('198', 'ifc', null, '2020-09-17', '10:02:58', 'in', null, 'FACE', '192.168.0.10', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('200', '101', null, '2020-09-17', '10:14:10', 'in', null, 'FACE', '192.168.0.10', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('202', '101', null, '2020-09-17', '10:14:48', 'in', null, 'FACE', '192.168.0.10', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('204', '101', null, '2020-09-17', '10:15:23', 'in', null, 'FACE', '192.168.0.10', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('206', '101', null, '2020-09-17', '10:15:32', 'in', null, 'FACE', '192.168.0.10', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('208', '101', null, '2020-09-17', '10:15:37', 'in', null, 'FACE', '192.168.0.10', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);
INSERT INTO `v_d_inout` VALUES ('210', '101', null, '2020-09-17', '10:50:35', 'in', null, 'FACE', '192.168.0.10', 'staff', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', null, null, null);

-- ----------------------------
-- Table structure for v_dept
-- ----------------------------
DROP TABLE IF EXISTS `v_dept`;
CREATE TABLE `v_dept` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '原company_section表',
  `org_id` bigint(11) DEFAULT NULL COMMENT '大楼号',
  `code` varchar(60) DEFAULT NULL COMMENT '部门编码',
  `dept_name` varchar(60) DEFAULT NULL COMMENT '部门名称',
  `floor` varchar(30) DEFAULT NULL COMMENT '楼层',
  `manage_user_id` bigint(11) DEFAULT NULL COMMENT '部门主管id 去dept_user 查询',
  `addr` varchar(100) DEFAULT NULL COMMENT '详细地址',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of v_dept
-- ----------------------------
INSERT INTO `v_dept` VALUES ('2', '1', '1112', '测试部', '21', null, null);
INSERT INTO `v_dept` VALUES ('7', '92', '9d5d1b1c-3df8-4757-9421-9fbd1a874840', '测试部', '21', null, null);
INSERT INTO `v_dept` VALUES ('9', '94', 'd2e8d620-6551-4b1d-b17e-ce6a133fe27d', 'test01', '24', null, null);
INSERT INTO `v_dept` VALUES ('10', '92', '3564e0c3-af7b-4c27-935c-c3fe4a465cdb', '技术部', '21', null, null);

-- ----------------------------
-- Table structure for v_dept_user
-- ----------------------------
DROP TABLE IF EXISTS `v_dept_user`;
CREATE TABLE `v_dept_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '员工管理表--原company_user表',
  `deptId` bigint(20) DEFAULT NULL COMMENT '部门id',
  `userNo` varchar(20) DEFAULT NULL COMMENT '工号',
  `realName` varchar(60) DEFAULT NULL COMMENT '员工姓名',
  `createDate` varchar(30) DEFAULT NULL COMMENT '创建日期yy:MM:dd',
  `roleType` varchar(60) DEFAULT NULL COMMENT '职位 manage 部门管理员 staff 员工 ',
  `status` varchar(60) DEFAULT NULL COMMENT '状态：确认:applySuc/未确认:applying/确认不通过:applyFail',
  `currentStatus` varchar(60) DEFAULT NULL COMMENT 'normal为正常，deleted为删除',
  `postId` bigint(20) DEFAULT NULL,
  `applyfailAnsaesn` varchar(500) DEFAULT NULL COMMENT '拒绝理由',
  `sex` varchar(10) DEFAULT NULL COMMENT '性别',
  `secucode` varchar(10) DEFAULT '0' COMMENT '是否涉密0可以被访问1不可访问',
  `authtype` varchar(10) DEFAULT '0' COMMENT '授权类型0为自己授权1不可授权2为本部门授权3全体公司授权',
  `phone` varchar(20) DEFAULT NULL COMMENT '员工电话',
  `idNO` varchar(100) DEFAULT NULL COMMENT '证件号 用密钥加密，取出来再解密',
  `isAuth` varchar(1) DEFAULT NULL COMMENT '是否实名 F:未实名 T：实名;N:审核中',
  `idHandleImgUrl` varchar(60) DEFAULT NULL COMMENT '手持证件照',
  `headImgUrl` varchar(60) DEFAULT 'user/headImg/z192.png' COMMENT '头像照片位置',
  `addr` varchar(255) DEFAULT NULL COMMENT '地址',
  `intime` varchar(30) DEFAULT NULL COMMENT '入职时间',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `token` varchar(100) DEFAULT NULL COMMENT 'token',
  `sysPwd` varchar(50) DEFAULT '670b14728ad9902aecba32e22fa4f6bd' COMMENT '默认密码',
  `deviceToken` varchar(100) DEFAULT NULL COMMENT '个推cid',
  `isOnlineApp` varchar(5) DEFAULT NULL COMMENT 'APP在线情况',
  `deviceType` varchar(20) DEFAULT NULL COMMENT '1--ios 2--andriod',
  `updateTime` varchar(20) DEFAULT NULL COMMENT '修改时间',
  `exp1` varchar(100) DEFAULT NULL COMMENT '扩展字段1',
  `exp2` varchar(100) DEFAULT NULL COMMENT '扩展字段2',
  `authDate` varchar(20) DEFAULT NULL,
  `isReceive` varchar(1) DEFAULT NULL COMMENT '是否已经发送完成 T :已发送  F:未发送',
  `cardNO` varchar(20) DEFAULT NULL COMMENT '卡号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of v_dept_user
-- ----------------------------
INSERT INTO `v_dept_user` VALUES ('8', '10', '1003', '陈乃亮', '2020-09-18 11:52:06', null, 'applySuc', 'normal', null, null, '1', '0', '0', '15005089512', 'F39A27843EB5426502F323BD0F85D51BB878422DD6D94DE6', 'T', '\\user\\cache\\1600311022798.jpg', 'user/headImg/z192.png', null, null, null, '0e2cf527-c5c4-4799-b227-85c3e2cd4474', '670b14728ad9902aecba32e22fa4f6bd', '32cd17c343e8478b3116b164b727070f', 'T', '1', null, null, null, '2020-08-07', 'T', null);
INSERT INTO `v_dept_user` VALUES ('16', '10', '1', '林福', '2020-09-17 10:48:34', null, 'applySuc', 'normal', null, null, '1', '0', '0', '18065988645', '080804E5852B71E9E3394E506740612B60B34A084EE7006D', 'T', '\\user\\cache\\1600310912648.jpg', 'user/headImg/z192.png', null, '2020-09-10', null, '0d24e906-a6d4-492b-897f-66ace4e7be04', '670b14728ad9902aecba32e22fa4f6bd', '17c44ea16d2d31af46bd82ba42f2dd0e', 'T', '1', null, null, null, null, 'T', null);
INSERT INTO `v_dept_user` VALUES ('24', '10', '2', '雷磊', '2020-09-17 09:54:08', null, 'applySuc', 'deleted', null, null, '1', '0', '0', '15627311700', '675015020A02928026724496CB31933EFCBF9FE91639C899', 'T', '\\user\\cache\\1600307644739.jpg', 'user/headImg/z192.png', null, '2020-09-15', null, '487b9519-3ce0-44dd-a75b-89ad1ea919b5', '670b14728ad9902aecba32e22fa4f6bd', '0bbb41034317cd0194cf9aa4ab4c62c2', 'T', '2', null, null, null, null, 'T', null);
INSERT INTO `v_dept_user` VALUES ('28', '2', '111', '说的', '2020-09-16 10:56:39', null, 'applySuc', 'deleted', null, null, '1', '0', '0', null, '7EF04189B0A09E16BF87CBB75D9F7ADC0D1685FFFA3DDCAF', null, '/user/cache/1600224998800.jpg', 'user/headImg/z192.png', null, null, null, null, '670b14728ad9902aecba32e22fa4f6bd', null, null, null, null, null, null, null, 'F', null);
INSERT INTO `v_dept_user` VALUES ('30', '10', '2', '雷磊', '2020-09-17 10:48:14', null, 'applySuc', 'normal', null, null, '1', '0', '0', '15627311700', '675015020A02928026724496CB31933EFCBF9FE91639C899', 'T', '\\user\\cache\\1600310893374.jpg', 'user/headImg/z192.png', null, '2020-09-17', null, null, '670b14728ad9902aecba32e22fa4f6bd', null, null, null, null, null, null, null, 'T', null);

-- ----------------------------
-- Table structure for v_device
-- ----------------------------
DROP TABLE IF EXISTS `v_device`;
CREATE TABLE `v_device` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `device_name` varchar(20) DEFAULT NULL COMMENT '设备名',
  `ip` varchar(20) DEFAULT NULL COMMENT 'IP地址',
  `type` varchar(5) DEFAULT NULL COMMENT '设备类型（SWJ,QRCODE,RELAY,FACE）',
  `gate` varchar(20) DEFAULT NULL COMMENT '控制的闸机',
  `floors` varchar(20) DEFAULT NULL COMMENT '控制的楼层',
  `createtime` varchar(20) DEFAULT NULL COMMENT '创建时间',
  `updateTime` varchar(20) DEFAULT NULL COMMENT '修改时间',
  `extra1` varchar(255) DEFAULT NULL,
  `extra2` varchar(255) DEFAULT NULL,
  `extra3` varchar(255) DEFAULT NULL,
  `pid` bigint(11) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `ping` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of v_device
-- ----------------------------
INSERT INTO `v_device` VALUES ('8', 'XS-0598', '192.168.0.10', 'FACE', 'BML', null, null, null, null, null, null, null, 'normal', '2');
INSERT INTO `v_device` VALUES ('10', 'KS-250', '192.168.0.88', 'FACE', 'BML', null, null, null, null, null, null, null, 'error', '0');
INSERT INTO `v_device` VALUES ('12', 'DS-K5671', '192.168.0.99', 'FACE', '101', null, null, null, null, null, null, null, 'error', '0');
INSERT INTO `v_device` VALUES ('16', 'DS-K5671', '192.168.10.99', 'FACE', '101', null, null, null, null, null, null, null, 'error', null);
INSERT INTO `v_device` VALUES ('20', 'DS-K5671', '192.168.2.188', 'FACE', 'BML', null, null, null, null, null, null, null, 'error', '0');
INSERT INTO `v_device` VALUES ('22', 'DS-K5671', '192.168.2.47', 'FACE', '101', null, null, null, null, null, null, null, 'error', null);
INSERT INTO `v_device` VALUES ('24', 'DS-K5671', '192.168.0.98', 'FACE', 'BML', null, null, null, null, null, null, null, 'normal', '0');
INSERT INTO `v_device` VALUES ('26', 'KS-250', '192.168.0.11', 'FACE', 'BML', null, null, null, null, null, null, null, 'error', '1');
INSERT INTO `v_device` VALUES ('32', 'DS-K5671', '192.168.10.80', 'FACE', '101', null, null, null, null, null, null, null, 'normal', '3');
INSERT INTO `v_device` VALUES ('34', '上位机', '192.168.10.29', 'SWJ', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `v_device` VALUES ('36', 'DS-K5671', '192.168.0.12', 'FACE', 'BML', null, null, null, null, null, null, null, 'error', '0');
INSERT INTO `v_device` VALUES ('38', 'DS-K5671', '192.168.0.13', 'FACE', '101', null, null, null, null, null, null, null, 'error', '0');
INSERT INTO `v_device` VALUES ('40', 'DS-K5671', '192.168.2.250', 'FACE', 'BML', null, null, null, null, null, null, null, 'error', '0');

-- ----------------------------
-- Table structure for v_dictionaries
-- ----------------------------
DROP TABLE IF EXISTS `v_dictionaries`;
CREATE TABLE `v_dictionaries` (
  `id` bigint(11) NOT NULL,
  `dictionaries_name` varchar(30) DEFAULT NULL,
  `dictionaries_key` varchar(50) DEFAULT NULL,
  `dictionaries_value` varchar(50) DEFAULT NULL,
  `updateTime` varchar(20) DEFAULT NULL COMMENT '修改时间',
  `extra1` varchar(255) DEFAULT NULL,
  `extra2` varchar(255) DEFAULT NULL,
  `extra3` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of v_dictionaries
-- ----------------------------
INSERT INTO `v_dictionaries` VALUES ('1', '1', '1', '1', null, '1', '1', '1');

-- ----------------------------
-- Table structure for v_error_log
-- ----------------------------
DROP TABLE IF EXISTS `v_error_log`;
CREATE TABLE `v_error_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `orgCode` varchar(60) NOT NULL COMMENT '大楼编码',
  `logContext` varchar(1000) NOT NULL COMMENT '错误原因',
  `errorType` varchar(100) DEFAULT NULL COMMENT '错误类型',
  `deviceId` varchar(60) DEFAULT NULL COMMENT '设备Id',
  `errorTime` varchar(20) DEFAULT NULL COMMENT '错误时间',
  `ext1` varchar(255) DEFAULT NULL COMMENT '拓展字段1',
  `ext2` varchar(255) DEFAULT NULL COMMENT '拓展字段2',
  `ext3` varchar(255) DEFAULT NULL COMMENT '拓展字段3',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `orgCode` (`orgCode`) USING BTREE COMMENT '大楼编号',
  KEY `errorType` (`errorType`) USING BTREE COMMENT '错误类型',
  KEY `orgCode,errorType` (`orgCode`,`errorType`) USING BTREE COMMENT '大楼编号与错误类型'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of v_error_log
-- ----------------------------

-- ----------------------------
-- Table structure for v_key
-- ----------------------------
DROP TABLE IF EXISTS `v_key`;
CREATE TABLE `v_key` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `swi_code` varchar(50) DEFAULT NULL COMMENT '上位机编码',
  `org_id` int(11) DEFAULT NULL COMMENT '大楼id',
  `mac` varchar(100) DEFAULT NULL COMMENT 'license对应的设备类型',
  `private_key` varchar(3000) DEFAULT NULL COMMENT '私钥',
  `public_key` varchar(3000) DEFAULT NULL COMMENT '公钥',
  `status` varchar(10) DEFAULT NULL COMMENT '状态',
  `begintime` varchar(20) DEFAULT NULL COMMENT '开始时间',
  `endtime` varchar(20) DEFAULT NULL COMMENT '结束时间',
  `createtime` varchar(20) DEFAULT NULL COMMENT '创建时间',
  `updateTime` varchar(20) DEFAULT NULL COMMENT '修改时间',
  `extra1` varchar(255) DEFAULT NULL,
  `extra2` varchar(255) DEFAULT NULL,
  `extra3` varchar(255) DEFAULT NULL,
  `license` varchar(3000) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of v_key
-- ----------------------------
INSERT INTO `v_key` VALUES ('2', '2598', null, '123', 'MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDPjlXHXLUyWqPpRqMcDwFguzv5JBhvUXyT0BVPKkc0PMW9Q14OQgXUgceAy9ETKMRCbOc0KjHZsjffo+V42yWqw07oNW+cTll1OhiQJzWat00tkjdyPz1dFfkXNvT3BfDIud6D/d7z3vQC+Smui15762xl11Cs/16l8Ekc9162f7xm2mOsWE6PUjP3mBauF+glrnwqzXINrfHf4+kfqpsufWw/ow3vG3eoGzfwjxL+pbTB1Yuah7g/YncLjkVVICLXNAMOoqFvhlsa6y6CI8FPI4Lcn1qiG8c0Byu81T9j5aG62RUnfvJ72FAPnsqyTpDbmo3wvpyetj05ivtESomhAgMBAAECggEAK7bTtDB/bUKP4TLiaadzZ9cnc3q5tsBX153szwadhpACKbGFnsUjCzXmOeczerCMXV0oeOEmLK6PfAe97e1iCowmE1wlzKrnxnvc7oeDj7lphN6V1Pciync1RkFp6JkUtIIJLo2Kppxfkjy+Haf212ynIO/vavBCE/r+ux4SkacGd3u8fdQshJpEYQbEVKhFBwZQnEl6kxC3F6vfPQW82TBYzXH5XMa3gD5//g3ABPpC3C2jXFi2PRZdnLX/0cVZbd4lMvSHSFwuNGmvNoalMlGEH5CvpYowzrEPQvpXbAUU11mgkqwjGzOeGYNa76O3Hb8+gbcp1PkywF1bgDEnAQKBgQDtqD+v869FLoP7r5+rDBT1if0fWtzcCXOkPKQVLcuYkABsL+5O1SKPylh3rn2wWG1r5NX+ZXo16w9YZa3PM04ww3H/Q7wD2DXTkIFB9EgAIXjLCb6eONSJ0MaX4udNbl/kfIeyw3EB4fBCIkCN+77n9nsqwbcYpQ56qBhTXz4gsQKBgQDfk1TDTwGI9wmpjxNhdh5wkmUDOHeghbvi1rLRrx89ZRnnyZ9KogLEdhpvdVqy9iahlR7d8PdtfJQOqMb8rJCVVvXewMLHanqG+Sh6zaljDfnUftPqkdyIw35Wwf44w34efolSlWAf1VXO4nRH5Zof5otE0tJCAIkbgfWZFZKz8QKBgE0zT2Tvyuq1Poh+t2ZP6WsNpR0PXhlYNKmQTVjX5IAtnwWF2GrhT3XYLD7MfuXPA9R9pIocGsPzFKwJc5mQOAEdLCXYsfWpwWp1UZfEK7NnLB8AjlxFZ71RBYIHYk3D4gCN92K5nDcZVvuUttWoho5BgicUgE7QJ1Du0+AZW0wxAoGAYODIg/ECK0VQjIaQ0VSLkB//YUvgmlAYwAxkY/PMR6A9f3Cgc4iMRnEet1lUueNrjey9+VHnBSxMQ6Xiw65K4EtUMve37w02lrH0VnIzidaOgQOL9ELMGv8LCbD55cLgLXqLKpvLkT+x9PmvhU/6XwRZKLNppGvy/VZtA66TaSECgYEAtdl+hREq5iQslnGx5zSnIIwWbiRqQtgQapul0rxhJkjzqTnWfOG4r9IurXyV7xcRNex3hmYcLKN+wXyjoqaCPJs70I29zZUITRzyIxMwEsSqWGeTaLtXxT/EqRyKdLY4gLISPL2qy2l9kG1ew264ndwKVlXJJoAqiuKv96zpum4=', 'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAz45Vx1y1Mlqj6UajHA8BYLs7+SQYb1F8k9AVTypHNDzFvUNeDkIF1IHHgMvREyjEQmznNCox2bI336PleNslqsNO6DVvnE5ZdToYkCc1mrdNLZI3cj89XRX5Fzb09wXwyLneg/3e8970Avkprotee+tsZddQrP9epfBJHPdetn+8ZtpjrFhOj1Iz95gWrhfoJa58Ks1yDa3x3+PpH6qbLn1sP6MN7xt3qBs38I8S/qW0wdWLmoe4P2J3C45FVSAi1zQDDqKhb4ZbGusugiPBTyOC3J9aohvHNAcrvNU/Y+WhutkVJ37ye9hQD57Ksk6Q25qN8L6cnrY9OYr7REqJoQIDAQAB', null, null, null, '2020-08-07 15:17:38', null, null, null, null, 'pIMT+ylQc39fgkrl1W3unXBmTyBE/iWZHBRP1PX0u7QbrajdJpwrQPESehjmQkrYI7A1KE673pdJVi7tQVt2IPszTU2fJMZjOCNoiuYRcCcSa7tB9xBXtCS2LwSVXDjMDnhQtBYo0R7h9KSbtz7HATqvfn2y94T7vo3h3U3/JRUitJXzUcV0IMeOiFDy/6u9PlaScChqU1GdnmDdlW6gTfRRH4jGClO85Z7lnkyjqeGvAlLRUZYMXJbMizhWvfP1WOaL+VIlFSjQnr62CayUWwCER/Ly0i7p/Qt1gat9J8RF19KAM8v9H2VXQHczYnmzb+NWtV0ZaZHmajJlt1Go+Q==');

-- ----------------------------
-- Table structure for v_kq_attendrule
-- ----------------------------
DROP TABLE IF EXISTS `v_kq_attendrule`;
CREATE TABLE `v_kq_attendrule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '考勤规则',
  `companyID` bigint(50) DEFAULT NULL COMMENT '公司ID',
  `orgCode` varchar(60) DEFAULT NULL COMMENT '大楼编码',
  `punchCount` int(11) DEFAULT NULL COMMENT '打卡次数',
  `mornTime` varchar(60) DEFAULT NULL COMMENT '早上上班时间',
  `mornOffTime` varchar(60) DEFAULT NULL COMMENT '最早打卡时间',
  `afterTime` varchar(60) DEFAULT NULL COMMENT '提前多长时间不算早退',
  `afterOffTime` varchar(60) DEFAULT NULL COMMENT '下午下班时间',
  `miniOverTime` varchar(60) DEFAULT NULL COMMENT '晚上最迟打卡时间',
  `lateTime` varchar(60) DEFAULT NULL COMMENT '迟到多长时间算迟到',
  `attendNote` varchar(200) DEFAULT NULL COMMENT '考勤备注',
  `operTime` varchar(200) DEFAULT NULL COMMENT '操作时间',
  `workHours` varchar(200) DEFAULT NULL COMMENT '工作日工时',
  `workOverTime` varchar(200) DEFAULT NULL COMMENT '工作日几点打卡算加班',
  `holidayHours` varchar(200) DEFAULT NULL COMMENT '节假日工时',
  `holidayOverTime` varchar(200) DEFAULT NULL COMMENT '节假日几点打卡算加班',
  `ext1` varchar(50) DEFAULT NULL COMMENT '可允许最晚打卡时间上午',
  `ext2` varchar(50) DEFAULT NULL COMMENT '可允许最晚打卡时间中午',
  `ext3` varchar(50) DEFAULT NULL COMMENT '可允许最迟打卡时间',
  `attendType` varchar(50) DEFAULT NULL COMMENT '考勤类型0：门禁1：考勤',
  `mornCloseTime` varchar(50) DEFAULT NULL COMMENT '上午下班时间',
  `afternoonCloseTime` varchar(50) DEFAULT NULL COMMENT '下午上班时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of v_kq_attendrule
-- ----------------------------

-- ----------------------------
-- Table structure for v_local_auth
-- ----------------------------
DROP TABLE IF EXISTS `v_local_auth`;
CREATE TABLE `v_local_auth` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userId` bigint(20) DEFAULT NULL COMMENT '用户id',
  `realName` varchar(20) DEFAULT NULL COMMENT '用户真实姓名',
  `idNo` varchar(50) DEFAULT NULL COMMENT '身份证号',
  `idHandleImgUrl` varchar(60) DEFAULT NULL COMMENT '手持证件照',
  `authDate` varchar(20) DEFAULT NULL COMMENT '实名日期时间 yyyy-MM-dd hhmmss',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `userId` (`userId`,`idNo`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2061 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of v_local_auth
-- ----------------------------
INSERT INTO `v_local_auth` VALUES ('2054', '5', '徐素芬', '593FBED69D967F078BD146D46679B7CEF7D686405164F4EB', '/usr/java/img/user/5/1579419719715.jpg', '2020-01-19 15:42:03');
INSERT INTO `v_local_auth` VALUES ('2056', '8', '陈乃亮', 'F39A27843EB5426502F323BD0F85D51BB878422DD6D94DE6', 'D:/usr/java/img/user81596784462356.jpg', '2020-08-07 15:13:46');
INSERT INTO `v_local_auth` VALUES ('2058', '20', '林福', '080804E5852B71E9E3394E506740612B60B34A084EE7006D', 'D:/usr/java/img/user201597633805284.jpg', '2020-08-17 11:09:24');
INSERT INTO `v_local_auth` VALUES ('2060', '12', '雷磊', '675015020A02928026724496CB31933EFCBF9FE91639C899', 'D:/usr/java/img/user121599551591922.jpg', '2020-09-08 15:52:21');

-- ----------------------------
-- Table structure for v_news
-- ----------------------------
DROP TABLE IF EXISTS `v_news`;
CREATE TABLE `v_news` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `newsDate` varchar(20) DEFAULT NULL COMMENT '日期',
  `newsName` varchar(60) DEFAULT NULL COMMENT '标题',
  `newsDetail` varchar(255) DEFAULT NULL COMMENT '简单描述',
  `newsImageUrl` varchar(255) DEFAULT NULL COMMENT '图片(图片服务器)',
  `newsUrl` varchar(255) DEFAULT NULL COMMENT '跳转URL',
  `newsStatus` varchar(60) DEFAULT NULL COMMENT 'normal:正常  disable:禁止',
  `updateTime` varchar(20) DEFAULT NULL COMMENT '修改时间',
  `headline` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of v_news
-- ----------------------------
INSERT INTO `v_news` VALUES ('14', '2020-08-17 16:41:26', '测试新闻', '测试', 'news/1597824260307.jpeg', '111', 'normal', '2020-08-19 16:15:33', 'F');

-- ----------------------------
-- Table structure for v_notice
-- ----------------------------
DROP TABLE IF EXISTS `v_notice`;
CREATE TABLE `v_notice` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `orgId` bigint(20) DEFAULT NULL COMMENT '所属机构',
  `relationNo` varchar(255) DEFAULT NULL COMMENT '机构关联 方便查看无限下级',
  `noticeTitle` varchar(60) DEFAULT NULL COMMENT '标题',
  `content` varchar(256) DEFAULT NULL COMMENT '内容',
  `createDate` varchar(20) DEFAULT NULL COMMENT '建立日期 yyyy-MM-dd',
  `createTime` varchar(8) DEFAULT NULL COMMENT '建立时间 HH:mm:ss',
  `cstatus` varchar(60) DEFAULT NULL COMMENT '状态 normal:正常 disable:失效',
  `companyId` bigint(20) DEFAULT NULL COMMENT '公司id',
  `updateTime` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of v_notice
-- ----------------------------
INSERT INTO `v_notice` VALUES ('18', null, null, '朋悦比邻更新啦', '朋悦比邻更新啦啦啦啦', '2020-08-17', '14:13:32', 'normal', null, '2020-08-17 14:27:35');
INSERT INTO `v_notice` VALUES ('20', null, null, '朋悦比邻日常维护', '朋悦比邻日常维护啊啊啊啊啊', '2020-08-17', '14:14:11', 'normal', null, null);

-- ----------------------------
-- Table structure for v_org
-- ----------------------------
DROP TABLE IF EXISTS `v_org`;
CREATE TABLE `v_org` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '大楼',
  `org_code` varchar(60) DEFAULT NULL COMMENT '大楼编码',
  `org_name` varchar(128) DEFAULT NULL COMMENT '大楼名',
  `realName` varchar(60) DEFAULT NULL COMMENT '创建人姓名---主管人',
  `phone` varchar(60) DEFAULT NULL COMMENT '手机',
  `createDate` varchar(60) DEFAULT NULL COMMENT '创建时间',
  `staff_access_type` varchar(10) CHARACTER SET utf8 DEFAULT NULL COMMENT '员工通行方式 0：人脸识别1：二维码 2：人脸or二维码',
  `visitor_access_type` varchar(10) DEFAULT NULL COMMENT '访客通行方式 0：人脸识别1：二维码 2：人脸or二维码',
  `share_access_type` varchar(10) DEFAULT NULL COMMENT '共享通行方式 0：人脸识别1：二维码 2：人脸or二维码',
  `update_time` varchar(20) DEFAULT NULL COMMENT '修改时间',
  `ext1` varchar(255) DEFAULT NULL,
  `ext2` varchar(255) DEFAULT NULL,
  `approle` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `org_name_index` (`org_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=97 DEFAULT CHARSET=gbk ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of v_org
-- ----------------------------
INSERT INTO `v_org` VALUES ('1', 'TEST', '朋悦比邻', '朋悦比邻', '12345678901', '2020-01-21 14:19:01', '0', '1', '1', null, null, null, null);
INSERT INTO `v_org` VALUES ('92', '101', 'GQ1HL', null, null, '2020-01-21 14:40:53', '0', '1', '1', null, null, null, null);
INSERT INTO `v_org` VALUES ('94', '105', '小松安信科技', null, null, '2020-04-28 11:24:33', '0', '1', '1', null, null, null, null);
INSERT INTO `v_org` VALUES ('96', '201', '大大', null, null, '2020-08-07 10:08:17', '2', '2', '1', null, null, null, null);

-- ----------------------------
-- Table structure for v_out_visitor
-- ----------------------------
DROP TABLE IF EXISTS `v_out_visitor`;
CREATE TABLE `v_out_visitor` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '企业版外部映射',
  `userCode` varchar(255) DEFAULT NULL COMMENT '企业内部员工唯一标识，根据某种算法获取，如身份证截取前6位后6位+手机号+姓名',
  `realName` varchar(255) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(255) DEFAULT NULL COMMENT '手机号码',
  `isAuth` varchar(10) DEFAULT NULL COMMENT '是否实人认证 T F',
  `idHandleImgUrl` varchar(255) DEFAULT NULL COMMENT '手持证件地址',
  `createTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `ext1` varchar(255) DEFAULT NULL COMMENT '拓展字段1',
  `ext2` varchar(255) DEFAULT NULL COMMENT '拓展字段2',
  `ext3` varchar(255) DEFAULT NULL COMMENT '拓展字段3',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of v_out_visitor
-- ----------------------------
INSERT INTO `v_out_visitor` VALUES ('1', 'test', 'test', '18150797777', 'T', 'user/125/1574909369276.jpg', '2020-01-14 10:44:29', null, null, null);

-- ----------------------------
-- Table structure for v_params
-- ----------------------------
DROP TABLE IF EXISTS `v_params`;
CREATE TABLE `v_params` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '系统参数',
  `paramName` varchar(60) DEFAULT NULL COMMENT '参数名',
  `paramText` varchar(255) DEFAULT NULL COMMENT '参数用法',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `params_name_index` (`paramName`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of v_params
-- ----------------------------
INSERT INTO `v_params` VALUES ('1', 'errorInputSyspwdWaitTime', '60', '错误输入系统密码的等待时间（单位：分钟）');
INSERT INTO `v_params` VALUES ('2', 'errorInputPaypwdWaitTime', '3', '错误输入支付密码的等待时间（单位：分钟）');
INSERT INTO `v_params` VALUES ('3', 'apiAuthCheckRedisDbIndex', '2', 'API中用户Token,是否实名（isAuth）,用户最新公告在缓存中存放的地址');
INSERT INTO `v_params` VALUES ('4', 'apiAuthCheckRedisExpire', '720', 'API中用户Token,是否实名（isAuth）,用户最新公告在缓存中的过期时间（单位：分钟）');
INSERT INTO `v_params` VALUES ('5', 'maxErrorInputSyspwdLimit', '5', '一定时间内允许连续输入错误系统密码的次数');
INSERT INTO `v_params` VALUES ('6', 'maxErrorInputPaypwdLimit', '5', '一定时间内允许连续输入错误支付密码的次数');
INSERT INTO `v_params` VALUES ('7', 'imageServerApiUrl', 'http://121.36.45.232:8081/goldccm-imgServer/goldccm/image/gainData', '图片服务器接口地址');
INSERT INTO `v_params` VALUES ('8', 'imageServerUrl', 'http://121.36.45.232:8098/imgserver/', '图片服务器访问地址');
INSERT INTO `v_params` VALUES ('9', 'imageNewsApiUrl', 'http://121.36.45.232:8081/goldccm-imgServer/goldccm/news/uploadImage', '新闻/信用卡审核图片服务器接口地址');
INSERT INTO `v_params` VALUES ('10', 'imageClientPath', '/usr/java/qrcode/image', '图片中转地址');
INSERT INTO `v_params` VALUES ('11', 'verifyTermOfValidity', '1', '实名认证有效期（年）');
INSERT INTO `v_params` VALUES ('12', 'userIdentityUrl    ', 'http://47.106.82.190/wisdom/identity/fastIdentify', '用户实名验证接口');
INSERT INTO `v_params` VALUES ('13', 'fileClientPath', '/usr/java/excelfile', '文件存储地址');
INSERT INTO `v_params` VALUES ('14', 'apiNewAuthCheckRedisDbIndex', '35', '新API中用户Token,是否实名（isAuth）,用户最新公告在缓存中存放的地址');
INSERT INTO `v_params` VALUES ('15', 'wxOpenIdCheckRedisDbindex', '36', '存放微信用户关注公众号后的openId在缓存中的地址');
INSERT INTO `v_params` VALUES ('16', 'leaveRecordSequence', '61', '请假编号自定义，每天清零');

-- ----------------------------
-- Table structure for v_sys_config
-- ----------------------------
DROP TABLE IF EXISTS `v_sys_config`;
CREATE TABLE `v_sys_config` (
  `id` bigint(11) NOT NULL,
  `function_name` varchar(20) DEFAULT NULL COMMENT '对应的子系统名称',
  `status` varchar(10) DEFAULT NULL COMMENT '状态（是否启动）',
  `createtime` varchar(20) DEFAULT NULL COMMENT '创建时间',
  `extra1` varchar(255) DEFAULT NULL,
  `extra2` varchar(255) DEFAULT NULL,
  `extra3` varchar(255) DEFAULT NULL,
  `trueName` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of v_sys_config
-- ----------------------------

-- ----------------------------
-- Table structure for v_sys_user
-- ----------------------------
DROP TABLE IF EXISTS `v_sys_user`;
CREATE TABLE `v_sys_user` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(11) unsigned DEFAULT NULL,
  `username` varchar(20) DEFAULT NULL COMMENT '登录名',
  `password` varchar(50) DEFAULT NULL COMMENT '登录密码',
  `true_name` varchar(20) DEFAULT NULL COMMENT '真实姓名',
  `tel` varchar(20) DEFAULT NULL COMMENT '电话',
  `is_del` varchar(5) DEFAULT NULL COMMENT '是否删除',
  `createtime` varchar(20) DEFAULT NULL COMMENT '创建时间',
  `extra1` varchar(255) DEFAULT NULL,
  `extra2` varchar(255) DEFAULT NULL,
  `extra3` varchar(255) DEFAULT NULL,
  `role_id` bigint(20) DEFAULT NULL,
  `token` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of v_sys_user
-- ----------------------------
INSERT INTO `v_sys_user` VALUES ('1', null, 'admin', 'e10adc3949ba59abbe56e057f20f883e', null, null, null, null, null, null, null, '1', '14b90949-4352-4a2a-9330-40c48bb56c9b');
INSERT INTO `v_sys_user` VALUES ('9', '1', 'qqq', '02a05c6e278d3e19afaca4f3f7cf47d9', 'qqqqqq', '13536866245', null, '2020-03-26 18:03:33', null, null, null, null, null);

-- ----------------------------
-- Table structure for v_user_auth
-- ----------------------------
DROP TABLE IF EXISTS `v_user_auth`;
CREATE TABLE `v_user_auth` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(11) DEFAULT NULL COMMENT '父节点id',
  `auth_name` varchar(30) DEFAULT NULL COMMENT '权限名称',
  `createtime` varchar(20) DEFAULT NULL COMMENT '创建时间',
  `description` varchar(50) DEFAULT NULL COMMENT '描述',
  `menu_url` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of v_user_auth
-- ----------------------------
INSERT INTO `v_user_auth` VALUES ('2', '9', '用户管理', null, null, 'SysUser');
INSERT INTO `v_user_auth` VALUES ('3', '9', '角色管理', null, null, 'SysRole');
INSERT INTO `v_user_auth` VALUES ('9', '0', '系统管理', '2020-08-07 17:10:14', 'el-icon-setting', '3');
INSERT INTO `v_user_auth` VALUES ('10', '9', '权限管理', null, null, 'SysAuth');
INSERT INTO `v_user_auth` VALUES ('11', '9', '参数配置', null, null, 'SysConfig');
INSERT INTO `v_user_auth` VALUES ('12', '9', '秘钥管理', null, null, 'key');
INSERT INTO `v_user_auth` VALUES ('13', '9', 'APP管理', null, null, 'appPower');
INSERT INTO `v_user_auth` VALUES ('14', '9', '公告管理', null, null, 'notice');
INSERT INTO `v_user_auth` VALUES ('15', '9', '新闻管理', null, null, 'news');
INSERT INTO `v_user_auth` VALUES ('16', '9', '广告管理', null, null, 'ad');
INSERT INTO `v_user_auth` VALUES ('17', '0', '企业管理', null, 'el-icon-s-custom', '3-2');
INSERT INTO `v_user_auth` VALUES ('18', '17', '楼宇管理', null, null, 'floorControl');
INSERT INTO `v_user_auth` VALUES ('19', '17', '部门管理', null, null, 'dept');
INSERT INTO `v_user_auth` VALUES ('20', '17', '员工管理', null, null, 'deptUser');
INSERT INTO `v_user_auth` VALUES ('21', '17', 'VIP管理', null, null, 'vip');
INSERT INTO `v_user_auth` VALUES ('22', '0', '数据分析', null, 'el-icon-s-data', '3-3');
INSERT INTO `v_user_auth` VALUES ('24', '22', '访客管理', null, null, 'visitor');
INSERT INTO `v_user_auth` VALUES ('25', '22', '通行记录', null, null, 'inOut');

-- ----------------------------
-- Table structure for v_user_friend
-- ----------------------------
DROP TABLE IF EXISTS `v_user_friend`;
CREATE TABLE `v_user_friend` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userId` bigint(20) DEFAULT NULL COMMENT '用户id',
  `friendId` bigint(20) DEFAULT NULL COMMENT '用户对应的好友id',
  `applyType` int(10) DEFAULT NULL COMMENT '1--通过 0,null---不通过 2已删除',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `remarkMsg` varchar(255) DEFAULT NULL,
  `authentication` varchar(255) DEFAULT NULL,
  `createTime` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=63455 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of v_user_friend
-- ----------------------------
INSERT INTO `v_user_friend` VALUES ('1', '6', '2', '1', '张三', null, null, null);
INSERT INTO `v_user_friend` VALUES ('4', '2', '3', '2', '唐龙辉', null, null, null);
INSERT INTO `v_user_friend` VALUES ('6', '7', '8', '0', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('9', '23', '2', '0', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('11', '6', '3', '1', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('12', '6', '4', '1', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('13', '6', '5', '0', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('14', '6', '7', '1', '黄荣杰', null, null, null);
INSERT INTO `v_user_friend` VALUES ('15', '6', '9', '0', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('16', '6', '10', '0', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('19', '24', '7', '1', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('20', '24', '3', '1', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('21', '7', '6', '1', '黄俊', null, null, null);
INSERT INTO `v_user_friend` VALUES ('22', '26', '9', '0', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('23', '26', '2', '1', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('25', '7', '4', '1', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('27', '7', '3', '1', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('29', '3', '9', '0', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('32', '26', '7', '1', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('37', '4', '2', '1', '嘉嘉', null, null, null);
INSERT INTO `v_user_friend` VALUES ('40', '3', '4', '1', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('45', '2', '4', '1', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('47', '2', '35', '0', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('49', '24', '2', '0', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('50', '26', '35', '0', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('51', '26', '4', '1', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('53', '2', '6', '1', '黄俊', null, null, null);
INSERT INTO `v_user_friend` VALUES ('54', '2', '55', '0', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('60', '2', '13', '0', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('61', '3', '2', '2', '鈡嘉嘉', null, null, null);
INSERT INTO `v_user_friend` VALUES ('62', '67', '7', '1', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('63335', '4', '17', '1', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('63336', '3', '35', '0', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('63337', '153', '7', '1', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('63338', '177', '122', '0', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('63339', '177', '175', '0', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('63340', '177', '181', '0', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('63341', '157', '156', '0', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('63345', '2', '125', '1', '2444', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63346', '2', '26', '1', '你好', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63348', '3', '7', '1', '？1', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63349', '3', '6', '1', '！', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63350', '3', '24', '1', '‘', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63351', '125', '10', '1', '2333', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63353', '13', '10', '1', '', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63356', '3', '2', '2', '本人', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63357', '2', '64', '0', '', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63358', '2', '157', '0', '', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63359', '2', '136', '0', '', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63361', '2', '63', '0', '', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63362', '2', '10', '1', '你好', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63365', '4', '6', '1', '黄俊', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63366', '4', '7', '1', '黄荣杰', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63367', '4', '3', '1', '素芬', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63368', '4', '26', '1', '黄荣杰', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63369', '4', '10', '1', '黄文坤', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63370', '6', '183', '0', '', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63371', '6', '152', '0', '', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63373', '379', '7', null, null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('63374', '379', '153', null, null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('63375', '507', '7', null, null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('63376', '429', '428', null, null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('63377', '429', '431', null, null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('63378', '2', '37', '0', '', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63379', '7', '12', null, null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('63380', '17', '3', '1', '', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63381', '17', '4', '1', '，', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63382', '3', '17', '1', '，', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63383', '7', '26', '1', '黄荣杰', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63384', '7', '153', '1', '宁振锋', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63385', '3', '5', '0', '', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63386', '7', '67', '1', '翁国泉-厦门鼎祥投资', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63387', '7', '24', '1', '李学斌-IOS', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63388', '2', '7', '0', '', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63401', '10', '125', '2', 'hh', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63402', '126', '125', '1', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('63403', '125', '126', '1', '1', null, null, null);
INSERT INTO `v_user_friend` VALUES ('63404', '126', '125', '0', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('63405', '2', '3', '2', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('63406', '2', '4', '2', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('63408', '2', '3', '2', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('63409', '2', '5', '0', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('63410', '5', '2', '0', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('63411', '2', '4', '2', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('63412', '2', '4', '2', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('63413', '2', '3', '2', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('63416', '1', '2', '1', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('63417', '2', '1', '1', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('63418', '1', '2', '1', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('63419', '2', '1', '1', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('63424', '8', '8', null, null, null, '11111', null);
INSERT INTO `v_user_friend` VALUES ('63426', '8', '20', null, null, '22222', '111111', null);
INSERT INTO `v_user_friend` VALUES ('63428', '8', '20', null, null, '22222', '111111', null);
INSERT INTO `v_user_friend` VALUES ('63430', '8', '20', null, null, '22222', '111111', null);
INSERT INTO `v_user_friend` VALUES ('63432', '8', '20', null, null, '22222', '111111', null);
INSERT INTO `v_user_friend` VALUES ('63434', '8', '20', null, null, '22222', '111111', null);
INSERT INTO `v_user_friend` VALUES ('63436', '8', '20', null, null, '22222', '111111', null);
INSERT INTO `v_user_friend` VALUES ('63438', '8', '20', null, null, '22222', '111111', null);
INSERT INTO `v_user_friend` VALUES ('63440', '8', '20', null, null, '22222', '111111', null);
INSERT INTO `v_user_friend` VALUES ('63442', '8', '20', null, null, '22222', '111111', null);
INSERT INTO `v_user_friend` VALUES ('63444', '8', '20', null, null, '22222', '111111', null);
INSERT INTO `v_user_friend` VALUES ('63446', '8', '20', null, null, '22222', '111111', null);
INSERT INTO `v_user_friend` VALUES ('63448', '8', '20', null, null, '22222', '111111', null);
INSERT INTO `v_user_friend` VALUES ('63450', '20', '8', null, null, null, '1111', null);
INSERT INTO `v_user_friend` VALUES ('63452', '8', '1', '0', null, null, null, null);
INSERT INTO `v_user_friend` VALUES ('63454', '1', '8', '0', null, null, null, null);

-- ----------------------------
-- Table structure for v_user_key
-- ----------------------------
DROP TABLE IF EXISTS `v_user_key`;
CREATE TABLE `v_user_key` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `workKey` varchar(60) DEFAULT NULL COMMENT '密钥的key',
  `cstatus` varchar(60) DEFAULT NULL COMMENT '状态，normal正常，disable:禁用',
  `createDate` varchar(30) DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of v_user_key
-- ----------------------------
INSERT INTO `v_user_key` VALUES ('1', 'iB4drRzSrC', 'normal', '2017-5-23');

-- ----------------------------
-- Table structure for v_user_role
-- ----------------------------
DROP TABLE IF EXISTS `v_user_role`;
CREATE TABLE `v_user_role` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(11) unsigned DEFAULT NULL,
  `role_name` varchar(20) DEFAULT NULL COMMENT '角色名称',
  `createtime` varchar(30) DEFAULT NULL COMMENT '创建时间',
  `description` varchar(50) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of v_user_role
-- ----------------------------
INSERT INTO `v_user_role` VALUES ('1', null, '超级管理员', null, 'admin');
INSERT INTO `v_user_role` VALUES ('16', '1', '超管1', '2020-03-26 17:33:21', '超管1');
INSERT INTO `v_user_role` VALUES ('17', '1', '普通角色', '2020-08-05 14:08:13', '普普通');
INSERT INTO `v_user_role` VALUES ('18', '1', '超管2', '2020-08-07 10:16:43', '超管2');

-- ----------------------------
-- Table structure for v_user_role_auth
-- ----------------------------
DROP TABLE IF EXISTS `v_user_role_auth`;
CREATE TABLE `v_user_role_auth` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(11) DEFAULT NULL,
  `auth_id` bigint(11) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=311 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of v_user_role_auth
-- ----------------------------
INSERT INTO `v_user_role_auth` VALUES ('21', '1', '2');
INSERT INTO `v_user_role_auth` VALUES ('22', '1', '3');
INSERT INTO `v_user_role_auth` VALUES ('28', '1', '9');
INSERT INTO `v_user_role_auth` VALUES ('29', '1', '10');
INSERT INTO `v_user_role_auth` VALUES ('30', '1', '11');
INSERT INTO `v_user_role_auth` VALUES ('31', '1', '12');
INSERT INTO `v_user_role_auth` VALUES ('32', '1', '13');
INSERT INTO `v_user_role_auth` VALUES ('33', '1', '14');
INSERT INTO `v_user_role_auth` VALUES ('34', '1', '22');
INSERT INTO `v_user_role_auth` VALUES ('36', '1', '24');
INSERT INTO `v_user_role_auth` VALUES ('39', '1', '15');
INSERT INTO `v_user_role_auth` VALUES ('40', '1', '16');
INSERT INTO `v_user_role_auth` VALUES ('41', '1', '17');
INSERT INTO `v_user_role_auth` VALUES ('42', '1', '18');
INSERT INTO `v_user_role_auth` VALUES ('43', '1', '19');
INSERT INTO `v_user_role_auth` VALUES ('44', '1', '20');
INSERT INTO `v_user_role_auth` VALUES ('45', '1', '21');
INSERT INTO `v_user_role_auth` VALUES ('170', '18', '9');
INSERT INTO `v_user_role_auth` VALUES ('172', '18', '2');
INSERT INTO `v_user_role_auth` VALUES ('174', '18', '3');
INSERT INTO `v_user_role_auth` VALUES ('176', '18', '10');
INSERT INTO `v_user_role_auth` VALUES ('178', '18', '11');
INSERT INTO `v_user_role_auth` VALUES ('180', '18', '12');
INSERT INTO `v_user_role_auth` VALUES ('182', '18', '13');
INSERT INTO `v_user_role_auth` VALUES ('184', '18', '14');
INSERT INTO `v_user_role_auth` VALUES ('186', '18', '15');
INSERT INTO `v_user_role_auth` VALUES ('188', '18', '16');
INSERT INTO `v_user_role_auth` VALUES ('190', '18', '17');
INSERT INTO `v_user_role_auth` VALUES ('192', '18', '18');
INSERT INTO `v_user_role_auth` VALUES ('194', '18', '19');
INSERT INTO `v_user_role_auth` VALUES ('196', '18', '20');
INSERT INTO `v_user_role_auth` VALUES ('198', '18', '21');
INSERT INTO `v_user_role_auth` VALUES ('200', '1', '25');
INSERT INTO `v_user_role_auth` VALUES ('202', '16', '9');
INSERT INTO `v_user_role_auth` VALUES ('204', '16', '2');
INSERT INTO `v_user_role_auth` VALUES ('206', '16', '3');
INSERT INTO `v_user_role_auth` VALUES ('208', '16', '10');
INSERT INTO `v_user_role_auth` VALUES ('210', '16', '11');
INSERT INTO `v_user_role_auth` VALUES ('212', '16', '12');
INSERT INTO `v_user_role_auth` VALUES ('214', '16', '13');
INSERT INTO `v_user_role_auth` VALUES ('216', '16', '14');
INSERT INTO `v_user_role_auth` VALUES ('218', '16', '15');
INSERT INTO `v_user_role_auth` VALUES ('220', '16', '16');
INSERT INTO `v_user_role_auth` VALUES ('222', '16', '22');
INSERT INTO `v_user_role_auth` VALUES ('224', '16', '24');
INSERT INTO `v_user_role_auth` VALUES ('226', '16', '25');
INSERT INTO `v_user_role_auth` VALUES ('228', '16', '17');
INSERT INTO `v_user_role_auth` VALUES ('230', '16', '18');
INSERT INTO `v_user_role_auth` VALUES ('232', '16', '19');
INSERT INTO `v_user_role_auth` VALUES ('234', '16', '20');
INSERT INTO `v_user_role_auth` VALUES ('236', '16', '21');
INSERT INTO `v_user_role_auth` VALUES ('288', '17', '9');
INSERT INTO `v_user_role_auth` VALUES ('290', '17', '2');
INSERT INTO `v_user_role_auth` VALUES ('292', '17', '3');
INSERT INTO `v_user_role_auth` VALUES ('294', '17', '10');
INSERT INTO `v_user_role_auth` VALUES ('296', '17', '11');
INSERT INTO `v_user_role_auth` VALUES ('298', '17', '12');
INSERT INTO `v_user_role_auth` VALUES ('300', '17', '13');
INSERT INTO `v_user_role_auth` VALUES ('302', '17', '14');
INSERT INTO `v_user_role_auth` VALUES ('304', '17', '15');
INSERT INTO `v_user_role_auth` VALUES ('306', '17', '16');
INSERT INTO `v_user_role_auth` VALUES ('308', '17', '24');
INSERT INTO `v_user_role_auth` VALUES ('310', '17', '22');

-- ----------------------------
-- Table structure for v_visitor_record
-- ----------------------------
DROP TABLE IF EXISTS `v_visitor_record`;
CREATE TABLE `v_visitor_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '智慧访客',
  `visitDate` varchar(10) DEFAULT NULL COMMENT '访问日期',
  `visitTime` varchar(10) DEFAULT NULL COMMENT '访问时间',
  `userId` bigint(20) DEFAULT NULL COMMENT '访客id',
  `visitorId` bigint(20) DEFAULT NULL COMMENT '被访者id',
  `reason` varchar(512) DEFAULT NULL COMMENT '访问原因',
  `cstatus` varchar(60) DEFAULT NULL COMMENT '状态 applying:申请中，applySuccess:接受访问，applyFail:拒绝访问',
  `dateType` varchar(60) DEFAULT NULL COMMENT '日期类型:无期：Indefinite,有限期:limitPeriod',
  `startDate` varchar(20) DEFAULT NULL COMMENT '开始日期',
  `endDate` varchar(20) DEFAULT NULL COMMENT '结束日期',
  `answerContent` varchar(512) DEFAULT NULL COMMENT '被访者回复',
  `orgCode` varchar(60) DEFAULT NULL COMMENT '被访者大楼编码',
  `companyId` bigint(20) DEFAULT NULL COMMENT '被访者公司Id',
  `vitype` varchar(20) DEFAULT 'A' COMMENT '访问类型b浏览器',
  `recordType` int(10) DEFAULT NULL COMMENT '1--访问，2--邀约',
  `replyDate` varchar(10) DEFAULT NULL COMMENT '审核日期',
  `replyTime` varchar(10) DEFAULT NULL COMMENT '审核时间',
  `replyUserId` bigint(20) DEFAULT NULL COMMENT '审核人ID',
  `isReceive` varchar(20) DEFAULT 'F' COMMENT '是否已下发用户 T--是 F--否',
  `remarkName` varchar(20) DEFAULT NULL COMMENT '备注名 非好友邀约用',
  `userType` varchar(10) DEFAULT NULL COMMENT 'in 内网 userId连dept_user id   --out 外网 userId 连out_visitor id ',
  `visitorType` varchar(10) DEFAULT NULL COMMENT 'in 内网 visitorId连dept_user   id --out  外网 visitorId连out_visitor id ',
  `outRecordId` bigint(20) DEFAULT NULL COMMENT '外部访问记录id',
  `exp1` varchar(100) DEFAULT NULL COMMENT '扩展字段1',
  `exp2` varchar(100) DEFAULT NULL COMMENT '扩展字段2',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `userId` (`userId`) USING BTREE,
  KEY `visitorId` (`visitorId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=261 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of v_visitor_record
-- ----------------------------
INSERT INTO `v_visitor_record` VALUES ('246', '2020-09-11', '10:32', '12', '8', '商务拜访', 'applySuccess', null, '2020-09-11 10:32', '2020-09-11 11:31', null, '101', '10', 'F', '1', '2020-09-11', '10:32', '8', 'T', null, null, null, null, null, null);
INSERT INTO `v_visitor_record` VALUES ('248', '2020-09-11', '10:37', '12', '16', '商务拜访', 'applyFail', null, '2020-09-11 10:37', '2020-09-11 11:37', null, null, null, 'F', '1', '2020-09-11', '10:37', '16', 'F', null, null, null, null, null, null);
INSERT INTO `v_visitor_record` VALUES ('250', '2020-09-11', '10:39', '12', '16', '配送服务', 'applySuccess', null, '2020-09-11 10:38', '2020-09-11 11:38', null, '101', '10', 'F', '1', '2020-09-11', '10:44', '16', 'T', null, null, null, null, null, null);
INSERT INTO `v_visitor_record` VALUES ('252', '2020-09-11', '10:40', '12', '8', '面试', 'applySuccess', null, '2020-09-11 11:52', '2020-09-11 11:39', null, '101', '10', 'F', '1', '2020-09-11', '10:40', '8', 'T', null, null, null, null, null, null);
INSERT INTO `v_visitor_record` VALUES ('254', '2020-09-11', '10:52', '12', '16', '找人', 'applySuccess', null, '2020-09-11 12:00', '2020-09-11 11:52', null, '101', '10', 'F', '1', '2020-09-11', '10:53', '16', 'T', null, null, null, null, null, null);
INSERT INTO `v_visitor_record` VALUES ('256', '2020-09-11', '11:03', '12', '16', '商务拜访', 'applySuccess', null, '2020-09-11 13:00', '2020-09-11 14:00', null, '101', '10', 'F', '1', '2020-09-11', '11:04', '16', 'T', null, null, null, null, null, null);
INSERT INTO `v_visitor_record` VALUES ('258', '2020-09-15', '18:04', '8', '24', '配送服务', 'applySuccess', null, '2020-09-15 18:04', '2020-09-15 19:04', null, '101', '10', 'F', '1', '2020-09-15', '18:05', '24', 'T', null, null, null, null, null, null);
INSERT INTO `v_visitor_record` VALUES ('260', '2020-09-15', '18:22', '8', '24', '配送服务', 'applySuccess', null, '2020-09-15 19:05', '2020-09-15 20:00', null, '101', '10', 'F', '1', '2020-09-15', '18:22', '24', 'T', null, null, null, null, null, null);

-- ----------------------------
-- Table structure for wk_record
-- ----------------------------
DROP TABLE IF EXISTS `wk_record`;
CREATE TABLE `wk_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '打卡记录id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `company_id` bigint(20) DEFAULT NULL COMMENT '公司id',
  `checkin_type` varchar(20) DEFAULT NULL COMMENT '1上班打卡，2下班打卡，3外出打卡',
  `exception_type` varchar(20) DEFAULT NULL COMMENT '异常类型，字符串，包括：1 时间异常，2 地点异常，未打卡，3 wifi异常，4 非常用设备。如果有多个异常，以分号间隔',
  `checkin_date` varchar(10) DEFAULT NULL COMMENT '打卡时间',
  `checkin_time` varchar(20) DEFAULT NULL COMMENT '打卡时间',
  `location_title` varchar(40) DEFAULT NULL COMMENT '打卡地点title',
  `location_detail` varchar(100) DEFAULT NULL COMMENT '打卡地点详情',
  `wifi_name` varchar(40) DEFAULT NULL COMMENT '打卡wifi名称',
  `wifi_mac` varchar(40) DEFAULT NULL COMMENT '打卡的MAC地址/bssid',
  `checkin_divice` varchar(60) DEFAULT NULL COMMENT '打卡设备',
  `notes` varchar(255) DEFAULT NULL COMMENT '打卡备注 文字或照片地址',
  `mediaids` varchar(255) DEFAULT NULL COMMENT '打卡的附件media_id，可使用media/get获取附件',
  `lat` bigint(20) DEFAULT NULL COMMENT '位置打卡地点纬度，是实际纬度的1000000倍，与腾讯地图一致采用GCJ-02坐标系统标准',
  `lng` bigint(20) DEFAULT NULL COMMENT '位置打卡地点经度，是实际经度的1000000倍，与腾讯地图一致采用GCJ-02坐标系统标准',
  `effective` varchar(5) DEFAULT NULL COMMENT '''T'' 有效 ''F'' 失效',
  `creat_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `exp1` varchar(100) DEFAULT NULL COMMENT '拓展字段',
  `exp2` varchar(100) DEFAULT NULL COMMENT '拓展字段',
  `exp3` varchar(100) DEFAULT NULL COMMENT '拓展字段',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `checkin_time` (`checkin_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of wk_record
-- ----------------------------
