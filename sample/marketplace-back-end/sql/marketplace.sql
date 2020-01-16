DROP TABLE IF EXISTS `tbl_register`;
CREATE TABLE `tbl_register` (
  `id` varchar(255) NOT NULL COMMENT '主键',
  `ontid` varchar(255) DEFAULT NULL COMMENT 'ontid',
  `user_name` varchar(255) DEFAULT NULL COMMENT '用户名',
  `state` int(2) DEFAULT NULL COMMENT '是否认证成功;0-失败，1-成功，2-已存在',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_ontid` (`ontid`),
  KEY `idx_user_name` (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `tbl_product`;
CREATE TABLE `tbl_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `data_id` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
  `auth_id` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
  `ontid` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
  `name` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
  `description` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
  `price` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
  `amount` int(11) DEFAULT NULL,
  `tag` text CHARACTER SET latin1,
  `judger` text CHARACTER SET latin1,
  `state` int(2) DEFAULT NULL COMMENT '0-准备/下架；1-上传；2-准备上架；3-上架',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_data_id` (`data_id`) USING BTREE,
  KEY `idx_ontid` (`ontid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

DROP TABLE IF EXISTS `tbl_order`;
CREATE TABLE `tbl_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `invoke_id` varchar(255) DEFAULT NULL,
  `order_id` varchar(255) DEFAULT NULL,
  `token_id` varchar(255) DEFAULT NULL,
  `jwt` mediumtext,
  `provider` varchar(255) DEFAULT NULL,
  `demander` varchar(255) DEFAULT NULL,
  `price` varchar(255) DEFAULT NULL,
  `judger` mediumtext,
  `tx_hash` varchar(255) DEFAULT NULL,
  `state` int(2) DEFAULT NULL COMMENT '1-购买；2-购买成功；3-确认；4-确认成功',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_provider` (`provider`),
  KEY `idx_demander` (`demander`),
  KEY `idx_tx_hash` (`tx_hash`),
  KEY `idx_invoke_id` (`invoke_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `tbl_login`;
CREATE TABLE `tbl_login` (
  `id` varchar(255) NOT NULL COMMENT '主键',
  `ontid` varchar(255) DEFAULT NULL COMMENT 'ontid',
  `user_name` varchar(255) DEFAULT NULL COMMENT '用户名',
  `state` int(2) DEFAULT NULL COMMENT '是否认证成功;0-失败，1-成功，2-未注册',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_ontid` (`ontid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `tbl_invoke`;
CREATE TABLE `tbl_invoke` (
  `id` varchar(255) NOT NULL,
  `params` text,
  `ontid` varchar(255) DEFAULT NULL,
  `ontid_index` int(11) DEFAULT NULL,
  `tx_hash` varchar(255) DEFAULT NULL,
  `state` int(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;