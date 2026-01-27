-- V2026.01.27.01__add_config_to_flow_connector_env.sql
-- 为 flow_connector_env 表添加 config 字段，用于存储动作配置

-- 添加 config 字段
ALTER TABLE flow_connector_env
ADD COLUMN IF NOT EXISTS config TEXT;

-- 添加字段注释
COMMENT ON COLUMN flow_connector_env.config IS '动作配置（JSON格式），包含 actions（列表字段）和 actionDetails（详细配置）';

-- 注意：未创建索引，原因：
-- 1. config 字段为 TEXT 类型，主要用于存储，不用于 WHERE 条件查询
-- 2. 项目中类似的 JSON 字段（auth_config、extra_config）均未创建索引
-- 3. 如果未来有 JSON 内容查询需求，可考虑添加 GIN 索引
