CREATE TABLE app_custom_button (
    id BIGINT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    tenant_id BIGINT,
    version_tag BIGINT,
    menu_uuid VARCHAR(64) NOT NULL,
    pageset_uuid VARCHAR(64) NOT NULL,
    page_uuid VARCHAR(64),
    button_uuid VARCHAR(64) NOT NULL,
    button_code VARCHAR(64) NOT NULL,
    button_name VARCHAR(64) NOT NULL,
    button_desc VARCHAR(512),
    show_desc INTEGER DEFAULT 1,
    style_type VARCHAR(32),
    color_hex VARCHAR(16),
    color_alpha INTEGER,
    icon_code VARCHAR(128),
    operation_scope VARCHAR(32) NOT NULL,
    show_in_form INTEGER DEFAULT 0,
    show_in_row_action INTEGER DEFAULT 0,
    show_in_batch_action INTEGER DEFAULT 0,
    action_type VARCHAR(64) NOT NULL,
    sort_no INTEGER DEFAULT 0,
    status VARCHAR(32) DEFAULT 'ENABLE',
    creator BIGINT,
    create_time TIMESTAMP,
    updater BIGINT,
    update_time TIMESTAMP,
    deleted BIGINT DEFAULT 0,
    lock_version BIGINT DEFAULT 0
);
CREATE UNIQUE INDEX uk_app_custom_button_code ON app_custom_button(application_id, button_code);
CREATE UNIQUE INDEX uk_app_custom_button_name ON app_custom_button(pageset_uuid, button_name, deleted);
CREATE INDEX idx_app_custom_button_pageset ON app_custom_button(pageset_uuid, status, sort_no);

CREATE TABLE app_custom_button_condition_group (
    id BIGINT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    tenant_id BIGINT,
    version_tag BIGINT,
    button_uuid VARCHAR(64) NOT NULL,
    group_no INTEGER DEFAULT 0,
    logic_type VARCHAR(16) NOT NULL,
    sort_no INTEGER DEFAULT 0,
    creator BIGINT,
    create_time TIMESTAMP,
    updater BIGINT,
    update_time TIMESTAMP,
    deleted BIGINT DEFAULT 0,
    lock_version BIGINT DEFAULT 0
);
CREATE INDEX idx_app_custom_button_cond_group_btn ON app_custom_button_condition_group(button_uuid, sort_no);

CREATE TABLE app_custom_button_condition_item (
    id BIGINT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    tenant_id BIGINT,
    version_tag BIGINT,
    button_uuid VARCHAR(64) NOT NULL,
    group_id BIGINT NOT NULL,
    field_uuid VARCHAR(64),
    field_code VARCHAR(128),
    operator VARCHAR(32) NOT NULL,
    value_type VARCHAR(32) NOT NULL,
    compare_value CLOB,
    sort_no INTEGER DEFAULT 0,
    creator BIGINT,
    create_time TIMESTAMP,
    updater BIGINT,
    update_time TIMESTAMP,
    deleted BIGINT DEFAULT 0,
    lock_version BIGINT DEFAULT 0
);
CREATE INDEX idx_app_custom_button_cond_item_btn ON app_custom_button_condition_item(button_uuid, group_id, sort_no);

CREATE TABLE app_custom_button_action_update (
    id BIGINT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    tenant_id BIGINT,
    version_tag BIGINT,
    button_uuid VARCHAR(64) NOT NULL,
    open_mode VARCHAR(32),
    submit_success_text VARCHAR(255),
    creator BIGINT,
    create_time TIMESTAMP,
    updater BIGINT,
    update_time TIMESTAMP,
    deleted BIGINT DEFAULT 0,
    lock_version BIGINT DEFAULT 0
);
CREATE UNIQUE INDEX uk_app_custom_button_action_update_btn ON app_custom_button_action_update(button_uuid);

