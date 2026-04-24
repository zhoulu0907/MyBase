BEGIN;

-- 自定义按钮测试数据：可重复执行，仅清理本脚本使用的测试应用与按钮数据。
DELETE FROM app_custom_button_exec_detail
WHERE application_id = 900000000001;

DELETE FROM app_custom_button_exec_log
WHERE application_id = 900000000001;

DELETE FROM app_page_link_param_config
WHERE application_id = 900000000001;

DELETE FROM app_custom_button_open_param
WHERE application_id = 900000000001;

DELETE FROM app_custom_button_update_field
WHERE application_id = 900000000001;

DELETE FROM app_custom_button_action_config
WHERE application_id = 900000000001;

DELETE FROM app_custom_button_condition_item
WHERE application_id = 900000000001;

DELETE FROM app_custom_button_condition_group
WHERE application_id = 900000000001;

DELETE FROM app_custom_button
WHERE application_id = 900000000001;

INSERT INTO app_custom_button (
    id, application_id, tenant_id, version_tag, menu_uuid, pageset_uuid, page_uuid,
    button_uuid, button_code, button_name, button_desc, show_desc, style_type,
    color_hex, color_alpha, icon_code, operation_scope, show_in_form,
    show_in_row_action, show_in_batch_action, action_type, sort_no, status,
    creator, create_time, updater, update_time, deleted, lock_version
) VALUES
    (900000000001, 900000000001, 1, 2026042401, 'menu-test-order', 'pageset-test-order', 'page-test-order-list',
     'btn-test-update-order', 'TEST_UPDATE_ORDER', '测试-修改当前订单', '打开弹窗修改订单状态和备注', 1, 'PRIMARY',
     '#1677FF', 100, 'edit', 'ROW', 0, 1, 0, 'UPDATE_FORM', 10, 'ENABLE',
     1, now(), 1, now(), 0, 0),
    (900000000002, 900000000001, 1, 2026042401, 'menu-test-order', 'pageset-test-order', 'page-test-order-list',
     'btn-test-create-related', 'TEST_CREATE_REFUND', '测试-新建退款单', '基于订单创建关联退款记录', 1, 'DEFAULT',
     '#13C2C2', 100, 'plus', 'ROW', 0, 1, 0, 'CREATE_RELATED_RECORD', 20, 'ENABLE',
     1, now(), 1, now(), 0, 0),
    (900000000003, 900000000001, 1, 2026042401, 'menu-test-order', 'pageset-test-order', 'page-test-order-list',
     'btn-test-trigger-flow', 'TEST_TRIGGER_AUDIT_FLOW', '测试-触发审批流', '批量触发订单审批自动化流', 1, 'WARNING',
     '#FAAD14', 100, 'play-circle', 'BATCH', 0, 0, 1, 'TRIGGER_FLOW', 30, 'ENABLE',
     1, now(), 1, now(), 0, 0),
    (900000000004, 900000000001, 1, 2026042401, 'menu-test-order', 'pageset-test-order', 'page-test-order-list',
     'btn-test-open-page', 'TEST_OPEN_CUSTOMER', '测试-打开客户详情', '打开客户详情页并传入客户ID', 1, 'LINK',
     '#722ED1', 100, 'link', 'ROW', 0, 1, 0, 'OPEN_PAGE', 40, 'ENABLE',
     1, now(), 1, now(), 0, 0);

INSERT INTO app_custom_button_condition_group (
    id, application_id, tenant_id, version_tag, button_uuid, group_no, logic_type,
    sort_no, creator, create_time, updater, update_time, deleted, lock_version
) VALUES
    (900000001001, 900000000001, 1, 2026042401, 'btn-test-update-order', 1, 'AND',
     10, 1, now(), 1, now(), 0, 0),
    (900000001002, 900000000001, 1, 2026042401, 'btn-test-trigger-flow', 1, 'AND',
     10, 1, now(), 1, now(), 0, 0),
    (900000001003, 900000000001, 1, 2026042401, 'btn-test-open-page', 1, 'OR',
     10, 1, now(), 1, now(), 0, 0);

