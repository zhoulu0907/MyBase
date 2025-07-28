/*
 * 元数据管理 - 实体字段类型字典数据初始化脚本
 * 
 * 功能描述：将实体字段类型插入到字典表中，供前端接口调用
 * 创建时间：2025-07-28
 * 作者：System
 */

-- ----------------------------
-- 插入字典类型：实体字段类型
-- ----------------------------
INSERT INTO system_dict_type (id, name, type, status, remark, creator, create_time, updater, update_time, deleted, deleted_time) 
VALUES (nextval('system_dict_type_seq'), '实体字段类型', 'metadata_entity_field_type', 0, '元数据管理中业务实体字段的类型定义', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0, NULL);

-- ----------------------------
-- 插入字典数据：实体字段类型数据
-- ----------------------------

-- 1. TEXT - 短文本录入
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) 
VALUES (nextval('system_dict_data_seq'), 1, '短文本', 'TEXT', 'metadata_entity_field_type', 0, 'primary', '', '短文本录入（如名称），对应单行输入框', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0);

-- 2. LONG_TEXT - 长文本内容
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) 
VALUES (nextval('system_dict_data_seq'), 2, '长文本', 'LONG_TEXT', 'metadata_entity_field_type', 0, 'info', '', '长文本内容（如描述、备注），对应多行文本框/富文本编辑器', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0);

-- 3. EMAIL - 邮箱地址录入
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) 
VALUES (nextval('system_dict_data_seq'), 3, '邮箱', 'EMAIL', 'metadata_entity_field_type', 0, 'success', '', '邮箱地址录入，带格式校验的输入框', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0);

-- 4. PHONE - 电话号码录入
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) 
VALUES (nextval('system_dict_data_seq'), 4, '电话', 'PHONE', 'metadata_entity_field_type', 0, 'success', '', '电话号码录入，带格式校验的输入框', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0);

-- 5. NUMBER - 通用数字
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) 
VALUES (nextval('system_dict_data_seq'), 5, '数字', 'NUMBER', 'metadata_entity_field_type', 0, 'warning', '', '通用数字（如数量、年龄），对应数字输入框', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0);

-- 6. DATE - 日期选择
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) 
VALUES (nextval('system_dict_data_seq'), 6, '日期', 'DATE', 'metadata_entity_field_type', 0, 'info', '', '日期选择（如生日、到期日），对应日期选择器', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0);

-- 7. DATETIME - 日期时间选择
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) 
VALUES (nextval('system_dict_data_seq'), 7, '日期时间', 'DATETIME', 'metadata_entity_field_type', 0, 'info', '', '日期时间选择（如创建时间），对应日期时间选择器', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0);

-- 8. BOOLEAN - 布尔值
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) 
VALUES (nextval('system_dict_data_seq'), 8, '布尔值', 'BOOLEAN', 'metadata_entity_field_type', 0, 'primary', '', '布尔值（是/否、启用/禁用），对应开关/复选框', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0);

-- 9. PICKLIST - 单选下拉
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) 
VALUES (nextval('system_dict_data_seq'), 9, '单选列表', 'PICKLIST', 'metadata_entity_field_type', 0, 'success', '', '单选下拉（如状态、类型），对应下拉选择框', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0);

-- 10. MULTI_PICKLIST - 多选列表
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) 
VALUES (nextval('system_dict_data_seq'), 10, '多选列表', 'MULTI_PICKLIST', 'metadata_entity_field_type', 0, 'success', '', '多选列表（如标签、权限），对应多选框组/下拉多选', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0);

-- 11. AUTO_CODE - 自动生成唯一编码
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) 
VALUES (nextval('system_dict_data_seq'), 11, '自动编码', 'AUTO_CODE', 'metadata_entity_field_type', 0, 'danger', '', '自动生成唯一编码（如订单号），对应只读展示框', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0);

-- 12. USER - 人员引用
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) 
VALUES (nextval('system_dict_data_seq'), 12, '人员引用', 'USER', 'metadata_entity_field_type', 0, 'warning', '', '人员引用（关联系统用户），对应人员选择器（弹窗/下拉）', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0);

-- 13. DEPARTMENT - 部门引用
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) 
VALUES (nextval('system_dict_data_seq'), 13, '部门引用', 'DEPARTMENT', 'metadata_entity_field_type', 0, 'warning', '', '部门引用（关联组织架构），对应部门选择器（树形/下拉）', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0);

-- 14. DATA_SELECTION - 跨表数据选择
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) 
VALUES (nextval('system_dict_data_seq'), 14, '数据选择', 'DATA_SELECTION', 'metadata_entity_field_type', 0, 'primary', '', '跨表数据选择（如关联其他表单），对应数据选择器（弹窗/搜索）', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0);

-- 15. RELATION - 表间关联关系
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) 
VALUES (nextval('system_dict_data_seq'), 15, '关联关系', 'RELATION', 'metadata_entity_field_type', 0, 'info', '', '表间关联关系（如1对多、多对多），对应关联字段编辑器', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0);

-- 16. FILE - 文件上传
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) 
VALUES (nextval('system_dict_data_seq'), 16, '文件', 'FILE', 'metadata_entity_field_type', 0, 'default', '', '文件上传（如附件、文档），对应文件上传组件', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0);

-- 17. IMAGE - 图片上传
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) 
VALUES (nextval('system_dict_data_seq'), 17, '图片', 'IMAGE', 'metadata_entity_field_type', 0, 'default', '', '图片上传（如头像、截图），对应图片上传+预览组件', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0);

-- ----------------------------
-- 提交事务
-- ----------------------------
COMMIT;

-- ----------------------------
-- 验证插入结果
-- ----------------------------
-- 查询字典类型
SELECT * FROM system_dict_type WHERE type = 'metadata_entity_field_type';

-- 查询字典数据
SELECT id, sort, label, value, remark 
FROM system_dict_data 
WHERE dict_type = 'metadata_entity_field_type' 
ORDER BY sort;
