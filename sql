/*
Navicat MySQL Data Transfer

Source Server         : 120.76.126.163
Source Server Version : 100130
Source Host           : 120.76.126.163:3306
Source Database       : web_app_server

Target Server Type    : MYSQL
Target Server Version : 100130
File Encoding         : 65001

Date: 2020-02-21 23:18:01
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for file_info
-- ----------------------------
DROP TABLE IF EXISTS `file_info`;
CREATE TABLE `file_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) DEFAULT NULL,
  `uuid` varchar(500) DEFAULT NULL,
  `md5` varchar(500) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by` varchar(255) DEFAULT NULL,
  `update_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of file_info
-- ----------------------------
INSERT INTO `file_info` VALUES ('1', '我是测试', 'cb260e79bccb4a1a993c459f4645c4f9', 'a1b5487f84d155b8d408f2ed55ec1068', '2020-02-21 23:12:17', '2020-02-21 09:17:28', null, null);
