-- OneBase Flow 模块启动问题修复SQL
-- 问题：流程定义为空导致启动失败
-- 日期：2025-12-02

-- 方案1：禁用问题流程（推荐）
UPDATE flow_process
SET enable_status = 0
WHERE id = 162636513213218816
  AND process_definition IS NULL;

-- 验证：查看所有 processDefinition 为空的流程
SELECT id, process_name, process_uuid, enable_status, create_time, update_time
FROM flow_process
WHERE process_definition IS NULL;

-- 方案2：删除测试数据（如果确认是测试数据）
-- DELETE FROM flow_process WHERE id = 162636513213218816;

-- 预防：查找所有启用但定义为空的流程
SELECT id, process_name, application_id, tenant_id, enable_status
FROM flow_process
WHERE enable_status = 1
  AND (process_definition IS NULL OR process_definition = '');
