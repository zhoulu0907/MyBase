BEGIN;

CREATE TABLE IF NOT EXISTS app_custom_button_action_config (
    id BIGINT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    tenant_id BIGINT,
    version_tag BIGINT,
    button_uuid VARCHAR(64) NOT NULL,
    action_type VARCHAR(64) NOT NULL,
    open_mode VARCHAR(32),
    submit_success_text VARCHAR(255),
    target_type VARCHAR(32),
    target_pageset_uuid VARCHAR(64),
    target_page_uuid VARCHAR(64),
    target_url VARCHAR(2048),
    target_entity_uuid VARCHAR(64),
    target_relation_field_uuid VARCHAR(64),
    target_relation_scope VARCHAR(32),
    flow_process_id BIGINT,
    flow_process_uuid VARCHAR(64),
    confirm_required INTEGER DEFAULT 1,
    confirm_text VARCHAR(512),
    config_json TEXT,
    creator BIGINT,
    create_time TIMESTAMP,
    updater BIGINT,
    update_time TIMESTAMP,
    deleted BIGINT DEFAULT 0,
    lock_version BIGINT DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_app_custom_button_action_config_btn
    ON app_custom_button_action_config(button_uuid);
CREATE INDEX IF NOT EXISTS idx_app_custom_button_action_config_flow
    ON app_custom_button_action_config(flow_process_uuid);
CREATE INDEX IF NOT EXISTS idx_app_custom_button_action_config_target_page
    ON app_custom_button_action_config(target_pageset_uuid, target_page_uuid);

CREATE TABLE IF NOT EXISTS app_custom_button_update_field (
    id BIGINT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    tenant_id BIGINT,
    version_tag BIGINT,
    button_uuid VARCHAR(64) NOT NULL,
    field_mode VARCHAR(16) NOT NULL,
    field_uuid VARCHAR(64) NOT NULL,
    field_code VARCHAR(128),
    required_flag INTEGER DEFAULT 0,
    value_type VARCHAR(32),
    value_config TEXT,
    sort_no INTEGER DEFAULT 0,
    creator BIGINT,
    create_time TIMESTAMP,
    updater BIGINT,
    update_time TIMESTAMP,
    deleted BIGINT DEFAULT 0,
    lock_version BIGINT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_app_custom_button_update_field_btn
    ON app_custom_button_update_field(button_uuid, field_mode, sort_no);

INSERT INTO app_custom_button_action_config (
    id, application_id, tenant_id, version_tag, button_uuid, action_type,
    open_mode, submit_success_text, creator, create_time, updater, update_time,
    deleted, lock_version
)
SELECT
    id, application_id, tenant_id, version_tag, button_uuid, 'UPDATE_FORM',
    open_mode, submit_success_text, creator, create_time, updater, update_time,
    deleted, lock_version
FROM app_custom_button_action_update old_action
WHERE NOT EXISTS (
    SELECT 1 FROM app_custom_button_action_config new_action
    WHERE new_action.button_uuid = old_action.button_uuid
);

INSERT INTO app_custom_button_action_config (
    id, application_id, tenant_id, version_tag, button_uuid, action_type,
    target_pageset_uuid, target_page_uuid, target_entity_uuid,
    target_relation_field_uuid, target_relation_scope, open_mode,
    creator, create_time, updater, update_time, deleted, lock_version
)
SELECT
    id, application_id, tenant_id, version_tag, button_uuid, 'CREATE_RELATED_RECORD',
    target_pageset_uuid, target_page_uuid, target_entity_uuid,
    target_relation_field_uuid, target_relation_scope, open_mode,
    creator, create_time, updater, update_time, deleted, lock_version
FROM app_custom_button_action_create_related old_action
WHERE NOT EXISTS (
    SELECT 1 FROM app_custom_button_action_config new_action
    WHERE new_action.button_uuid = old_action.button_uuid
);

INSERT INTO app_custom_button_action_config (
    id, application_id, tenant_id, version_tag, button_uuid, action_type,
    flow_process_id, flow_process_uuid, confirm_required, confirm_text,
    creator, create_time, updater, update_time, deleted, lock_version
)
SELECT
    id, application_id, tenant_id, version_tag, button_uuid, 'TRIGGER_FLOW',
    flow_process_id, flow_process_uuid, confirm_required, confirm_text,
    creator, create_time, updater, update_time, deleted, lock_version
FROM app_custom_button_action_flow old_action
WHERE NOT EXISTS (
    SELECT 1 FROM app_custom_button_action_config new_action
    WHERE new_action.button_uuid = old_action.button_uuid
);

INSERT INTO app_custom_button_action_config (
    id, application_id, tenant_id, version_tag, button_uuid, action_type,
    target_type, target_pageset_uuid, target_page_uuid, target_url, open_mode,
    creator, create_time, updater, update_time, deleted, lock_version
)
SELECT
    id, application_id, tenant_id, version_tag, button_uuid, 'OPEN_PAGE',
    target_type, target_pageset_uuid, target_page_uuid, target_url, open_mode,
    creator, create_time, updater, update_time, deleted, lock_version
FROM app_custom_button_action_open_page old_action
WHERE NOT EXISTS (
    SELECT 1 FROM app_custom_button_action_config new_action
    WHERE new_action.button_uuid = old_action.button_uuid
);

INSERT INTO app_custom_button_update_field (
    id, application_id, tenant_id, version_tag, button_uuid, field_mode,
    field_uuid, field_code, required_flag, sort_no, creator, create_time,
    updater, update_time, deleted, lock_version
)
SELECT
    id, application_id, tenant_id, version_tag, button_uuid, 'EDIT',
    field_uuid, field_code, required_flag, sort_no, creator, create_time,
    updater, update_time, deleted, lock_version
FROM app_custom_button_update_edit_field old_field
WHERE NOT EXISTS (
    SELECT 1 FROM app_custom_button_update_field new_field
    WHERE new_field.id = old_field.id
);

INSERT INTO app_custom_button_update_field (
    id, application_id, tenant_id, version_tag, button_uuid, field_mode,
    field_uuid, field_code, required_flag, value_type, value_config, sort_no,
    creator, create_time, updater, update_time, deleted, lock_version
)
SELECT
    id, application_id, tenant_id, version_tag, button_uuid, 'AUTO',
    field_uuid, field_code, 0, value_type, value_config, sort_no,
    creator, create_time, updater, update_time, deleted, lock_version
FROM app_custom_button_update_auto_field old_field
WHERE NOT EXISTS (
    SELECT 1 FROM app_custom_button_update_field new_field
    WHERE new_field.id = old_field.id
);

DROP TABLE IF EXISTS app_custom_button_action_update;
DROP TABLE IF EXISTS app_custom_button_action_create_related;
DROP TABLE IF EXISTS app_custom_button_action_flow;
DROP TABLE IF EXISTS app_custom_button_action_open_page;
DROP TABLE IF EXISTS app_custom_button_update_edit_field;
DROP TABLE IF EXISTS app_custom_button_update_auto_field;

COMMENT ON TABLE app_custom_button_action_config IS '自定义按钮统一动作配置表';
COMMENT ON COLUMN app_custom_button_action_config.id IS '主键ID';
COMMENT ON COLUMN app_custom_button_action_config.application_id IS '应用ID';
COMMENT ON COLUMN app_custom_button_action_config.tenant_id IS '租户ID';
COMMENT ON COLUMN app_custom_button_action_config.version_tag IS '版本标识';
COMMENT ON COLUMN app_custom_button_action_config.button_uuid IS '按钮UUID';
COMMENT ON COLUMN app_custom_button_action_config.action_type IS '动作类型：UPDATE_FORM/CREATE_RELATED_RECORD/TRIGGER_FLOW/OPEN_PAGE';
COMMENT ON COLUMN app_custom_button_action_config.open_mode IS '打开方式';
COMMENT ON COLUMN app_custom_button_action_config.submit_success_text IS '提交成功提示文案';
COMMENT ON COLUMN app_custom_button_action_config.target_type IS '目标类型：INNER_PAGE/OUTER_URL';
COMMENT ON COLUMN app_custom_button_action_config.target_pageset_uuid IS '目标页面集UUID';
COMMENT ON COLUMN app_custom_button_action_config.target_page_uuid IS '目标页面UUID';
COMMENT ON COLUMN app_custom_button_action_config.target_url IS '目标外部链接';
COMMENT ON COLUMN app_custom_button_action_config.target_entity_uuid IS '目标实体UUID';
COMMENT ON COLUMN app_custom_button_action_config.target_relation_field_uuid IS '目标关联字段UUID';
COMMENT ON COLUMN app_custom_button_action_config.target_relation_scope IS '目标关联范围';
COMMENT ON COLUMN app_custom_button_action_config.flow_process_id IS '自动化流流程ID';
COMMENT ON COLUMN app_custom_button_action_config.flow_process_uuid IS '自动化流流程UUID';
COMMENT ON COLUMN app_custom_button_action_config.confirm_required IS '是否需要二次确认：0-否，1-是';
COMMENT ON COLUMN app_custom_button_action_config.confirm_text IS '二次确认文案';
COMMENT ON COLUMN app_custom_button_action_config.config_json IS '扩展配置JSON';
COMMENT ON COLUMN app_custom_button_action_config.creator IS '创建人';
COMMENT ON COLUMN app_custom_button_action_config.create_time IS '创建时间';
COMMENT ON COLUMN app_custom_button_action_config.updater IS '更新人';
COMMENT ON COLUMN app_custom_button_action_config.update_time IS '更新时间';
COMMENT ON COLUMN app_custom_button_action_config.deleted IS '删除标识';
COMMENT ON COLUMN app_custom_button_action_config.lock_version IS '乐观锁版本号';

COMMENT ON TABLE app_custom_button_update_field IS '自定义按钮修改当前表单字段配置表';
COMMENT ON COLUMN app_custom_button_update_field.id IS '主键ID';
COMMENT ON COLUMN app_custom_button_update_field.application_id IS '应用ID';
COMMENT ON COLUMN app_custom_button_update_field.tenant_id IS '租户ID';
COMMENT ON COLUMN app_custom_button_update_field.version_tag IS '版本标识';
COMMENT ON COLUMN app_custom_button_update_field.button_uuid IS '按钮UUID';
COMMENT ON COLUMN app_custom_button_update_field.field_mode IS '字段模式：EDIT-用户可编辑，AUTO-系统自动更新';
COMMENT ON COLUMN app_custom_button_update_field.field_uuid IS '字段UUID';
COMMENT ON COLUMN app_custom_button_update_field.field_code IS '字段编码';
COMMENT ON COLUMN app_custom_button_update_field.required_flag IS '是否必填：0-否，1-是';
COMMENT ON COLUMN app_custom_button_update_field.value_type IS '自动更新值类型';
COMMENT ON COLUMN app_custom_button_update_field.value_config IS '自动更新值配置';
COMMENT ON COLUMN app_custom_button_update_field.sort_no IS '排序号';
COMMENT ON COLUMN app_custom_button_update_field.creator IS '创建人';
COMMENT ON COLUMN app_custom_button_update_field.create_time IS '创建时间';
COMMENT ON COLUMN app_custom_button_update_field.updater IS '更新人';
COMMENT ON COLUMN app_custom_button_update_field.update_time IS '更新时间';
COMMENT ON COLUMN app_custom_button_update_field.deleted IS '删除标识';
COMMENT ON COLUMN app_custom_button_update_field.lock_version IS '乐观锁版本号';

COMMIT;
