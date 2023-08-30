#
建表脚本

-- 创建库
create
database if not exists yubi;

-- 切换库
use
yubi;

-- 用户表
CREATE TABLE `user`
(
    `id`           bigint(20) NOT NULL COMMENT 'id',
    `userAccount`  varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号',
    `userPassword` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
    `userName`     varchar(256) COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT '用户昵称',
    `userAvatar`   varchar(1024) COLLATE utf8mb4_unicode_ci         DEFAULT NULL COMMENT '用户头像',
    `userRole`     varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'user' COMMENT '用户角色：user/admin/ban',
    `createTime`   datetime                                NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime`   datetime                                NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `isDelete`     tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
    `availableNum` int(11) NOT NULL DEFAULT '0' COMMENT '可使用次数',
    PRIMARY KEY (`id`),
    KEY            `idx_userAccount` (`userAccount`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户';


-- 图表信息表
CREATE TABLE `chart`
(
    `id`          bigint(20) NOT NULL COMMENT 'id',
    `goal`        text COLLATE utf8mb4_unicode_ci COMMENT '分析目标',
    `chartData`   text COLLATE utf8mb4_unicode_ci COMMENT '图表数据',
    `chartType`   varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图表类型',
    `genChart`    text COLLATE utf8mb4_unicode_ci COMMENT '生成的图表数据',
    `genResult`   text COLLATE utf8mb4_unicode_ci COMMENT '生成的分析结论',
    `userId`      bigint(20) DEFAULT NULL COMMENT '用户Id',
    `createTime`  datetime NOT NULL                       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime`  datetime NOT NULL                       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `isDelete`    tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
    `name`        varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图表名称',
    `status`      tinyint(4) NOT NULL DEFAULT '0' COMMENT '图表状态：0-等待,1-执行中,2-执行成功,3-执行失败',
    `execMessage` text COLLATE utf8mb4_unicode_ci COMMENT '执行信息',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图表信息表';

-- 图表分析溢出任务表
CREATE TABLE `chart_analyse_overflow`
(
    `id`         bigint(20) NOT NULL COMMENT 'id',
    `chartId`    bigint(20) DEFAULT NULL COMMENT '图表Id',
    `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `isDelete`   tinyint(4) DEFAULT NULL COMMENT '是否删除',
    `question`   text COLLATE utf8mb4_unicode_ci COMMENT '分析问题',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图表分析溢出任务表';



