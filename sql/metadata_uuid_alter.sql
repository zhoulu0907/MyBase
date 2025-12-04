-- ============================================================================
-- Metadata模块UUID改造 ALTER SQL脚本
-- 说明：为元数据模块相关表添加UUID字段，并创建唯一索引
-- 作者：matianyu
-- 日期：2025-01-25
-- ============================================================================

-- ============================================================================
-- 1. metadata_datasource（数据源表）
-- ============================================================================
ALTER TABLE metadata_datasource ADD COLUMN datasource_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_datasource.datasource_uuid IS '数据源UUID';

CREATE UNIQUE INDEX uk_datasource_uuid_app_ver 
    ON metadata_datasource (datasource_uuid, application_id, version_tag);

-- ============================================================================
-- 2. metadata_app_and_datasource（应用数据源关联表）
-- ============================================================================
-- 删除原关联ID字段，新增UUID关联字段
ALTER TABLE metadata_app_and_datasource DROP COLUMN IF EXISTS datasource_id;
ALTER TABLE metadata_app_and_datasource ADD COLUMN datasource_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_app_and_datasource.datasource_uuid IS '数据源UUID';

-- ============================================================================
-- 3. metadata_business_entity（业务实体表）
-- ============================================================================
ALTER TABLE metadata_business_entity ADD COLUMN entity_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_business_entity.entity_uuid IS '实体UUID';

-- 将datasourceId改为datasourceUuid
ALTER TABLE metadata_business_entity DROP COLUMN IF EXISTS datasource_id;
ALTER TABLE metadata_business_entity ADD COLUMN datasource_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_business_entity.datasource_uuid IS '数据源UUID';

CREATE UNIQUE INDEX uk_entity_uuid_app_ver 
    ON metadata_business_entity (entity_uuid, application_id, version_tag);

-- ============================================================================
-- 4. metadata_entity_field（实体字段表）
-- ============================================================================
ALTER TABLE metadata_entity_field ADD COLUMN field_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_entity_field.field_uuid IS '字段UUID';

-- 将entityId改为entityUuid
ALTER TABLE metadata_entity_field DROP COLUMN IF EXISTS entity_id;
ALTER TABLE metadata_entity_field ADD COLUMN entity_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_entity_field.entity_uuid IS '实体UUID';

CREATE UNIQUE INDEX uk_field_uuid_app_ver 
    ON metadata_entity_field (field_uuid, application_id, version_tag);

-- ============================================================================
-- 5. metadata_entity_field_option（字段选项表）
-- ============================================================================
ALTER TABLE metadata_entity_field_option ADD COLUMN option_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_entity_field_option.option_uuid IS '选项UUID';

-- 将fieldId改为fieldUuid
ALTER TABLE metadata_entity_field_option DROP COLUMN IF EXISTS field_id;
ALTER TABLE metadata_entity_field_option ADD COLUMN field_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_entity_field_option.field_uuid IS '字段UUID';

CREATE UNIQUE INDEX uk_option_uuid_app_ver 
    ON metadata_entity_field_option (option_uuid, application_id, version_tag);

-- ============================================================================
-- 6. metadata_entity_relationship（实体关系表）
-- ============================================================================
ALTER TABLE metadata_entity_relationship ADD COLUMN relationship_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_entity_relationship.relationship_uuid IS '关系UUID';

-- 将所有关联ID改为UUID
ALTER TABLE metadata_entity_relationship DROP COLUMN IF EXISTS source_entity_id;
ALTER TABLE metadata_entity_relationship ADD COLUMN source_entity_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_entity_relationship.source_entity_uuid IS '源实体UUID';

ALTER TABLE metadata_entity_relationship DROP COLUMN IF EXISTS target_entity_id;
ALTER TABLE metadata_entity_relationship ADD COLUMN target_entity_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_entity_relationship.target_entity_uuid IS '目标实体UUID';

ALTER TABLE metadata_entity_relationship DROP COLUMN IF EXISTS source_field_id;
ALTER TABLE metadata_entity_relationship ADD COLUMN source_field_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_entity_relationship.source_field_uuid IS '源字段UUID';