CREATE TABLE app_custom_button_update_edit_field (
    id BIGINT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    tenant_id BIGINT,
    version_tag BIGINT,
    button_uuid VARCHAR(64) NOT NULL,
    field_uuid VARCHAR(64) NOT NULL,
    field_code VARCHAR(128),
    required_flag INTEGER DEFAULT 0,
    sort_no INTEGER DEFAULT 0,
    creator BIGINT,
    create_time TIMESTAMP,
    updater BIGINT,
    update_time TIMESTAMP,
    deleted BIGINT DEFAULT 0,
    lock_version BIGINT DEFAULT 0
);
CREATE INDEX idx_app_custom_button_update_edit_field_btn ON app_custom_button_update_edit_field(button_uuid, sort_no);

CREATE TABLE app_custom_button_update_auto_field (
    id BIGINT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    tenant_id BIGINT,
    version_tag BIGINT,
    button_uuid VARCHAR(64) NOT NULL,
    field_uuid VARCHAR(64) NOT NULL,
    field_code VARCHAR(128),
    value_type VARCHAR(32) NOT NULL,
    value_config CLOB,
    sort_no INTEGER DEFAULT 0,
    creator BIGINT,
    create_time TIMESTAMP,
    updater BIGINT,
    update_time TIMESTAMP,
    deleted BIGINT DEFAULT 0,
    lock_version BIGINT DEFAULT 0
);
CREATE INDEX idx_app_custom_button_update_auto_field_btn ON app_custom_button_update_auto_field(button_uuid, sort_no);

CREATE TABLE app_custom_button_action_create_related (
    id BIGINT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    tenant_id BIGINT,
    version_tag BIGINT,
    button_uuid VARCHAR(64) NOT NULL,
    target_pageset_uuid VARCHAR(64),
    target_page_uuid VARCHAR(64),
    target_entity_uuid VARCHAR(64),
    target_relation_field_uuid VARCHAR(64),
    target_relation_scope VARCHAR(32),
    open_mode VARCHAR(32),
    creator BIGINT,
    create_time TIMESTAMP,
    updater BIGINT,
    update_time TIMESTAMP,
    deleted BIGINT DEFAULT 0,
    lock_version BIGINT DEFAULT 0
);
CREATE UNIQUE INDEX uk_app_custom_button_action_create_related_btn ON app_custom_button_action_create_related(button_uuid);

CREATE TABLE app_custom_button_action_flow (
    id BIGINT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    tenant_id BIGINT,
    version_tag BIGINT,
    button_uuid VARCHAR(64) NOT NULL,
    flow_process_id BIGINT NOT NULL,
    flow_process_uuid VARCHAR(64),
    confirm_required INTEGER DEFAULT 1,
    confirm_text VARCHAR(512),
    creator BIGINT,
    create_time TIMESTAMP,
    updater BIGINT,
    update_time TIMESTAMP,
    deleted BIGINT DEFAULT 0,
    lock_version BIGINT DEFAULT 0
);
CREATE UNIQUE INDEX uk_app_custom_button_action_flow_btn ON app_custom_button_action_flow(button_uuid);
CREATE INDEX idx_app_custom_button_action_flow_process ON app_custom_button_action_flow(flow_process_uuid);

CREATE TABLE app_custom_button_action_open_page (
    id BIGINT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    tenant_id BIGINT,
    version_tag BIGINT,
    button_uuid VARCHAR(64) NOT NULL,
    target_type VARCHAR(32) NOT NULL,
    target_pageset_uuid VARCHAR(64),
    target_page_uuid VARCHAR(64),
    target_url VARCHAR(2048),
    open_mode VARCHAR(32),
    creator BIGINT,
    create_time TIMESTAMP,
    updater BIGINT,
    update_time TIMESTAMP,
    deleted BIGINT DEFAULT 0,
    lock_version BIGINT DEFAULT 0
);
CREATE UNIQUE INDEX uk_app_custom_button_action_open_page_btn ON app_custom_button_action_open_page(button_uuid);

