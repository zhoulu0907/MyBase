-- 修复 flow_connector 表缺少 code 列的问题
-- 添加 code 字段用于存储连接器编码
-- 日期: 2026-01-17

-- PostgreSQL 版本
ALTER TABLE flow_connector ADD COLUMN code VARCHAR(64);

COMMENT ON COLUMN flow_connector.code IS '连接器编码';

-- 添加索引以提高查询性能
CREATE INDEX idx_flow_connector_code ON flow_connector(code);