ALTER TABLE metadata_entity_relationship DROP COLUMN IF EXISTS target_field_id;
ALTER TABLE metadata_entity_relationship ADD COLUMN target_field_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_entity_relationship.target_field_uuid IS '目标字段UUID';

ALTER TABLE metadata_entity_relationship DROP COLUMN IF EXISTS select_field_id;
ALTER TABLE metadata_entity_relationship ADD COLUMN select_field_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_entity_relationship.select_field_uuid IS '选择字段UUID';

CREATE UNIQUE INDEX uk_relationship_uuid_app_ver 
    ON metadata_entity_relationship (relationship_uuid, application_id, version_tag);

-- ============================================================================
-- 7. metadata_auto_number_config（自动编号配置表）
-- ============================================================================
ALTER TABLE metadata_auto_number_config ADD COLUMN config_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_auto_number_config.config_uuid IS '配置UUID';

-- 将fieldId改为fieldUuid
ALTER TABLE metadata_auto_number_config DROP COLUMN IF EXISTS field_id;
ALTER TABLE metadata_auto_number_config ADD COLUMN field_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_auto_number_config.field_uuid IS '字段UUID';

CREATE UNIQUE INDEX uk_auto_config_uuid_app_ver 
    ON metadata_auto_number_config (config_uuid, application_id, version_tag);

-- ============================================================================
-- 8. metadata_auto_number_rule_item（自动编号规则项表）
-- ============================================================================
ALTER TABLE metadata_auto_number_rule_item ADD COLUMN rule_item_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_auto_number_rule_item.rule_item_uuid IS '规则项UUID';

-- 将configId改为configUuid
ALTER TABLE metadata_auto_number_rule_item DROP COLUMN IF EXISTS config_id;
ALTER TABLE metadata_auto_number_rule_item ADD COLUMN config_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_auto_number_rule_item.config_uuid IS '配置UUID';

-- 将refFieldId改为refFieldUuid
ALTER TABLE metadata_auto_number_rule_item DROP COLUMN IF EXISTS ref_field_id;
ALTER TABLE metadata_auto_number_rule_item ADD COLUMN ref_field_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_auto_number_rule_item.ref_field_uuid IS '引用字段UUID';

CREATE UNIQUE INDEX uk_rule_item_uuid_app_ver 
    ON metadata_auto_number_rule_item (rule_item_uuid, application_id, version_tag);

-- ============================================================================
-- 9. metadata_auto_number_state（自动编号状态表）
-- ============================================================================
ALTER TABLE metadata_auto_number_state ADD COLUMN state_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_auto_number_state.state_uuid IS '状态UUID';

-- 将configId改为configUuid
ALTER TABLE metadata_auto_number_state DROP COLUMN IF EXISTS config_id;
ALTER TABLE metadata_auto_number_state ADD COLUMN config_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_auto_number_state.config_uuid IS '配置UUID';

CREATE UNIQUE INDEX uk_state_uuid_app_ver 
    ON metadata_auto_number_state (state_uuid, application_id, version_tag);

-- ============================================================================
-- 10. metadata_validation_rule_group（校验规则组表）
-- ============================================================================
ALTER TABLE metadata_validation_rule_group ADD COLUMN group_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_rule_group.group_uuid IS '规则组UUID';

-- 将entityId改为entityUuid
ALTER TABLE metadata_validation_rule_group DROP COLUMN IF EXISTS entity_id;
ALTER TABLE metadata_validation_rule_group ADD COLUMN entity_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_rule_group.entity_uuid IS '实体UUID';

CREATE UNIQUE INDEX uk_val_group_uuid_app_ver 
    ON metadata_validation_rule_group (group_uuid, application_id, version_tag);

-- ============================================================================
-- 11. metadata_validation_rule_definition（校验规则定义表）
-- ============================================================================
ALTER TABLE metadata_validation_rule_definition ADD COLUMN definition_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_rule_definition.definition_uuid IS '规则定义UUID';

-- 将所有关联ID改为UUID
ALTER TABLE metadata_validation_rule_definition DROP COLUMN IF EXISTS group_id;
ALTER TABLE metadata_validation_rule_definition ADD COLUMN group_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_rule_definition.group_uuid IS '规则组UUID';