CREATE TABLE app_custom_button_open_param (
    id BIGINT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    tenant_id BIGINT,
    version_tag BIGINT,
    button_uuid VARCHAR(64) NOT NULL,
    param_name VARCHAR(64) NOT NULL,
    param_source_type VARCHAR(32) NOT NULL,
    param_value_config CLOB,
    sort_no INTEGER DEFAULT 0,
    creator BIGINT,
    create_time TIMESTAMP,
    updater BIGINT,
    update_time TIMESTAMP,
    deleted BIGINT DEFAULT 0,
    lock_version BIGINT DEFAULT 0
);
CREATE INDEX idx_app_custom_button_open_param_btn ON app_custom_button_open_param(button_uuid, sort_no);

CREATE TABLE app_page_link_param_config (
    id BIGINT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    tenant_id BIGINT,
    version_tag BIGINT,
    pageset_uuid VARCHAR(64) NOT NULL,
    page_uuid VARCHAR(64) NOT NULL,
    field_uuid VARCHAR(64) NOT NULL,
    field_code VARCHAR(128),
    param_name VARCHAR(64) NOT NULL,
    field_type VARCHAR(32),
    sort_no INTEGER DEFAULT 0,
    status VARCHAR(32) DEFAULT 'ENABLE',
    creator BIGINT,
    create_time TIMESTAMP,
    updater BIGINT,
    update_time TIMESTAMP,
    deleted BIGINT DEFAULT 0,
    lock_version BIGINT DEFAULT 0
);
CREATE UNIQUE INDEX uk_app_page_link_param_config_page_param ON app_page_link_param_config(page_uuid, param_name, deleted);
CREATE INDEX idx_app_page_link_param_config_pageset ON app_page_link_param_config(pageset_uuid, page_uuid, status);

CREATE TABLE app_custom_button_exec_log (
    id BIGINT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    tenant_id BIGINT,
    version_tag BIGINT,
    button_uuid VARCHAR(64) NOT NULL,
    button_code VARCHAR(64) NOT NULL,
    button_name VARCHAR(64),
    action_type VARCHAR(64) NOT NULL,
    operator_user_id BIGINT,
    operator_dept_id BIGINT,
    menu_uuid VARCHAR(64),
    pageset_uuid VARCHAR(64),
    page_uuid VARCHAR(64),
    record_id VARCHAR(64),
    batch_no VARCHAR(64),
    operation_scope VARCHAR(32),
    exec_status VARCHAR(32) NOT NULL,
    error_code VARCHAR(64),
    error_message VARCHAR(1024),
    request_snapshot CLOB,
    response_snapshot CLOB,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    duration_ms BIGINT,
    creator BIGINT,
    create_time TIMESTAMP,
    updater BIGINT,
    update_time TIMESTAMP,
    deleted BIGINT DEFAULT 0,
    lock_version BIGINT DEFAULT 0
);
CREATE INDEX idx_app_custom_button_exec_log_btn_time ON app_custom_button_exec_log(button_uuid, create_time);
CREATE INDEX idx_app_custom_button_exec_log_user_time ON app_custom_button_exec_log(operator_user_id, create_time);
CREATE INDEX idx_app_custom_button_exec_log_batch ON app_custom_button_exec_log(batch_no);

CREATE TABLE app_custom_button_exec_detail (
    id BIGINT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    tenant_id BIGINT,
    version_tag BIGINT,
    exec_log_id BIGINT NOT NULL,
    batch_no VARCHAR(64),
    record_id VARCHAR(64),
    exec_status VARCHAR(32) NOT NULL,
    error_code VARCHAR(64),
    error_message VARCHAR(1024),
    result_snapshot CLOB,
    creator BIGINT,
    create_time TIMESTAMP,
    updater BIGINT,
    update_time TIMESTAMP,
    deleted BIGINT DEFAULT 0,
    lock_version BIGINT DEFAULT 0
);
CREATE INDEX idx_app_custom_button_exec_detail_log ON app_custom_button_exec_detail(exec_log_id);
CREATE INDEX idx_app_custom_button_exec_detail_batch ON app_custom_button_exec_detail(batch_no, record_id);
