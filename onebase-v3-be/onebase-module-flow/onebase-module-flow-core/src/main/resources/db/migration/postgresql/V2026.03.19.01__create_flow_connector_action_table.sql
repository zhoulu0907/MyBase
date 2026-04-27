-- 统一动作表
-- 用于存储所有连接器类型的动作配置，通过 connector_type 区分类型
CREATE TABLE flow_connector_action (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL,

    -- 关联字段
    connector_uuid VARCHAR(64) NOT NULL,        -- 所属连接器UUID
    connector_type VARCHAR(50) NOT NULL,        -- 连接器类型(HTTP/SCRIPT/...)

    -- 动作基本信息
    action_uuid VARCHAR(64) NOT NULL UNIQUE,    -- 动作唯一标识
    action_code VARCHAR(100),                   -- 动作编码
    action_name VARCHAR(100) NOT NULL,          -- 动作名称
    description VARCHAR(500),                   -- 动作描述

    -- 输入输出定义
    input_schema TEXT,                          -- 输入参数Schema(JSON)
    output_schema TEXT,                         -- 输出参数Schema(JSON)

    -- 扩展配置（混合模式：标准化字段 + 类型特有配置）
    action_config TEXT,                         -- 类型特有配置(JSON)

    -- 状态
    active_status INTEGER DEFAULT 1,            -- 启用状态(0-禁用,1-启用)
    sort_order INTEGER DEFAULT 0,               -- 排序

    -- 审计字段
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    creator BIGINT,
    updater BIGINT,
    deleted INTEGER DEFAULT 0,
    tenant_id BIGINT
);

-- 索引
CREATE INDEX idx_fca_connector_uuid ON flow_connector_action(connector_uuid);
CREATE INDEX idx_fca_connector_type ON flow_connector_action(connector_type);
CREATE INDEX idx_fca_action_code ON flow_connector_action(action_code);
CREATE INDEX idx_fca_active_status ON flow_connector_action(active_status);
CREATE INDEX idx_fca_action_uuid ON flow_connector_action(action_uuid);

-- 表注释
COMMENT ON TABLE flow_connector_action IS '统一动作配置表';
COMMENT ON COLUMN flow_connector_action.id IS '主键ID';
COMMENT ON COLUMN flow_connector_action.application_id IS '应用ID';
COMMENT ON COLUMN flow_connector_action.connector_uuid IS '所属连接器UUID';
COMMENT ON COLUMN flow_connector_action.connector_type IS '连接器类型(HTTP/SCRIPT/...)';
COMMENT ON COLUMN flow_connector_action.action_uuid IS '动作唯一标识';
COMMENT ON COLUMN flow_connector_action.action_code IS '动作编码';
COMMENT ON COLUMN flow_connector_action.action_name IS '动作名称';
COMMENT ON COLUMN flow_connector_action.description IS '动作描述';
COMMENT ON COLUMN flow_connector_action.input_schema IS '输入参数Schema(JSON)';
COMMENT ON COLUMN flow_connector_action.output_schema IS '输出参数Schema(JSON)';
COMMENT ON COLUMN flow_connector_action.action_config IS '类型特有配置(JSON)，混合模式：标准化字段(timeout/retryCount/mockResponse) + 类型特有配置';
COMMENT ON COLUMN flow_connector_action.active_status IS '启用状态(0-禁用,1-启用)';
COMMENT ON COLUMN flow_connector_action.sort_order IS '排序';
COMMENT ON COLUMN flow_connector_action.create_time IS '创建时间';
COMMENT ON COLUMN flow_connector_action.update_time IS '更新时间';
COMMENT ON COLUMN flow_connector_action.creator IS '创建人';
COMMENT ON COLUMN flow_connector_action.updater IS '更新人';
COMMENT ON COLUMN flow_connector_action.deleted IS '删除标志';
COMMENT ON COLUMN flow_connector_action.tenant_id IS '租户ID';