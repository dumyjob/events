create table t_event(
     `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增id，无业务意义，不使用',
     `topic` varchar(64)  NOT NULL default '' COMMENT '消息topic',
     `tag` varchar(64)  NOT NULL default ''  COMMENT '消息tag',
     `status` varchar(16) UNSIGNED NOT NULL default 'WAIT' COMMENT '消息状态',
     `message` varchar(60000)  NOT NULL default ''  COMMENT '消息体',
     `tracker_id` varchar(64)  NOT NULL default  COMMENT '消息跟踪id',
     `is_delete` int(11) NOT NULL DEFAULT 0 COMMENT '是否删除 0:未删除 1:已删除',
     `created_by` varchar(32)CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '创建人',
     `created_date` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     `updated_by` varchar(32) NOT NULL DEFAULT '' COMMENT '最后更新人',
     `updated_date` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
      PRIMARY KEY (`id`) USING BTREE,
      INDEX `idx_create_date`(`created_date`) USING BTREE COMMENT '创建时间索引',
      INDEX `idx_update_date`(`updated_date`) USING BTREE COMMENT '更新时间索引',
      INDEX `idx_tracker_id`(`tracker_id`) USING BTREE COMMENT '跟踪id'
);


create table tbl_stored_event (
	event_id int(11) not null auto_increment,
	event_body varchar(65000) not  null,
	occurred_on datetime not null,
	type_name varchar(100) not null,
	primary_key('event_id')

)