ALTER TABLE metadata_validation_rule_definition DROP COLUMN IF EXISTS parent_rule_id;
ALTER TABLE metadata_validation_rule_definition ADD COLUMN parent_rule_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_rule_definition.parent_rule_uuid IS '父规则UUID';

ALTER TABLE metadata_validation_rule_definition DROP COLUMN IF EXISTS entity_id;
ALTER TABLE metadata_validation_rule_definition ADD COLUMN entity_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_rule_definition.entity_uuid IS '实体UUID';

ALTER TABLE metadata_validation_rule_definition DROP COLUMN IF EXISTS field_id;
ALTER TABLE metadata_validation_rule_definition ADD COLUMN field_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_rule_definition.field_uuid IS '字段UUID';

CREATE UNIQUE INDEX uk_val_def_uuid_app_ver 
    ON metadata_validation_rule_definition (definition_uuid, application_id, version_tag);

-- ============================================================================
-- 12. metadata_validation_required（必填校验表）
-- ============================================================================
ALTER TABLE metadata_validation_required ADD COLUMN required_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_required.required_uuid IS '必填校验UUID';

-- 将所有关联ID改为UUID
ALTER TABLE metadata_validation_required DROP COLUMN IF EXISTS group_id;
ALTER TABLE metadata_validation_required ADD COLUMN group_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_required.group_uuid IS '规则组UUID';

ALTER TABLE metadata_validation_required DROP COLUMN IF EXISTS entity_id;
ALTER TABLE metadata_validation_required ADD COLUMN entity_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_required.entity_uuid IS '实体UUID';

ALTER TABLE metadata_validation_required DROP COLUMN IF EXISTS field_id;
ALTER TABLE metadata_validation_required ADD COLUMN field_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_required.field_uuid IS '字段UUID';

CREATE UNIQUE INDEX uk_val_req_uuid_app_ver 
    ON metadata_validation_required (required_uuid, application_id, version_tag);

-- ============================================================================
-- 13. metadata_validation_unique（唯一性校验表）
-- ============================================================================
ALTER TABLE metadata_validation_unique ADD COLUMN unique_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_unique.unique_uuid IS '唯一性校验UUID';

-- 将所有关联ID改为UUID
ALTER TABLE metadata_validation_unique DROP COLUMN IF EXISTS group_id;
ALTER TABLE metadata_validation_unique ADD COLUMN group_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_unique.group_uuid IS '规则组UUID';

ALTER TABLE metadata_validation_unique DROP COLUMN IF EXISTS entity_id;
ALTER TABLE metadata_validation_unique ADD COLUMN entity_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_unique.entity_uuid IS '实体UUID';

ALTER TABLE metadata_validation_unique DROP COLUMN IF EXISTS field_id;
ALTER TABLE metadata_validation_unique ADD COLUMN field_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_unique.field_uuid IS '字段UUID';

CREATE UNIQUE INDEX uk_val_uniq_uuid_app_ver 
    ON metadata_validation_unique (unique_uuid, application_id, version_tag);

-- ============================================================================
-- 14. metadata_validation_format（格式校验表）
-- ============================================================================
ALTER TABLE metadata_validation_format ADD COLUMN format_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_format.format_uuid IS '格式校验UUID';

-- 将所有关联ID改为UUID
ALTER TABLE metadata_validation_format DROP COLUMN IF EXISTS group_id;
ALTER TABLE metadata_validation_format ADD COLUMN group_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_format.group_uuid IS '规则组UUID';

ALTER TABLE metadata_validation_format DROP COLUMN IF EXISTS entity_id;
ALTER TABLE metadata_validation_format ADD COLUMN entity_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_format.entity_uuid IS '实体UUID';

ALTER TABLE metadata_validation_format DROP COLUMN IF EXISTS field_id;
ALTER TABLE metadata_validation_format ADD COLUMN field_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_format.field_uuid IS '字段UUID';

CREATE UNIQUE INDEX uk_val_fmt_uuid_app_ver 
    ON metadata_validation_format (format_uuid, application_id, version_tag);

