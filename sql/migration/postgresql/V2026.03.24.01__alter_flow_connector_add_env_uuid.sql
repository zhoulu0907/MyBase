-- 为 flow_connector 表添加 env_uuid 字段
-- 用于关联 flow_connector_env 表中的环境配置

ALTER TABLE flow_connector
ADD COLUMN IF NOT EXISTS env_uuid VARCHAR(64);

COMMENT ON COLUMN flow_connector.env_uuid IS '启用的环境配置UUID，关联 flow_connector_env.env_uuid';

-- 创建索引以提升查询性能
CREATE INDEX IF NOT EXISTS idx_flow_connector_env_uuid ON flow_connector(env_uuid);