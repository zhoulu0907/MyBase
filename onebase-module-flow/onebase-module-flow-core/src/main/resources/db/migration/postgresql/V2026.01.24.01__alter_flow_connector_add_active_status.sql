-- 为 flow_connector 表添加 active_status 字段
-- 支持连接器实例的启用/禁用状态管理

ALTER TABLE flow_connector
ADD COLUMN IF NOT EXISTS active_status INT DEFAULT 1;

COMMENT ON COLUMN flow_connector.active_status IS '启用状态（0-禁用，1-启用）';
