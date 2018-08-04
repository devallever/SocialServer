/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50720
Source Host           : localhost:3306
Source Database       : dbsocial

Target Server Type    : MYSQL
Target Server Version : 50720
File Encoding         : 65001

Date: 2018-01-09 08:13:11
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for tad
-- ----------------------------
DROP TABLE IF EXISTS `tad`;
CREATE TABLE `tad` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `day_space` int(11) DEFAULT NULL,
  `count` int(11) DEFAULT NULL,
  `isshow` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tad
-- ----------------------------

-- ----------------------------
-- Table structure for taddetail
-- ----------------------------
DROP TABLE IF EXISTS `taddetail`;
CREATE TABLE `taddetail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `optlock1` bigint(20) DEFAULT '0',
  `optlock2` bigint(20) DEFAULT '0',
  `path` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `adname` varchar(255) DEFAULT NULL,
  `type` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of taddetail
-- ----------------------------

-- ----------------------------
-- Table structure for tautoreaction
-- ----------------------------
DROP TABLE IF EXISTS `tautoreaction`;
CREATE TABLE `tautoreaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `content` varchar(255) DEFAULT '',
  `optlock1` bigint(20) DEFAULT '0',
  `optlock2` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `tautoreaction_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tuser` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tautoreaction
-- ----------------------------

-- ----------------------------
-- Table structure for tcomment
-- ----------------------------
DROP TABLE IF EXISTS `tcomment`;
CREATE TABLE `tcomment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` longtext,
  `date` datetime DEFAULT NULL,
  `optlock1` bigint(20) DEFAULT NULL,
  `optlock2` bigint(20) DEFAULT NULL,
  `comment_id` bigint(20) DEFAULT NULL,
  `news_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `audio_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_1bf53845a4414d388edccc63fab` (`comment_id`),
  KEY `FK_db7a36877d3d4c8baeee2ac5be9` (`news_id`),
  KEY `FK_ddac7385e43d40e89b381ec5c7b` (`user_id`),
  CONSTRAINT `FK_1bf53845a4414d388edccc63fab` FOREIGN KEY (`comment_id`) REFERENCES `tcomment` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_db7a36877d3d4c8baeee2ac5be9` FOREIGN KEY (`news_id`) REFERENCES `tnews` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_ddac7385e43d40e89b381ec5c7b` FOREIGN KEY (`user_id`) REFERENCES `tuser` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tcomment
-- ----------------------------

-- ----------------------------
-- Table structure for tfee
-- ----------------------------
DROP TABLE IF EXISTS `tfee`;
CREATE TABLE `tfee` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `video_fee` int(11) DEFAULT '0',
  `voice_fee` int(11) DEFAULT '0',
  `accept_video` int(11) DEFAULT '1',
  `optlock1` bigint(20) DEFAULT '0',
  `optlock2` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `tfee_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tuser` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tfee
-- ----------------------------

-- ----------------------------
-- Table structure for tfeedback
-- ----------------------------
DROP TABLE IF EXISTS `tfeedback`;
CREATE TABLE `tfeedback` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` varchar(255) DEFAULT '',
  `optlock1` bigint(20) DEFAULT '0',
  `optlock2` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tfeedback
-- ----------------------------

-- ----------------------------
-- Table structure for tfollow
-- ----------------------------
DROP TABLE IF EXISTS `tfollow`;
CREATE TABLE `tfollow` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `follow_id` bigint(20) DEFAULT NULL,
  `optlock1` bigint(20) DEFAULT NULL,
  `optlock2` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `follow_id` (`follow_id`),
  CONSTRAINT `tfollow_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tuser` (`id`) ON DELETE CASCADE,
  CONSTRAINT `tfollow_ibfk_2` FOREIGN KEY (`follow_id`) REFERENCES `tuser` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tfollow
-- ----------------------------

