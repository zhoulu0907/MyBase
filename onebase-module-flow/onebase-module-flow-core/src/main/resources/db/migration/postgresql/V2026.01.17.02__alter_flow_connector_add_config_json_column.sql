-- 修复 flow_connector 表缺少 config_json 列的问题
-- 添加 config_json 字段用于存储JSON格式的配置
-- 日期: 2026-01-17

-- PostgreSQL 版本
ALTER TABLE flow_connector ADD COLUMN IF NOT EXISTS config_json TEXT;

COMMENT ON COLUMN flow_connector.config_json IS '连接器配置（JSON格式）';