INSERT INTO app_custom_button_condition_item (
    id, application_id, tenant_id, version_tag, button_uuid, group_id, field_uuid,
    field_code, operator, value_type, compare_value, sort_no, creator, create_time,
    updater, update_time, deleted, lock_version
) VALUES
    (900000002001, 900000000001, 1, 2026042401, 'btn-test-update-order', 900000001001, 'field-test-order-status',
     'order_status', 'IN', 'CONST', '["PENDING","PROCESSING"]', 10, 1, now(), 1, now(), 0, 0),
    (900000002002, 900000000001, 1, 2026042401, 'btn-test-update-order', 900000001001, 'field-test-order-owner',
     'owner_user_id', 'EQ', 'CURRENT_USER', null, 20, 1, now(), 1, now(), 0, 0),
    (900000002003, 900000000001, 1, 2026042401, 'btn-test-trigger-flow', 900000001002, 'field-test-order-amount',
     'order_amount', 'GT', 'CONST', '1000', 10, 1, now(), 1, now(), 0, 0),
    (900000002004, 900000000001, 1, 2026042401, 'btn-test-open-page', 900000001003, 'field-test-customer-id',
     'customer_id', 'NOT_EMPTY', 'CONST', null, 10, 1, now(), 1, now(), 0, 0);

INSERT INTO app_custom_button_action_config (
    id, application_id, tenant_id, version_tag, button_uuid, action_type,
    open_mode, submit_success_text, creator, create_time, updater, update_time,
    deleted, lock_version
) VALUES
    (900000003001, 900000000001, 1, 2026042401, 'btn-test-update-order', 'UPDATE_FORM', 'DIALOG',
     '订单已更新', 1, now(), 1, now(), 0, 0);

INSERT INTO app_custom_button_update_field (
    id, application_id, tenant_id, version_tag, button_uuid, field_mode,
    field_uuid, field_code, required_flag, sort_no, creator, create_time,
    updater, update_time, deleted, lock_version
) VALUES
    (900000004001, 900000000001, 1, 2026042401, 'btn-test-update-order', 'EDIT', 'field-test-order-status', 'order_status',
     1, 10, 1, now(), 1, now(), 0, 0),
    (900000004002, 900000000001, 1, 2026042401, 'btn-test-update-order', 'EDIT', 'field-test-order-remark', 'order_remark',
     0, 20, 1, now(), 1, now(), 0, 0);

INSERT INTO app_custom_button_update_field (
    id, application_id, tenant_id, version_tag, button_uuid, field_mode,
    field_uuid, field_code, required_flag, value_type, value_config, sort_no,
    creator, create_time, updater, update_time, deleted, lock_version
) VALUES
    (900000005001, 900000000001, 1, 2026042401, 'btn-test-update-order', 'AUTO', 'field-test-updated-by', 'updated_by', 0,
     'CURRENT_USER', null, 10, 1, now(), 1, now(), 0, 0),
    (900000005002, 900000000001, 1, 2026042401, 'btn-test-update-order', 'AUTO', 'field-test-updated-time', 'updated_time', 0,
     'CURRENT_TIME', null, 20, 1, now(), 1, now(), 0, 0);

INSERT INTO app_custom_button_action_config (
    id, application_id, tenant_id, version_tag, button_uuid, target_pageset_uuid,
    target_page_uuid, target_entity_uuid, target_relation_field_uuid,
    target_relation_scope, action_type, open_mode, creator, create_time, updater,
    update_time, deleted, lock_version
) VALUES
    (900000006001, 900000000001, 1, 2026042401, 'btn-test-create-related', 'pageset-test-refund',
     'page-test-refund-edit', 'entity-test-refund', 'field-test-refund-order-id',
     'CURRENT_RECORD', 'CREATE_RELATED_RECORD', 'DIALOG', 1, now(), 1, now(), 0, 0);

INSERT INTO app_custom_button_action_config (
    id, application_id, tenant_id, version_tag, button_uuid, flow_process_id,
    flow_process_uuid, confirm_required, confirm_text, action_type, creator,
    create_time, updater, update_time, deleted, lock_version
) VALUES
    (900000007001, 900000000001, 1, 2026042401, 'btn-test-trigger-flow', 900000070001,
     'flow-test-order-audit', 1, '确认要批量触发订单审批流程吗？', 'TRIGGER_FLOW', 1, now(), 1, now(), 0, 0);