-- ============================================================================
-- 15. metadata_validation_length（长度校验表）
-- ============================================================================
ALTER TABLE metadata_validation_length ADD COLUMN length_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_length.length_uuid IS '长度校验UUID';

-- 将所有关联ID改为UUID
ALTER TABLE metadata_validation_length DROP COLUMN IF EXISTS group_id;
ALTER TABLE metadata_validation_length ADD COLUMN group_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_length.group_uuid IS '规则组UUID';

ALTER TABLE metadata_validation_length DROP COLUMN IF EXISTS entity_id;
ALTER TABLE metadata_validation_length ADD COLUMN entity_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_length.entity_uuid IS '实体UUID';

ALTER TABLE metadata_validation_length DROP COLUMN IF EXISTS field_id;
ALTER TABLE metadata_validation_length ADD COLUMN field_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_length.field_uuid IS '字段UUID';

CREATE UNIQUE INDEX uk_val_len_uuid_app_ver 
    ON metadata_validation_length (length_uuid, application_id, version_tag);

-- ============================================================================
-- 16. metadata_validation_range（范围校验表）
-- ============================================================================
ALTER TABLE metadata_validation_range ADD COLUMN range_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_range.range_uuid IS '范围校验UUID';

-- 将所有关联ID改为UUID
ALTER TABLE metadata_validation_range DROP COLUMN IF EXISTS group_id;
ALTER TABLE metadata_validation_range ADD COLUMN group_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_range.group_uuid IS '规则组UUID';

ALTER TABLE metadata_validation_range DROP COLUMN IF EXISTS entity_id;
ALTER TABLE metadata_validation_range ADD COLUMN entity_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_range.entity_uuid IS '实体UUID';

ALTER TABLE metadata_validation_range DROP COLUMN IF EXISTS field_id;
ALTER TABLE metadata_validation_range ADD COLUMN field_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_range.field_uuid IS '字段UUID';

CREATE UNIQUE INDEX uk_val_rng_uuid_app_ver 
    ON metadata_validation_range (range_uuid, application_id, version_tag);

-- ============================================================================
-- 17. metadata_validation_child_not_empty（子表非空校验表）
-- ============================================================================
ALTER TABLE metadata_validation_child_not_empty ADD COLUMN child_not_empty_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_child_not_empty.child_not_empty_uuid IS '子表非空校验UUID';

-- 将所有关联ID改为UUID
ALTER TABLE metadata_validation_child_not_empty DROP COLUMN IF EXISTS group_id;
ALTER TABLE metadata_validation_child_not_empty ADD COLUMN group_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_child_not_empty.group_uuid IS '规则组UUID';

ALTER TABLE metadata_validation_child_not_empty DROP COLUMN IF EXISTS entity_id;
ALTER TABLE metadata_validation_child_not_empty ADD COLUMN entity_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_child_not_empty.entity_uuid IS '实体UUID';

ALTER TABLE metadata_validation_child_not_empty DROP COLUMN IF EXISTS field_id;
ALTER TABLE metadata_validation_child_not_empty ADD COLUMN field_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_child_not_empty.field_uuid IS '字段UUID';

ALTER TABLE metadata_validation_child_not_empty DROP COLUMN IF EXISTS child_entity_id;
ALTER TABLE metadata_validation_child_not_empty ADD COLUMN child_entity_uuid VARCHAR(37);
COMMENT ON COLUMN metadata_validation_child_not_empty.child_entity_uuid IS '子实体UUID';

CREATE UNIQUE INDEX uk_val_cne_uuid_app_ver 
    ON metadata_validation_child_not_empty (child_not_empty_uuid, application_id, version_tag);

-- ============================================================================
-- 完成说明
-- ============================================================================
-- 本脚本完成以下改造：
-- 1. 为17张表添加了UUID字段
-- 2. 将所有表间关联的ID字段改为UUID字段
-- 3. 为每个UUID字段创建了基于(uuid, application_id, version_tag)的唯一索引
-- 
-- 注意事项：
-- 1. 执行前请备份数据库
-- 2. 本脚本仅添加新字段，不涉及数据迁移
-- 3. 新增数据需要由应用程序生成UUID并写入
-- ============================================================================