-- ----------------------------
-- Table structure for tfriend
-- ----------------------------
DROP TABLE IF EXISTS `tfriend`;
CREATE TABLE `tfriend` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `optlock1` bigint(20) DEFAULT NULL,
  `optlock2` bigint(20) DEFAULT NULL,
  `state` int(11) NOT NULL,
  `friend_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `friendgroup_id` bigint(20) DEFAULT NULL,
  `second_name` varchar(255) DEFAULT '',
  `show_location` int(20) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `FK_4932008029d34d90a3534838302` (`friend_id`),
  KEY `FK_e80b8068a8ce4f19822b9fdf561` (`user_id`),
  KEY `fk_friendgroup_id` (`friendgroup_id`),
  CONSTRAINT `FK_4932008029d34d90a3534838302` FOREIGN KEY (`friend_id`) REFERENCES `tuser` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_e80b8068a8ce4f19822b9fdf561` FOREIGN KEY (`user_id`) REFERENCES `tuser` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_friendgroup_id` FOREIGN KEY (`friendgroup_id`) REFERENCES `tfriendgroup` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tfriend
-- ----------------------------

-- ----------------------------
-- Table structure for tfriendgroup
-- ----------------------------
DROP TABLE IF EXISTS `tfriendgroup`;
CREATE TABLE `tfriendgroup` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `friendgroup_name` varchar(255) DEFAULT NULL,
  `optlock1` bigint(20) DEFAULT NULL,
  `optlock2` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `tfriendgroup_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tuser` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tfriendgroup
-- ----------------------------
INSERT INTO `tfriendgroup` VALUES ('6', '6', '我的好友', '0', '0');

-- ----------------------------
-- Table structure for tgroup
-- ----------------------------
DROP TABLE IF EXISTS `tgroup`;
CREATE TABLE `tgroup` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `groupname` varchar(255) NOT NULL,
  `description` longtext,
  `groupimg` varchar(255) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `date` datetime NOT NULL,
  `longitude` double NOT NULL,
  `latitude` double NOT NULL,
  `point` varchar(255) NOT NULL,
  `state` int(11) DEFAULT '0',
  `grouplevel` int(11) DEFAULT '1',
  `attention` longtext,
  `limited` longtext,
  `optlock1` bigint(20) DEFAULT '0',
  `optlock2` bigint(20) DEFAULT '0',
  `hx_group_id` varchar(255) DEFAULT '0',
  `red_pocket_count` int(11) DEFAULT '0',
  `group_type` int(11) DEFAULT '2',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `tgroup_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tuser` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tgroup
-- ----------------------------

-- ----------------------------
-- Table structure for tgroupmember
-- ----------------------------
DROP TABLE IF EXISTS `tgroupmember`;
CREATE TABLE `tgroupmember` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `optlock1` bigint(20) DEFAULT '0',
  `optlock2` bigint(20) DEFAULT '0',
  `group_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `group_id` (`group_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `tgroupmember_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `tgroup` (`id`) ON DELETE CASCADE,
  CONSTRAINT `tgroupmember_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `tuser` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tgroupmember
-- ----------------------------

-- ----------------------------
-- Table structure for tlike
-- ----------------------------
DROP TABLE IF EXISTS `tlike`;
CREATE TABLE `tlike` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `optlock1` bigint(20) DEFAULT NULL,
  `optlock2` bigint(20) DEFAULT NULL,
  `news_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_9fedf9412330485c8a78257fb0f` (`news_id`),
  KEY `FK_9545c83ab5794d7faa07ec8ed52` (`user_id`),
  CONSTRAINT `FK_9545c83ab5794d7faa07ec8ed52` FOREIGN KEY (`user_id`) REFERENCES `tuser` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_9fedf9412330485c8a78257fb0f` FOREIGN KEY (`news_id`) REFERENCES `tnews` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tlike
-- ----------------------------

-- ----------------------------
-- Table structure for tnews
-- ----------------------------
DROP TABLE IF EXISTS `tnews`;
CREATE TABLE `tnews` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `city` varchar(255) DEFAULT NULL,
  `commentcount` int(11) NOT NULL,
  `content` longtext,
  `date` datetime DEFAULT NULL,
  `latitude` double NOT NULL,
  `lickcount` int(11) NOT NULL,
  `longitude` double NOT NULL,
  `optlock1` bigint(20) DEFAULT NULL,
  `optlock2` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `visited_count` int(11) DEFAULT '0',
  `audio_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_fc95fde8191d45388d4dc8ce1d1` (`user_id`),
  CONSTRAINT `FK_fc95fde8191d45388d4dc8ce1d1` FOREIGN KEY (`user_id`) REFERENCES `tuser` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tnews
-- ----------------------------

-- ----------------------------
-- Table structure for tnewsimage
-- ----------------------------
DROP TABLE IF EXISTS `tnewsimage`;
CREATE TABLE `tnewsimage` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `optlock1` bigint(20) DEFAULT NULL,
  `optlock2` bigint(20) DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  `news_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ae6817abd2ca415e8cd108816da` (`news_id`),
  CONSTRAINT `FK_ae6817abd2ca415e8cd108816da` FOREIGN KEY (`news_id`) REFERENCES `tnews` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tnewsimage
-- ----------------------------

-- ----------------------------
-- Table structure for tphotowall
-- ----------------------------
DROP TABLE IF EXISTS `tphotowall`;
CREATE TABLE `tphotowall` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `optlock1` bigint(20) DEFAULT '0',
  `optlock2` bigint(20) DEFAULT '0',
  `path` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `photoposition` int(4) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `tphotowall_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tuser` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tphotowall
-- ----------------------------

-- ----------------------------
-- Table structure for tpost
-- ----------------------------
DROP TABLE IF EXISTS `tpost`;
CREATE TABLE `tpost` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `optlock1` bigint(20) DEFAULT NULL,
  `optlock2` bigint(20) DEFAULT NULL,
  `postname` varchar(255) DEFAULT NULL,
  `salary` varchar(255) DEFAULT NULL,
  `recruit_id` bigint(20) DEFAULT NULL,
  `requirement` longtext,
  `description` longtext,
  `longitude` double DEFAULT '0',
  `latitude` double DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `recruit_id` (`recruit_id`),
  CONSTRAINT `tpost_ibfk_1` FOREIGN KEY (`recruit_id`) REFERENCES `trecruit` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tpost
-- ----------------------------

-- ----------------------------
-- Table structure for trank
-- ----------------------------
DROP TABLE IF EXISTS `trank`;
CREATE TABLE `trank` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `chatwith_id` bigint(20) NOT NULL,
  `optlock1` bigint(20) DEFAULT '0',
  `optlock2` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `chatwith_id` (`chatwith_id`),
  CONSTRAINT `trank_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tuser` (`id`) ON DELETE CASCADE,
  CONSTRAINT `trank_ibfk_2` FOREIGN KEY (`chatwith_id`) REFERENCES `tuser` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of trank
-- ----------------------------

-- ----------------------------
-- Table structure for trecommend
-- ----------------------------
DROP TABLE IF EXISTS `trecommend`;
CREATE TABLE `trecommend` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `recommended_id` bigint(20) NOT NULL,
  `count` int(11) DEFAULT '1',
  `optlock1` bigint(20) DEFAULT '0',
  `optlock2` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `recommended_id` (`recommended_id`),
  CONSTRAINT `trecommend_ibfk_1` FOREIGN KEY (`recommended_id`) REFERENCES `tuser` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of trecommend
-- ----------------------------

-- ----------------------------
-- Table structure for trecruit
-- ----------------------------
DROP TABLE IF EXISTS `trecruit`;
CREATE TABLE `trecruit` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `companyname` varchar(255) NOT NULL,
  `requirement` longtext,
  `date` datetime DEFAULT NULL,
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  `optlock1` bigint(20) DEFAULT NULL,
  `optlock2` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `link` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT '广东省',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `trecruit_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tuser` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of trecruit
-- ----------------------------

-- ----------------------------
-- Table structure for trecruitimage
-- ----------------------------
DROP TABLE IF EXISTS `trecruitimage`;
CREATE TABLE `trecruitimage` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `optlock1` bigint(20) DEFAULT NULL,
  `optlock2` bigint(20) DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  `recruit_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `recruit_id` (`recruit_id`),
  CONSTRAINT `trecruitimage_ibfk_1` FOREIGN KEY (`recruit_id`) REFERENCES `trecruit` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of trecruitimage
-- ----------------------------

-- ----------------------------
-- Table structure for tshare
-- ----------------------------
DROP TABLE IF EXISTS `tshare`;
CREATE TABLE `tshare` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `img_url` varchar(255) DEFAULT NULL,
  `optlock1` bigint(20) DEFAULT '0',
  `optlock2` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tshare
-- ----------------------------

-- ----------------------------
-- Table structure for tsharerank
-- ----------------------------
DROP TABLE IF EXISTS `tsharerank`;
CREATE TABLE `tsharerank` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `share_count` int(11) DEFAULT '0',
  `optlock1` bigint(20) DEFAULT '0',
  `optlock2` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `tsharerank_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tuser` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tsharerank
-- ----------------------------

-- ----------------------------
-- Table structure for tshuashua
-- ----------------------------
DROP TABLE IF EXISTS `tshuashua`;
CREATE TABLE `tshuashua` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `my_id` bigint(20) NOT NULL,
  `other_id` bigint(20) NOT NULL,
  `flag` int(11) DEFAULT NULL,
  `optlock1` bigint(20) DEFAULT '0',
  `optlock2` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `my_id` (`my_id`),
  KEY `other_id` (`other_id`),
  CONSTRAINT `tshuashua_ibfk_1` FOREIGN KEY (`my_id`) REFERENCES `tuser` (`id`) ON DELETE CASCADE,
  CONSTRAINT `tshuashua_ibfk_2` FOREIGN KEY (`other_id`) REFERENCES `tuser` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tshuashua
-- ----------------------------

-- ----------------------------
-- Table structure for tsign
-- ----------------------------
DROP TABLE IF EXISTS `tsign`;
CREATE TABLE `tsign` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `sign_date` datetime DEFAULT NULL,
  `day_count` int(11) DEFAULT '0',
  `optlock1` bigint(20) DEFAULT NULL,
  `optlock2` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `tsign_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tuser` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tsign
-- ----------------------------

-- ----------------------------
-- Table structure for tuser
-- ----------------------------
DROP TABLE IF EXISTS `tuser`;
CREATE TABLE `tuser` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `city` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `headpath` varchar(255) DEFAULT NULL,
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  `nickname` varchar(255) DEFAULT NULL,
  `optlock1` bigint(20) DEFAULT NULL,
  `optlock2` bigint(20) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `sex` varchar(255) DEFAULT NULL,
  `signature` longtext,
  `state` int(11) NOT NULL,
  `username` varchar(255) DEFAULT NULL,
  `age` int(4) DEFAULT NULL,
  `occupation` varchar(255) DEFAULT '学生',
  `constellation` varchar(255) DEFAULT '白羊座',
  `hight` varchar(255) DEFAULT '160',
  `weight` varchar(255) DEFAULT '45',
  `figure` varchar(255) DEFAULT '匀称',
  `emotion` varchar(255) DEFAULT '单身',
  `is_vip` int(4) DEFAULT '0',
  `visited_count` int(11) DEFAULT '0',
  `credit` int(11) DEFAULT '0',
  `second_name` varchar(255) DEFAULT '',
  `jpush_registration_id` varchar(255) DEFAULT '',
  `onlinestate` varchar(255) DEFAULT '在线',
  `messagecount` int(11) DEFAULT '10',
  `qq_open_id` varchar(255) DEFAULT '',
  `login_count` bigint(100) DEFAULT '0',
  `login_time` datetime DEFAULT NULL,
  `address` varchar(255) DEFAULT '广州市天河',
  `video_fee` int(11) DEFAULT '0',
  `voice_fee` int(11) DEFAULT '0',
  `accept_video` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tuser
-- ----------------------------
INSERT INTO `tuser` VALUES ('6', '北京 海淀', '', '/images/head/allever.jpg', '23.04666', '113.373211', 'allever', '1', '11', 'dixm', '13800138000', '男', '他很懒，什么也没写。', '0', 'allever', '23', '学生', '白羊座', '160', '45', '匀称', '单身', '0', '0', '0', null, '120c83f7601ad32fee5', '在线', '10', '', '1', '2017-12-10 13:56:41', '中国广东省广州市番禺区外环西路230号', '1', '1', '0');

-- ----------------------------
-- Table structure for tversion
-- ----------------------------
DROP TABLE IF EXISTS `tversion`;
CREATE TABLE `tversion` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version_code` int(11) DEFAULT NULL,
  `version_name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `app_path` varchar(255) DEFAULT NULL,
  `download_count` int(20) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tversion
-- ----------------------------

-- ----------------------------
-- Table structure for tvip
-- ----------------------------
DROP TABLE IF EXISTS `tvip`;
CREATE TABLE `tvip` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` int(11) DEFAULT '1',
  `start_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `optlock1` bigint(20) DEFAULT NULL,
  `optlock2` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `month_count` int(4) DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `tvip_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tuser` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tvip
-- ----------------------------

-- ----------------------------
-- Table structure for tvisitednews
-- ----------------------------
DROP TABLE IF EXISTS `tvisitednews`;
CREATE TABLE `tvisitednews` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `news_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `who_id` bigint(20) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `optlock1` bigint(20) DEFAULT NULL,
  `optlock2` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `news_id` (`news_id`),
  KEY `who_id` (`who_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `tvisitednews_ibfk_1` FOREIGN KEY (`news_id`) REFERENCES `tnews` (`id`) ON DELETE CASCADE,
  CONSTRAINT `tvisitednews_ibfk_2` FOREIGN KEY (`who_id`) REFERENCES `tuser` (`id`) ON DELETE CASCADE,
  CONSTRAINT `tvisitednews_ibfk_3` FOREIGN KEY (`user_id`) REFERENCES `tuser` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tvisitednews
-- ----------------------------

-- ----------------------------
-- Table structure for tvisiteduser
-- ----------------------------
DROP TABLE IF EXISTS `tvisiteduser`;
CREATE TABLE `tvisiteduser` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `who_id` bigint(20) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `optlock1` bigint(20) DEFAULT NULL,
  `optlock2` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `who_id` (`who_id`),
  CONSTRAINT `tvisiteduser_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tuser` (`id`) ON DELETE CASCADE,
  CONSTRAINT `tvisiteduser_ibfk_2` FOREIGN KEY (`who_id`) REFERENCES `tuser` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tvisiteduser
-- ----------------------------

-- ----------------------------
-- Table structure for twebcollection
-- ----------------------------
DROP TABLE IF EXISTS `twebcollection`;
CREATE TABLE `twebcollection` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `title` varchar(255) DEFAULT '',
  `url` varchar(255) DEFAULT '',
  `date` datetime DEFAULT NULL,
  `optlock1` bigint(20) DEFAULT '0',
  `optlock2` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `twebcollection_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tuser` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of twebcollection
-- ----------------------------

-- ----------------------------
-- Table structure for twithdraw
-- ----------------------------
DROP TABLE IF EXISTS `twithdraw`;
CREATE TABLE `twithdraw` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `money` int(11) DEFAULT '0',
  `account` varchar(255) DEFAULT NULL,
  `state` int(11) DEFAULT '0',
  `date` datetime DEFAULT NULL,
  `optlock1` bigint(20) DEFAULT '0',
  `optlock2` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `twithdraw_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tuser` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of twithdraw
-- ----------------------------