INSERT INTO app_custom_button_action_config (
    id, application_id, tenant_id, version_tag, button_uuid, target_type,
    target_pageset_uuid, target_page_uuid, target_url, open_mode, creator,
    create_time, updater, update_time, deleted, lock_version, action_type
) VALUES
    (900000008001, 900000000001, 1, 2026042401, 'btn-test-open-page', 'INNER_PAGE',
     'pageset-test-customer', 'page-test-customer-detail', null, 'NEW_TAB',
     1, now(), 1, now(), 0, 0, 'OPEN_PAGE');

INSERT INTO app_custom_button_open_param (
    id, application_id, tenant_id, version_tag, button_uuid, param_name,
    param_source_type, param_value_config, sort_no, creator, create_time, updater,
    update_time, deleted, lock_version
) VALUES
    (900000009001, 900000000001, 1, 2026042401, 'btn-test-open-page', 'customerId',
     'FIELD', '{"fieldUuid":"field-test-customer-id","fieldCode":"customer_id"}', 10,
     1, now(), 1, now(), 0, 0),
    (900000009002, 900000000001, 1, 2026042401, 'btn-test-open-page', 'fromButton',
     'CONST', '{"value":"TEST_OPEN_CUSTOMER"}', 20,
     1, now(), 1, now(), 0, 0);

INSERT INTO app_page_link_param_config (
    id, application_id, tenant_id, version_tag, pageset_uuid, page_uuid, field_uuid,
    field_code, param_name, field_type, sort_no, status, creator, create_time,
    updater, update_time, deleted, lock_version
) VALUES
    (900000010001, 900000000001, 1, 2026042401, 'pageset-test-customer', 'page-test-customer-detail', 'field-test-customer-id',
     'customer_id', 'customerId', 'TEXT', 10, 'ENABLE', 1, now(), 1, now(), 0, 0),
    (900000010002, 900000000001, 1, 2026042401, 'pageset-test-refund', 'page-test-refund-edit', 'field-test-refund-order-id',
     'order_id', 'orderId', 'TEXT', 10, 'ENABLE', 1, now(), 1, now(), 0, 0);

INSERT INTO app_custom_button_exec_log (
    id, application_id, tenant_id, version_tag, button_uuid, button_code, button_name,
    action_type, operator_user_id, operator_dept_id, menu_uuid, pageset_uuid,
    page_uuid, record_id, batch_no, operation_scope, exec_status, error_code,
    error_message, request_snapshot, response_snapshot, start_time, end_time,
    duration_ms, creator, create_time, updater, update_time, deleted, lock_version
) VALUES
    (900000011001, 900000000001, 1, 2026042401, 'btn-test-update-order', 'TEST_UPDATE_ORDER', '测试-修改当前订单',
     'UPDATE_FORM', 1, 100, 'menu-test-order', 'pageset-test-order',
     'page-test-order-list', 'order-test-001', null, 'ROW', 'SUCCESS', null,
     null, '{"recordId":"order-test-001"}', '{"updated":true}', now() - interval '3 minutes',
     now() - interval '2 minutes 59 seconds', 1000, 1, now(), 1, now(), 0, 0),
    (900000011002, 900000000001, 1, 2026042401, 'btn-test-trigger-flow', 'TEST_TRIGGER_AUDIT_FLOW', '测试-触发审批流',
     'TRIGGER_FLOW', 1, 100, 'menu-test-order', 'pageset-test-order',
     'page-test-order-list', null, 'batch-test-001', 'BATCH', 'PART_SUCCESS', 'PART_RECORD_FAILED',
     '部分记录触发失败', '{"recordIds":["order-test-002","order-test-003"]}', '{"success":1,"failed":1}',
     now() - interval '2 minutes', now() - interval '1 minute 58 seconds', 2000, 1, now(), 1, now(), 0, 0);

INSERT INTO app_custom_button_exec_detail (
    id, application_id, tenant_id, version_tag, exec_log_id, batch_no, record_id,
    exec_status, error_code, error_message, result_snapshot, creator, create_time,
    updater, update_time, deleted, lock_version
) VALUES
    (900000012001, 900000000001, 1, 2026042401, 900000011002, 'batch-test-001', 'order-test-002',
     'SUCCESS', null, null, '{"flowInstanceId":"flow-inst-test-001"}', 1, now(), 1, now(), 0, 0),
    (900000012002, 900000000001, 1, 2026042401, 900000011002, 'batch-test-001', 'order-test-003',
     'FAIL', 'FLOW_START_FAILED', '流程启动失败', '{"reason":"mock failure"}', 1, now(), 1, now(), 0, 0);

COMMIT;
