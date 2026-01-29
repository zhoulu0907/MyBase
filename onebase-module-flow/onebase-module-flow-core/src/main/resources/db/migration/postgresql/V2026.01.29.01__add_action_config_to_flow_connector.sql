-- 添加 action_config 字段到 flow_connector 表
ALTER TABLE flow_connector
ADD COLUMN action_config TEXT;

-- 添加字段注释
COMMENT ON COLUMN flow_connector.action_config IS '动作配置（JSON格式），使用Formily Schema存储每个动作的分步表单配置';

-- 创建索引以提升 JSON 查询性能
CREATE INDEX idx_connector_action_config
ON flow_connector((action_config::json->>'metadata'));
