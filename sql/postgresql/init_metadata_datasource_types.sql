/*
 * 元数据管理 - 数据源类型字典数据初始化脚本
 * 
 * 功能描述：将数据源类型插入到字典表中，供前端接口调用
 * 创建时间：2025-07-28
 * 作者：System
 */

-- ----------------------------
-- 插入字典类型：数据源类型
-- ----------------------------
INSERT INTO system_dict_type (id, name, type, status, remark, creator, create_time, updater, update_time, deleted, deleted_time) 
VALUES (nextval('system_dict_type_seq'), '数据源类型', 'metadata_datasource_type', 0, '元数据管理中支持的数据源类型定义', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0, NULL);

-- ----------------------------
-- 插入字典数据：数据源类型数据
-- ----------------------------

-- 1. POSTGRESQL - PostgreSQL数据库
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) 
VALUES (nextval('system_dict_data_seq'), 1, 'PostgreSQL', 'POSTGRESQL', 'metadata_datasource_type', 0, 'primary', '', 'PostgreSQL开源关系型数据库，支持9.6及以上版本', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0);

-- 2. MYSQL - MySQL数据库
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) 
VALUES (nextval('system_dict_data_seq'), 2, 'MySQL', 'MYSQL', 'metadata_datasource_type', 0, 'success', '', 'MySQL关系型数据库，支持5.7及以上版本', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0);

-- 3. CLICKHOUSE - ClickHouse分析数据库
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) 
VALUES (nextval('system_dict_data_seq'), 3, 'ClickHouse', 'CLICKHOUSE', 'metadata_datasource_type', 0, 'warning', '', 'ClickHouse列式分析数据库，适用于OLAP场景', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0);

-- 4. TDENGINE - TDengine时序数据库
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) 
VALUES (nextval('system_dict_data_seq'), 4, 'TDengine', 'TDENGINE', 'metadata_datasource_type', 0, 'info', '', 'TDengine时序数据库，专为物联网和工业互联网设计', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0);

-- 5. DM - 达梦数据库
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) 
VALUES (nextval('system_dict_data_seq'), 5, '达梦数据库', 'DM', 'metadata_datasource_type', 0, 'danger', '', '达梦数据库管理系统，国产关系型数据库', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0);

-- 6. KINGBASE - 人大金仓数据库
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) 
VALUES (nextval('system_dict_data_seq'), 6, '人大金仓', 'KINGBASE', 'metadata_datasource_type', 0, 'danger', '', '人大金仓KingbaseES数据库，国产关系型数据库', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0);

-- 7. ORACLE - Oracle数据库
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) 
VALUES (nextval('system_dict_data_seq'), 7, 'Oracle', 'ORACLE', 'metadata_datasource_type', 0, 'primary', '', 'Oracle关系型数据库，支持11g及以上版本', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0);

-- 8. SQLSERVER - SQL Server数据库
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) 
VALUES (nextval('system_dict_data_seq'), 8, 'SQL Server', 'SQLSERVER', 'metadata_datasource_type', 0, 'info', '', 'Microsoft SQL Server数据库，支持2012及以上版本', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0);

-- 9. OPENGAUSS - openGauss数据库
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) 
VALUES (nextval('system_dict_data_seq'), 9, 'openGauss', 'OPENGAUSS', 'metadata_datasource_type', 0, 'success', '', 'openGauss开源关系型数据库，华为开源数据库', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0);

-- 10. REDIS - Redis缓存数据库
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) 
VALUES (nextval('system_dict_data_seq'), 10, 'Redis', 'REDIS', 'metadata_datasource_type', 0, 'warning', '', 'Redis内存数据库，用于缓存和会话存储', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0);

-- 11. MONGODB - MongoDB文档数据库
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) 
VALUES (nextval('system_dict_data_seq'), 11, 'MongoDB', 'MONGODB', 'metadata_datasource_type', 0, 'success', '', 'MongoDB文档型NoSQL数据库', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0);

-- 12. ELASTICSEARCH - Elasticsearch搜索引擎
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) 
VALUES (nextval('system_dict_data_seq'), 12, 'Elasticsearch', 'ELASTICSEARCH', 'metadata_datasource_type', 0, 'info', '', 'Elasticsearch分布式搜索和分析引擎', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0);

-- ----------------------------
-- 提交事务
-- ----------------------------
COMMIT;

-- ----------------------------
-- 验证插入结果
-- ----------------------------
-- 查询字典类型
SELECT * FROM system_dict_type WHERE type = 'metadata_datasource_type';

-- 查询字典数据
SELECT id, sort, label, value, remark 
FROM system_dict_data 
WHERE dict_type = 'metadata_datasource_type' 
ORDER BY sort;
