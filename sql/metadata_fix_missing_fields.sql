-- ============================================================================
-- Metadata模块表结构修复 ALTER SQL脚本 (PostgreSQL)
-- 说明：修复缺失的字段和字段名错误
-- 作者：matianyu
-- 日期：2025-12-03
-- ============================================================================

-- ============================================================================
-- 1. metadata_app_and_datasource（应用数据源关联表）- 缺少version_tag字段
-- ============================================================================
ALTER TABLE metadata_app_and_datasource ADD COLUMN IF NOT EXISTS version_tag BIGINT DEFAULT 0;
COMMENT ON COLUMN metadata_app_and_datasource.version_tag IS '版本标识';

-- ============================================================================
-- 2. metadata_auto_number_rule_item（自动编号规则项表）- 缺少version_tag字段
-- ============================================================================
ALTER TABLE metadata_auto_number_rule_item ADD COLUMN IF NOT EXISTS version_tag BIGINT DEFAULT 0;
COMMENT ON COLUMN metadata_auto_number_rule_item.version_tag IS '版本标识';

-- ============================================================================
-- 3. metadata_auto_number_state（自动编号状态表）- 缺少version_tag字段
-- ============================================================================
ALTER TABLE metadata_auto_number_state ADD COLUMN IF NOT EXISTS version_tag BIGINT DEFAULT 0;
COMMENT ON COLUMN metadata_auto_number_state.version_tag IS '版本标识';

-- ============================================================================
-- 4. metadata_entity_field_constraint（实体字段约束表）
--    - 缺少version_tag字段
--    - 需要将app_id修改为application_id
-- ============================================================================
-- 添加version_tag字段
ALTER TABLE metadata_entity_field_constraint ADD COLUMN IF NOT EXISTS version_tag BIGINT DEFAULT 0;
COMMENT ON COLUMN metadata_entity_field_constraint.version_tag IS '版本标识';

-- 将app_id重命名为application_id（如果存在app_id）
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'metadata_entity_field_constraint' AND column_name = 'app_id'
    ) THEN
        ALTER TABLE metadata_entity_field_constraint RENAME COLUMN app_id TO application_id;
    END IF;
END $$;
COMMENT ON COLUMN metadata_entity_field_constraint.application_id IS '应用ID';

-- ============================================================================
-- 5. metadata_validation_rule_definition（校验规则定义表）
--    - 缺少application_id和version_tag字段
-- ============================================================================
ALTER TABLE metadata_validation_rule_definition ADD COLUMN IF NOT EXISTS application_id BIGINT;
COMMENT ON COLUMN metadata_validation_rule_definition.application_id IS '应用ID';

ALTER TABLE metadata_validation_rule_definition ADD COLUMN IF NOT EXISTS version_tag BIGINT DEFAULT 0;
COMMENT ON COLUMN metadata_validation_rule_definition.version_tag IS '版本标识';

-- ============================================================================
-- 6. metadata_validation_rule_group（校验规则组表）
--    - 缺少application_id和version_tag字段
-- ============================================================================
ALTER TABLE metadata_validation_rule_group ADD COLUMN IF NOT EXISTS application_id BIGINT;
COMMENT ON COLUMN metadata_validation_rule_group.application_id IS '应用ID';

ALTER TABLE metadata_validation_rule_group ADD COLUMN IF NOT EXISTS version_tag BIGINT DEFAULT 0;
COMMENT ON COLUMN metadata_validation_rule_group.version_tag IS '版本标识';

-- ============================================================================
-- 7. metadata_entity_field_option（实体字段选项表）- 缺少version_tag字段
-- ============================================================================
ALTER TABLE metadata_entity_field_option ADD COLUMN IF NOT EXISTS version_tag BIGINT DEFAULT 0;
COMMENT ON COLUMN metadata_entity_field_option.version_tag IS '版本标识';

-- ============================================================================
-- 8. metadata_entity_field（实体字段表）- 删除多余的application_uuid字段
-- ============================================================================
ALTER TABLE metadata_entity_field DROP COLUMN IF EXISTS application_uuid;

-- ============================================================================
-- 9. metadata_entity_field_option（实体字段选项表）- 删除多余的application_uuid字段
-- ============================================================================
ALTER TABLE metadata_entity_field_option DROP COLUMN IF EXISTS application_uuid;

-- ============================================================================
-- 完成说明
-- ============================================================================
-- 本脚本完成以下修复：
-- 1. metadata_app_and_datasource: 添加version_tag字段
-- 2. metadata_auto_number_rule_item: 添加version_tag字段
-- 3. metadata_auto_number_state: 添加version_tag字段
-- 4. metadata_entity_field_constraint: 添加version_tag字段，将app_id重命名为application_id
-- 5. metadata_validation_rule_definition: 添加application_id和version_tag字段
-- 6. metadata_validation_rule_group: 添加application_id和version_tag字段
-- 7. metadata_entity_field_option: 添加version_tag字段
-- 8. metadata_entity_field: 删除多余的application_uuid字段（已有application_id）
-- 9. metadata_entity_field_option: 删除多余的application_uuid字段（已有application_id）
-- 
-- 注意事项：
-- 1. 执行前请备份数据库
-- 2. 该脚本使用 IF NOT EXISTS / IF EXISTS 语法，可以安全地重复执行
-- ============================================================================
